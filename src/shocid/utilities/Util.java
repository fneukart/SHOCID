package shocid.utilities;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.encog.mathutil.error.ErrorCalculationMode;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.rbf.RBFNetwork;
import org.encog.util.obj.SerializeObject;

import shocid.hoann.AdaptiveHybridHOANNCommitteeJSP;
import shocid.hoann.HOANNCommitteeJSP;
import shocid.normalization.NormalizeCSV2CSV;

public class Util {
	public static String dateTime = null;
	static String saveDirectory = null;
	private static int numberInputNeurons = 0;
	private static int numberHiddenNeurons = 0;
	private static int numberOutputNeurons = 0;
	private static BasicNetwork[] networks = null;
	private static double[] qualityCalculationArray;
	public static ErrorCalculationMode mode = null;
	public static File inFile;
	public static File outFile;
	public static String outFilePath;
	public static String savePath;
	public static String networkName;
	public static String networkNameWithoutExtension;
	public static File temporaryNetwork;
	public static int taskType;
	public static String trainingMethod;
	private static double [][] inputValuesArray;
	private static double [][] inputValuesArrayDenormalized;
	private static double [][] idealValuesArray;
	private static double [][] 	idealValuesArrayDenormalized;
	private static double [][] outputValuesArray;
	private static File temporaryInputFile;
	private static File temporaryNormalizedInputFile;
	private static int normalization = 0;

	public static void writeSingleAgentInfo(String fileName, String networkName, int numberInputNeurons, int numberOutputNeurons) throws IOException
	{
		FileWriter agentInfo = new FileWriter(getSaveDirectory()+fileName);

		BufferedWriter agentFile = null;

		agentFile = new BufferedWriter(agentInfo);

		//write the agent information
		agentFile.write(getSaveDirectory()+networkName);
		agentFile.newLine();
		agentFile.append(String.valueOf(numberInputNeurons));
		agentFile.newLine();
		agentFile.append(String.valueOf(numberOutputNeurons));

		//close the file
		agentFile.close();
		//SerializeObject.save(getSaveDirectory()+name, agentInfo);
		System.out.println("SaveDirectory: "+getSaveDirectory()+"\n");
		System.out.println("FileName: "+agentInfo);

	}

	//	public static void insertSingleAgentNetwork(BasicNetwork network, String comment)
	public static void insertSingleAgentNetwork(String comment)
	{
		System.out.println("Inserting values in Mysql database table!");
		Connection con = null;
		String url = "jdbc:mysql://localhost:3306/";
		String db = "shocid";
		String driver = "com.mysql.jdbc.Driver";
		PreparedStatement insertNetworks = null;
		PreparedStatement insertResults = null;
		FileInputStream nwStream;
		int nwID = 0;
		try
		{
			Class.forName(driver);
			con = DriverManager.getConnection(url+db,"shocid","shocid");

			//===============================get highest network_id=======================================
			String nwIDQuery = "select max(network_id) network_id from networks";
			Statement nwIDStatement = con.createStatement();
			ResultSet nwIDRs = nwIDStatement.executeQuery(nwIDQuery);

			while (nwIDRs.next())
			{
				nwID = nwIDRs.getInt("network_id")+1;
				System.out.println("ID: "+nwID);
			}

			//===============================insert networks=======================================
			try
			{
				insertNetworks = con.prepareStatement("insert into networks(network_id,network_name,input_neurons,hidden_neurons,output_neurons,network,creation_date,training_method,task_type,comment)"
						+"values(?,?,?,?,?,?,?,?,?,?)");
				insertNetworks.setInt(1, nwID);
				insertNetworks.setString(2, getNetworkNameWithoutExtension()+".net");
				insertNetworks.setInt(3, getNumberInputNeurons());
				insertNetworks.setInt(4, getNumberHiddenNeurons());
				insertNetworks.setInt(5, getNumberOutputNeurons());

				nwStream = new FileInputStream(getTemporaryNetwork());

				insertNetworks.setBinaryStream(6, (InputStream)nwStream, (int)(getTemporaryNetwork().length()));
				insertNetworks.setString(7, getSetDateTime());
				insertNetworks.setString(8, getTrainingMethod());
				insertNetworks.setInt(9, getTaskType());
				insertNetworks.setString(10, comment);

				int success = insertNetworks.executeUpdate();

				if (success > 0)
				{
					//					System.out.println("Upload successful");
					//					System.out.println("1 row affected");
				}
				else
				{
					System.out.println("There occured an error in the storage process. Please contact Mr. Bombastic (aka known as Admin).");
				}
			}
			catch (SQLException s)
			{
				System.out.println("SQL statement is not executed!");
			}

			//===============================insert results=======================================
			try
			{
				insertResults = con.prepareStatement("insert into training_results(network_id,input_values,actual_output_values,ideal_output_values,input_values_denormalized,actual_output_values_denormalized,ideal_output_values_denormalized)"
						+"values(?,?,?,?,?,?,?)");

				for (int i = 0; i < getInputValues().length; i++)
				{
					String inputValues = "";
					String idealValues = "";
					String outputValues = "";
					String denormalizedInputValues = "";
					String denormalizedIdealValues = "";
					String denormalizedOutputValues = "";

					//only if some sort of normalization has been applied, the values will be written
					if (getNormalization() == 1)
					{
						for (int in = 0; in < getNumberInputNeurons(); in++)
						{
							if (in == 0)
							{
								denormalizedInputValues = denormalizedInputValues+String.valueOf(getInputValuesDenormalized()[i][in]).toString();
							}
							else
							{
								denormalizedInputValues = denormalizedInputValues+","+String.valueOf(getInputValuesDenormalized()[i][in]).toString();
							}
						}

						for (int ou = 0; ou < getNumberOutputNeurons(); ou++)
						{
							if (ou == 0)
							{
								denormalizedIdealValues = denormalizedIdealValues+String.valueOf(getIdealValuesDenormalized()[i][ou]).toString();
								//TODO: denormalize the output
								//denormalizedOutputValues = outputValues+String.valueOf(getOutputValues()[i][ou]).toString();
							}
							else
							{
								denormalizedIdealValues = denormalizedIdealValues+","+String.valueOf(getIdealValuesDenormalized()[i][ou]).toString();
								//denormalizedOutputValues = outputValues+","+String.valueOf(getOutputValues()[i][ou]).toString();
							}
						}
					}

					//happens anyway, regardless the normalization
					for (int in = 0; in < getNumberInputNeurons(); in++)
					{
						if (in == 0)
						{
							inputValues = inputValues+String.valueOf(getInputValues()[i][in]).toString();
						}
						else
						{
							inputValues = inputValues+","+String.valueOf(getInputValues()[i][in]).toString();
						}
					}

					for (int ou = 0; ou < getNumberOutputNeurons(); ou++)
					{
						if (ou == 0)
						{
							idealValues = idealValues+String.valueOf(getIdealValues()[i][ou]).toString();
							outputValues = outputValues+String.valueOf(getOutputValues()[i][ou]).toString();
						}
						else
						{
							idealValues = idealValues+","+String.valueOf(getIdealValues()[i][ou]).toString();
							outputValues = outputValues+","+String.valueOf(getOutputValues()[i][ou]).toString();
						}
					}

					insertResults.setInt(1, nwID);
					insertResults.setString(2, inputValues);
					insertResults.setString(3, outputValues);
					insertResults.setString(4, idealValues);

					if (getNormalization() == 1)
					{
						insertResults.setString(5, denormalizedInputValues);
						insertResults.setString(6, outputValues);//TODO: denormalize the output values
						insertResults.setString(7, denormalizedIdealValues);
					}
					else
					{
						insertResults.setString(5, "");
						insertResults.setString(6, "");//TODO: denormalize the output values
						insertResults.setString(7, "");
					}
					int success = insertResults.executeUpdate();
					if (success > 0)
					{
						//						System.out.println("Upload successful");
						//						System.out.println("1 row affected");
					}
					else
					{
						System.out.println("There occured an error in the storage process. Please contact Mr. Bombastic (aka known as Admin).");
					}
				}
			}
			catch (SQLException s)
			{
				s.printStackTrace();
				System.out.println("SQL statement is not executed!");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static String SaveFile(String fileName) 
	{
		String path = null;
		FileDialog fileDialog = new FileDialog(new Frame(), "Save", FileDialog.SAVE);
		fileDialog.setFilenameFilter(new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				return name.endsWith(".txt");
			}
		});

		fileDialog.setFile(fileName);
		fileDialog.setVisible(true);
		path = fileDialog.getDirectory();
		//System.out.println("File: " + path+fileDialog.getFile());

		return path+fileDialog.getFile();
	}

	public static void writeCommitteeInfo(String fileName, String savePath, int numberInputNeurons, int numberOutputNeurons) throws IOException
	{
		FileWriter agentInfo = new FileWriter(fileName);

		BufferedWriter agentFile = null;

		agentFile = new BufferedWriter(agentInfo);

		//write the agent information
		agentFile.write(String.valueOf(HOANNCommitteeJSP.trainedCommittee.length));
		agentFile.newLine();

		for (int i=0; i< HOANNCommitteeJSP.trainedCommittee.length; i++)
		{
			try {
				agentFile.write(savePath+HOANNCommitteeJSP.getTrainedCommitteeMemberNames()[i]+".net");
				agentFile.newLine();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		agentFile.append(String.valueOf(numberInputNeurons));
		agentFile.newLine();
		agentFile.append(String.valueOf(numberOutputNeurons));

		//close the file
		agentFile.close();
	}

	public static void writeAdaptiveHybridCommitteeInfo(String fileName, String savePath, int numberInputNeurons, int numberOutputNeurons) throws IOException
	{
		FileWriter agentInfo = new FileWriter(fileName);

		BufferedWriter agentFile = null;

		agentFile = new BufferedWriter(agentInfo);

		//write the agent information
		agentFile.write(String.valueOf(AdaptiveHybridHOANNCommitteeJSP.trainedCommittee.length));
		agentFile.newLine();

		for (int i=0; i< AdaptiveHybridHOANNCommitteeJSP.trainedCommittee.length; i++)
		{
			try {
				agentFile.write(savePath+AdaptiveHybridHOANNCommitteeJSP.getTrainedCommitteeMemberNames()[i]+".net");
				agentFile.newLine();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		agentFile.append(String.valueOf(numberInputNeurons));
		agentFile.newLine();
		agentFile.append(String.valueOf(numberOutputNeurons));

		//close the file
		agentFile.close();
	}

	public static BasicNetwork loadSingleAgent(String agentInfoFile) throws IOException, ClassNotFoundException
	{
		BasicNetwork network = new BasicNetwork();

		{
			int lineNumber = 0;
			HashMap<String, String> agentInfo = new HashMap<String, String>();
			try
			{
				// open agent info file
				FileInputStream fstream = new FileInputStream(agentInfoFile);

				// get object from DataInputStream
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;

				//read file
				while ((strLine = br.readLine()) != null)
				{
					switch(lineNumber)
					{
					case 0:
						agentInfo.put("pathAndName", strLine);
					case 1:
						agentInfo.put("numberInputNeurons", strLine);
					case 2:
						agentInfo.put("numberOutputNeurons", strLine);
					}
					lineNumber = lineNumber+1;
				}
				//Close the input stream
				in.close();
			}
			catch (Exception e){//Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}

			//			File loadFile = new File("pathAndName");
			//			(BasicNetwork)SerializeObject.load(loadFile);
			network = (BasicNetwork) SerializeObject.load(new File(agentInfo.get("pathAndName")));
			setNumberInputNeurons(Integer.valueOf(agentInfo.get("numberInputNeurons")).intValue());
			setNumberOutputNeurons(Integer.valueOf(agentInfo.get("numberOutputNeurons")).intValue());
			//			System.out.println("Name of agent: "+agentInfo.get("pathAndName"));
			//			System.out.println("Number inputNeurons: "+agentInfo.get("numberInputNeurons"));
			//			System.out.println("Number outputNeurons: "+agentInfo.get("numberOutputNeurons"));
		}
		return network;
	}

	public static RBFNetwork loadSingleAgentRBF(String agentInfoFile) throws IOException, ClassNotFoundException
	{
		RBFNetwork network = new RBFNetwork();

		{
			int lineNumber = 0;
			HashMap<String, String> agentInfo = new HashMap<String, String>();
			try
			{
				// open agent info file
				FileInputStream fstream = new FileInputStream(agentInfoFile);

				// get object from DataInputStream
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;

				//read file
				while ((strLine = br.readLine()) != null)
				{
					switch(lineNumber)
					{
					case 0:
						agentInfo.put("pathAndName", strLine);
					case 1:
						agentInfo.put("numberInputNeurons", strLine);
					case 2:
						agentInfo.put("numberOutputNeurons", strLine);
					}
					lineNumber = lineNumber+1;
				}
				//Close the input stream
				in.close();
			}
			catch (Exception e){//Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}

			File loadFile = new File("pathAndName");
			network = (RBFNetwork)SerializeObject.load(loadFile);
			setNumberInputNeurons(Integer.valueOf(agentInfo.get("numberInputNeurons")).intValue());
			setNumberOutputNeurons(Integer.valueOf(agentInfo.get("numberOutputNeurons")).intValue());
			//			System.out.println("Name of agent: "+agentInfo.get("pathAndName"));
			//			System.out.println("Number inputNeurons: "+agentInfo.get("numberInputNeurons"));
			//			System.out.println("Number outputNeurons: "+agentInfo.get("numberOutputNeurons"));
		}
		return network;
	}


	public static void loadCommitteeAgents(String agentInfoFile, int numberCommitteeMembers) throws IOException, ClassNotFoundException
	{
		networks = new BasicNetwork[numberCommitteeMembers];
		{
			int lineNumber = 0;
			HashMap<String, String> agentInfo = new HashMap<String, String>();
			try{
				// open agent info file
				FileInputStream fstream = new FileInputStream(agentInfoFile);

				// get object from DataInputStream
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;

				//read file
				while ((strLine = br.readLine()) != null)
				{
					if (numberCommitteeMembers == 2)
					{
						switch(lineNumber)
						{
						case 1:
							agentInfo.put(String.valueOf(lineNumber), strLine);
						case 2:
							agentInfo.put(String.valueOf(lineNumber), strLine);
						}
					}

					if (numberCommitteeMembers == 3)
					{
						switch(lineNumber)
						{
						case 1:
							agentInfo.put(String.valueOf(lineNumber), strLine);
						case 2:
							agentInfo.put(String.valueOf(lineNumber), strLine);
						case 3:
							agentInfo.put(String.valueOf(lineNumber), strLine);
						}
					}

					if (numberCommitteeMembers == 4)
					{
						switch(lineNumber)
						{
						case 1:
							agentInfo.put(String.valueOf(lineNumber), strLine);
						case 2:
							agentInfo.put(String.valueOf(lineNumber), strLine);
						case 3:
							agentInfo.put(String.valueOf(lineNumber), strLine);
						case 4:
							agentInfo.put(String.valueOf(lineNumber), strLine);
						}
					}

					if (numberCommitteeMembers == 5)
					{
						switch(lineNumber)
						{
						case 1:
							agentInfo.put(String.valueOf(lineNumber), strLine);
						case 2:
							agentInfo.put(String.valueOf(lineNumber), strLine);
						case 3:
							agentInfo.put(String.valueOf(lineNumber), strLine);
						case 4:
							agentInfo.put(String.valueOf(lineNumber), strLine);
						case 5:
							agentInfo.put(String.valueOf(lineNumber), strLine);
						}
					}
					lineNumber = lineNumber+1;
				}
				//Close the input stream
				in.close();
			}catch (Exception e){//Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}

			for (int i = 0; i < numberCommitteeMembers; i++)
			{
				File loadFile = new File(agentInfo.get(String.valueOf(i+1)));
				setCommitteeNetworks(i, (BasicNetwork)SerializeObject.load(loadFile));	
			}
		}
	}

	public static int numberCommitteeMembers(String agentInfoFile) throws IOException, ClassNotFoundException
	{
		int numberCommitteeMembers = 0;
		int lineNumber = 0;
		int startNeuronInformation = 0;

		try{
			// open agent info file
			FileInputStream fstream = new FileInputStream(agentInfoFile);

			// get object from DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			//read file
			while ((strLine = br.readLine()) != null)
			{
				switch(lineNumber)
				{
				case 0:
					numberCommitteeMembers = Integer.valueOf(strLine).intValue();
				}
				break;
				//lineNumber = lineNumber+1;
			}			

			if (numberCommitteeMembers == 2)
			{
				startNeuronInformation = 3;
			}
			else if (numberCommitteeMembers == 3)
			{
				startNeuronInformation = 4;
			}
			else if (numberCommitteeMembers == 4)
			{
				startNeuronInformation = 5;
			}
			else if (numberCommitteeMembers == 5)
			{
				startNeuronInformation = 6;
			}

			//Close the input stream
			in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

		lineNumber = 0;

		//extract the number of input and output neurons
		try{
			// open agent info file
			FileInputStream fstream = new FileInputStream(agentInfoFile);

			// get object from DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			//read file
			while ((strLine = br.readLine()) != null)
			{
				if (startNeuronInformation == 3)
				{
					switch(lineNumber)
					{
					case 3:
						setNumberInputNeurons(Integer.valueOf(strLine).intValue());
					case 4:
						setNumberOutputNeurons(Integer.valueOf(strLine).intValue());
						break;
					}
				}

				if (startNeuronInformation == 4)
				{
					switch(lineNumber)
					{
					case 4:
						setNumberInputNeurons(Integer.valueOf(strLine).intValue());
					case 5:
						setNumberOutputNeurons(Integer.valueOf(strLine).intValue());
						break;
					}
				}

				if (startNeuronInformation == 5)
				{
					switch(lineNumber)
					{
					case 5:
						setNumberInputNeurons(Integer.valueOf(strLine).intValue());
					case 6:
						setNumberOutputNeurons(Integer.valueOf(strLine).intValue());
						break;
					}
				}

				if (startNeuronInformation == 6)
				{
					switch(lineNumber)
					{
					case 6:
						setNumberInputNeurons(Integer.valueOf(strLine).intValue());
					case 7:
						setNumberOutputNeurons(Integer.valueOf(strLine).intValue());
						break;
					}
				}
				lineNumber = lineNumber+1;
			}
			//Close the input stream
			in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

		return numberCommitteeMembers;
	}

	public static int inputFileLineNumber(String sourceFile)
	{
		int lineCount = 0;

		try
		{
			// open agent info file
			FileInputStream fstream = new FileInputStream(sourceFile);

			// get object from DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			//read file
			while ((strLine = br.readLine()) != null)
			{
				lineCount = lineCount+1;
			}
			//Close the input stream
			in.close();
		}
		catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

		return lineCount;
	}

	public static double getANNQuality(BasicNetwork network, MLDataSet trainingSet)
	{
		defineQualityCalculationArray(trainingSet);
		//calculate the quality of the current ANN
		double quality = 0.0;
		int currentLine = 0;
		for(MLDataPair pair: trainingSet )
		{
			final MLData output = network.compute(pair.getInput());

			quality = (pair.getIdeal().getData(0) - output.getData(0));

			if (quality < 0.0)
			{
				quality*=-1;
			}

			//set the quality of the current output in the calculation array
			setQualityCalculationArray(currentLine, quality);
			currentLine+=1;
		}

		return calculateANNQuality(getQualityCalculationArray());
	}

	public static double calculateANNQuality(double[] quality)
	{
		double accuracy1 = 0.0;
		double accuracy2 = 0.0;
		double accuracy3 = 0.0;
		double accuracy4 = 0.0;
		double accuracy5 = 0.0;
		double accuracy6 = 0.0;
		double accuracy7 = 0.0;
		double accuracy8 = 0.0;
		double accuracy9 = 0.0;
		double accuracy10 = 0.0;
		int number1 = 0;
		int number2 = 0;
		int number3 = 0;
		int number4 = 0;
		int number5 = 0;
		int number6 = 0;
		int number7 = 0;
		int number8 = 0;
		int number9 = 0;
		int number10 = 0;

		double overallQuality = 0.0;

		for (int i = 0; i < quality.length; i++)
		{
			if (quality[i] >= 0.1)
			{
				accuracy1 = accuracy1 + quality[i];
				//number1 = number1 + 1;
			}

			if (quality[i] >= 0.01 && quality[i] < 0.1)
			{
				accuracy2 = accuracy2 + quality[i];
				number2 = number2 + 1;
			}

			if (quality[i] >= 0.001 && quality[i] < 0.01)
			{
				accuracy3 = accuracy3 + quality[i];
				number3 = number3 + 1;
			}

			if (quality[i] >= 0.0001 && quality[i] < 0.001)
			{
				accuracy4 = accuracy4 + quality[i];
				number4 = number4 + 1;
			}

			if (quality[i] >= 0.00001 && quality[i] < 0.0001)
			{
				accuracy5 = accuracy5 + quality[i];
				number5 = number5 + 1;
			}

			if (quality[i] >= 0.000001 && quality[i] < 0.00001)
			{
				accuracy6 = accuracy6 + quality[i];
				number6 = number6 + 1;
			}

			if (quality[i] >= 0.0000001 && quality[i] < 0.000001)
			{
				accuracy7 = accuracy7 + quality[i];
				number7 = number7 + 1;
			}

			if (quality[i] >= 0.00000001 && quality[i] < 0.0000001)
			{
				accuracy8 = accuracy8 + quality[i];
				number8 = number8 + 1;
			}

			if (quality[i] >= 0.000000001 && quality[i] < 0.00000001)
			{
				accuracy9 = accuracy9 + quality[i];
				number9 = number9 + 1;
			}

			if (quality[i] >= 0.0000000001 && quality[i] < 0.000000001)
			{
				accuracy10 = accuracy10 + quality[i];
				number10 = number10 + 1;
			}
		}

		//		if (number1 > 0)
		//		{
		//			accuracy1 = accuracy1;
		//		}

		if (number2 > 0)
		{
			accuracy2 = accuracy2 * 10;
		}

		if (number3 > 0)
		{
			accuracy3 = accuracy3 * 100;
		}

		if (number4 > 0)
		{
			accuracy4 = accuracy4 * 1000;
		}

		if (number5 > 0)
		{
			accuracy5 = accuracy5 * 10000;
		}

		if (number6 > 0)
		{
			accuracy6 = accuracy6 * 100000;
		}

		if (number7 > 0)
		{
			accuracy7 = accuracy7 * 1000000;
		}

		if (number8 > 0)
		{
			accuracy8 = accuracy8 * 10000000;
		}

		if (number9 > 0)
		{
			accuracy9 = accuracy9 * 100000000;
		}

		if (number10 > 0)
		{
			accuracy10 = accuracy10 * 1000000000;
		}

		overallQuality = accuracy1 + accuracy2 + accuracy3 + accuracy4 + accuracy5 + accuracy7 + accuracy8 + accuracy9 + accuracy10;

		return overallQuality;
	}

	public static double calculateANNQualityNew(double[] quality)
	{
		double accuracy1 = 0.0;
		double accuracy2 = 0.0;
		double accuracy3 = 0.0;
		double accuracy4 = 0.0;
		double accuracy5 = 0.0;
		double accuracy6 = 0.0;
		double accuracy7 = 0.0;
		double accuracy8 = 0.0;
		double accuracy9 = 0.0;
		double accuracy10 = 0.0;
		int number1 = 0;
		int number2 = 0;
		int number3 = 0;
		int number4 = 0;
		int number5 = 0;
		int number6 = 0;
		int number7 = 0;
		int number8 = 0;
		int number9 = 0;
		int number10 = 0;

		double overallQuality = 0.0;

		for (int i = 0; i < quality.length; i++)
		{
			if (quality[i] >= 0.1)
			{
				accuracy1 = accuracy1 + quality[i];
				number1 = number1 + 1;
			}

			if (quality[i] >= 0.01 && quality[i] < 0.1)
			{
				accuracy2 = accuracy2 + quality[i];
				number2 = number2 + 1;
			}

			if (quality[i] >= 0.001 && quality[i] < 0.01)
			{
				accuracy3 = accuracy3 + quality[i];
				number3 = number3 + 1;
			}

			if (quality[i] >= 0.0001 && quality[i] < 0.001)
			{
				accuracy4 = accuracy4 + quality[i];
				number4 = number4 + 1;
			}

			if (quality[i] >= 0.00001 && quality[i] < 0.0001)
			{
				accuracy5 = accuracy5 + quality[i];
				number5 = number5 + 1;
			}

			if (quality[i] >= 0.000001 && quality[i] < 0.00001)
			{
				accuracy6 = accuracy6 + quality[i];
				number6 = number6 + 1;
			}

			if (quality[i] >= 0.0000001 && quality[i] < 0.000001)
			{
				accuracy7 = accuracy7 + quality[i];
				number7 = number7 + 1;
			}

			if (quality[i] >= 0.00000001 && quality[i] < 0.0000001)
			{
				accuracy8 = accuracy8 + quality[i];
				number8 = number8 + 1;
			}

			if (quality[i] >= 0.000000001 && quality[i] < 0.00000001)
			{
				accuracy9 = accuracy9 + quality[i];
				number9 = number9 + 1;
			}

			if (quality[i] >= 0.0000000001 && quality[i] < 0.000000001)
			{
				accuracy10 = accuracy10 + quality[i];
				number10 = number10 + 1;
			}
		}

		if (number1 > 0)
		{
			accuracy1 = accuracy1 * 10;
		}

		if (number2 > 0)
		{
			accuracy2 = accuracy2 * 9;
		}

		if (number3 > 0)
		{
			accuracy3 = accuracy3 * 8;
		}

		if (number4 > 0)
		{
			accuracy4 = accuracy4 * 7;
		}

		if (number5 > 0)
		{
			accuracy5 = accuracy5 * 6;
		}

		if (number6 > 0)
		{
			accuracy6 = accuracy6 * 5;
		}

		if (number7 > 0)
		{
			accuracy7 = accuracy7 * 4;
		}

		if (number8 > 0)
		{
			accuracy8 = accuracy8 * 3;
		}

		if (number9 > 0)
		{
			accuracy9 = accuracy9 * 2;
		}

		if (number10 > 0)
		{
			accuracy10 = accuracy10 * 1;
		}

		overallQuality = (accuracy1 + accuracy2 + accuracy3 + accuracy4 + accuracy5 + accuracy7 + accuracy8 + accuracy9 + accuracy10);
		//TODO: implementation according to excel

		return overallQuality;
	}

	public static void setQualityCalculationArray(int lineNumber, double quality)
	{
		qualityCalculationArray[lineNumber] = quality;
	}

	public static void setSetDateTime(String dT)
	{
		dateTime = dT; 
	}

	public static String getSetDateTime()
	{
		return dateTime;
	}

	public static void setSaveDirectory(String sP)
	{
		saveDirectory = sP;
	}

	public static String getSaveDirectory()
	{
		return saveDirectory;
	}

	public static void setNumberInputNeurons(int neurons)
	{
		numberInputNeurons = neurons;
	}

	public static int getNumberInputNeurons()
	{
		return numberInputNeurons;
	}

	public static void setNumberHiddenNeurons(int neurons)
	{
		numberHiddenNeurons = neurons;
	}

	public static int getNumberHiddenNeurons()
	{
		return numberHiddenNeurons;
	}

	public static void setNumberOutputNeurons(int neurons)
	{
		numberOutputNeurons = neurons;
	}

	public static int getNumberOutputNeurons()
	{
		return numberOutputNeurons;
	}

	private static void setCommitteeNetworks(int committeeMember, BasicNetwork network)
	{
		networks[committeeMember] = network;
	}

	public static BasicNetwork getCommitteeNetworks(int committeeMember)
	{
		return networks[committeeMember];
	}

	public static void defineQualityCalculationArray(MLDataSet trainingSet)
	{
		int line = 0;
		for(@SuppressWarnings("unused") MLDataPair pair: trainingSet)
		{
			//necessary, as more than one input neurons are likely
			for (int i = 0; i < numberInputNeurons; i++)
			{
				//just a counter for setting line
			}
			line = line+1;
		}
		qualityCalculationArray = new double[line];
	}

	public static String normalize(String sourceNameAndPath, String normalizationType, String normOutFile)
	{
		String path = "C:\\Users\\Florian Neukart\\workspace\\SHOCID\\";
		File inputFile = new File(sourceNameAndPath);
		Util.setInputFile(inputFile);

		if (normalizationType.trim().equals("c2c"))
		{
			File outputFile = new File(path+normOutFile);

			//set the input file for normalization and the output file as result

			setOutputFile(outputFile);
			setOutputFilePath(path+normOutFile);

			//normalize the input file

			NormalizeCSV2CSV normalize = new NormalizeCSV2CSV();
			normalize.normalizeMultiplicative(getInputFile(), getOutputFile(), getNumberInputNeurons(), getNumberOutputNeurons());
		}		
		return getOutputFilePath();
	}

	public static double getMaxArrayValue(double[] numbers)
	{
		double maxValue = numbers[0];
		for(int i=1;i < numbers.length;i++)
		{
			if(numbers[i] > maxValue)
			{
				maxValue = numbers[i];
			}
		}
		return maxValue;
	}

	public static double getMinArrayValue(double[] numbers)
	{
		double minValue = numbers[0];
		for(int i=1;i<numbers.length;i++)
		{
			if(numbers[i] < minValue)
			{
				minValue = numbers[i];
			}
		}
		return minValue;
	}

	public static double[] getQualityCalculationArray()
	{
		return qualityCalculationArray;
	}

	public static void setErrorCalculationMode(ErrorCalculationMode m)
	{
		mode = m;
	}

	public static ErrorCalculationMode getErrorCalculationMode()
	{
		return mode;
	}

	public static void setInputFile(File inputFile)
	{
		inFile = inputFile;
	}

	public static File getInputFile()
	{
		return inFile;
	}

	public static void setOutputFile(File outputFile)
	{
		outFile = outputFile;
	}

	public static File getOutputFile()
	{
		return outFile;
	}

	private static void setOutputFilePath(String normOutFilePath)
	{
		outFilePath = normOutFilePath;
	}

	private static String getOutputFilePath()
	{
		return outFilePath;
	}

	public static void setNetworkNameWithoutExtension(String type)
	{
		networkNameWithoutExtension = type+Util.getSetDateTime()+"_";
		createTemporaryNetwork();
	}

	public static String getNetworkNameWithoutExtension()
	{
		return networkNameWithoutExtension;
	}

	public static void setNetworkSavePath(String path)
	{
		savePath = path;
	}

	public static String getNetworkSavePath()
	{
		return savePath;
	}

	public static void createTemporaryNetwork()
	{
		try
		{
			temporaryNetwork = File.createTempFile(getNetworkNameWithoutExtension(),".net");
		}
		catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

	public static void setTemporaryNetwork(BasicNetwork network)
	{
		try
		{
			SerializeObject.save(temporaryNetwork, network);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static File getTemporaryNetwork()
	{
		return temporaryNetwork;
	}

	public static void setTaskType(int type)
	{
		taskType = type;
	}

	private static int getTaskType()
	{
		return taskType;
	}

	public static void setTrainingMethod(String method)
	{
		trainingMethod = method;
	}

	private static String getTrainingMethod()
	{
		return trainingMethod;
	}

	public static void setInputValues(double [][] iv)
	{
		inputValuesArray = iv;
	}

	public static double[][] getInputValues()
	{
		return inputValuesArray;
	}

	public static void setInputValuesDenormalized(double [][] iv)
	{
		inputValuesArrayDenormalized = iv;
	}

	public static double[][] getInputValuesDenormalized()
	{
		return inputValuesArrayDenormalized;
	}

	public static void setIdealValues(double [][] iv)
	{
		idealValuesArray = iv;
	}

	public static double[][] getIdealValues()
	{
		return idealValuesArray;
	}

	public static void setIdealValuesDenormalized(double [][] iv)
	{
		idealValuesArrayDenormalized = iv;
	}

	public static double[][] getIdealValuesDenormalized()
	{
		return idealValuesArrayDenormalized;
	}

	public static void setOutputValues(double [][] ov)
	{
		outputValuesArray = ov;
	}

	public static double[][] getOutputValues()
	{
		return outputValuesArray;
	}

	public static void createTemporaryInputFile()
	{
		try
		{
			temporaryInputFile = File.createTempFile("temporaryInputFile",".txt");
		}
		catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

	public static void createTemporaryNormalizedInputFile()
	{
		try
		{
			temporaryNormalizedInputFile = File.createTempFile("temporaryNormalizedInputFile",".txt");
		}
		catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

	public static void setNormalization(int n)
	{
		normalization = n;
	}

	private static int getNormalization()
	{
		return normalization;
	}

}