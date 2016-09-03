package shocid.imputation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.TrainingSetUtil;
import org.encog.util.obj.SerializeObject;

import shocid.ffann.training.FFANNGeneticAlgorithmJSP;

public class ANNPreparationJSP extends Thread 
{
	public static String imputeFilePath;
	public static int[] influencingColumns; //array for holding the columns that have influence on the missing column value
	public static int imputeColumn;
	public static int numberInputNeurons;
	public static String targetTraining;
	public static String targetBase;
	public static String network;
	public static ArrayList<String> replacedDatasets=new ArrayList<String>();
	public static ArrayList<String> completeDatasets=new ArrayList<String>();
	public static ArrayList<String> incompleteDatasets=new ArrayList<String>();
	public static ArrayList<String> trainingDatasets=new ArrayList<String>();
	private static double allowedError = 0.0;
	private static String outFile;
	

	public static void prepare() throws Throwable
	{

		//Logging.stopConsoleLogging();

//		//  prompt to enter the path to the input file
//		System.out.print("Enter path and name to the file to be imputed: ");
//		BufferedReader brinf = new BufferedReader(new InputStreamReader(System.in));
//		String inPath = null;
//		try
//		{
//			inPath = brinf.readLine();
//			System.out.println("Reading file...");
//
//			//set the path to the input file containing input and output neurons values
//			setImputeFilePath(inPath);
//		}
//		catch (IOException ioe)
//		{
//			System.out.println("IO error trying to read the path to the input file.");
//			System.exit(1);
//		}
//
//
//		//  prompt to enter the influencing columns
//		System.out.print("Enter the number of columns that influence the missing column (e.g.: 1,2): ");
//		BufferedReader brmc = new BufferedReader(new InputStreamReader(System.in));
//		String mc = null;
//		try
//		{
//			mc = brmc.readLine();
//			System.out.println("Reading file...");
//
//			//set the path to the input file containing input and output neurons values
//			setInfluencingColumns(mc);
//		}
//		catch (IOException ioe)
//		{
//			System.out.println("IO error trying to read the path to the input file.");
//			System.exit(1);
//		}
//
//
//		//  prompt to enter the missing column to impute TODO: do that automatically by returning the position of NaN
//		System.out.print("Enter the number of the column to impute: ");
//		BufferedReader bric = new BufferedReader(new InputStreamReader(System.in));
//		String ic = null;
//		try
//		{
//			ic = bric.readLine();
//			System.out.println("Reading file...");
//
//			//set the path to the input file containing input and output neurons values
//			setImputeColumn(ic);
//		}
//		catch (IOException ioe)
//		{
//			System.out.println("IO error trying to read the path to the input file.");
//			System.exit(1);
//		}

		//String fileName="C:\\Users\\fneukart\\workspace\\encog-java-core-2.5.3\\imputation\\norm_impute.csv";
		ANNPreparationJSP x=new ANNPreparationJSP(getImputeFilePath());

		String targetReplace = "C:\\Users\\Florian Neukart\\workspace\\SHOCID\\imputation\\norm_impute_prepared.csv";
		ANNImputation.setWholePath(targetReplace);
		FileReader frWR = new FileReader(getImputeFilePath());
		BufferedReader brWR = new BufferedReader(frWR);
		x.writeAndReplace(brWR, targetReplace);
		brWR.close();
		frWR.close();

		String targetComplete = "C:\\Users\\Florian Neukart\\workspace\\SHOCID\\imputation\\norm_impute_complete.csv";
		FileReader frC = new FileReader(targetReplace);
		BufferedReader brC = new BufferedReader(frC);
		x.ReadCompleteDataSets(targetComplete, brC);
		brC.close();
		frC.close();

		String targetIncomplete = "C:\\Users\\Florian Neukart\\workspace\\SHOCID\\imputation\\norm_impute_incomplete.csv";
		ANNImputation.setIncompletePath(targetIncomplete);
		FileReader frI = new FileReader(targetReplace);
		BufferedReader brI = new BufferedReader(frI);
		x.ReadIncompleteDataSets(targetIncomplete, brI);
		brI.close();
		frI.close();

		String targetTraining = "C:\\Users\\Florian Neukart\\workspace\\SHOCID\\imputation\\norm_impute_training.csv";
		FileReader frT = new FileReader(targetComplete);
		BufferedReader brT = new BufferedReader(frT);
		x.writeTrainingFile(influencingColumns, getImputeColumn(), brT, targetTraining);
		brWR.close();
		frWR.close();

		String targetBase = "C:\\Users\\Florian Neukart\\workspace\\SHOCID\\imputation\\baseFile.csv";
		FileReader frB = new FileReader(targetIncomplete);
		BufferedReader brB = new BufferedReader(frB);
		x.writeBaseFile(influencingColumns, getImputeColumn(), brB, targetBase, targetIncomplete);
		brB.close();
		frB.close();

		setNumberInputNeurons(influencingColumns.length);
		setTargetTrainingFile(targetTraining);
		setTargetBaseFile(targetBase);
		setImputationNetwork("C:\\Users\\Florian Neukart\\workspace\\SHOCID\\imputation\\imputation_FFANNGA.net");

		prepareImputation(getNumberInputNeurons(),1,getTargetTrainingFile(),true, getTargetBaseFile());
		
		ANNImputationJSP.preparation2();
	}

	static String fileName;

	ArrayList <String>storeValues = new ArrayList<String>();

	public ANNPreparationJSP(String FileName)
	{
		ANNPreparationJSP.fileName=FileName;
	}

	public ANNPreparationJSP()
	{

	}
	
	public ANNPreparationJSP(String inPath, String columnsInfluence, String imputationColumn, double allowedError, String outFile)
	{
		setImputeFilePath(inPath);
		setInfluencingColumns(columnsInfluence);
		setImputeColumn(imputationColumn);
		setAllowedError(allowedError);
		setOutFile(outFile);
	}

	public void ReadCompleteDataSets(String fileNameTarget, BufferedReader br)
	{
		try {
			//storeValues.clear();/


			StringTokenizer st = null;

			FileWriter writer = new FileWriter(fileNameTarget);

			int lineNumber = 0, tokenNumber = 0;

			String currentToken = null;

			System.out.println("Complete Datasets: \n");

			while((fileName = br.readLine()) != null)
			{
				lineNumber++;
				//System.out.println(fileName);
				storeValues.add(fileName);
				//break comma separated line using ","
				st = new StringTokenizer(fileName, ",");

				if(!fileName.contains("NaN"))
				{
					while(st.hasMoreTokens())
					{
						if (tokenNumber > 0)
						{
							writer.append(",");
						}
						currentToken = st.nextToken();

						System.out.println("Line # " + lineNumber + 
								", Token # " + tokenNumber 
								+ ", Token : "+ currentToken);

						//for the GUI
						completeDatasets.add("Line # " + lineNumber + 
								", Token # " + tokenNumber 
								+ ", Token : "+ currentToken);
						tokenNumber++;
						writer.append(currentToken);
					}
					writer.append("\n");
				}
				//reset token number
				tokenNumber = 0;
			}
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		storeValues.clear();
	}

	public Runnable ReadIncompleteDataSets(String fileNameTarget, BufferedReader br)
	{
		try {

			//FileReader frInc = new FileReader(fileName);
			//BufferedReader brInc = new BufferedReader(frInc);

			StringTokenizer st = null;

			FileWriter writer = new FileWriter(fileNameTarget);

			int lineNumber = 0, tokenNumber = 0;

			String currentToken = null;

			System.out.println("Incomplete Datasets: \n");

			while((fileName = br.readLine()) != null)
			{
				lineNumber++;
				//System.out.println(fileName);
				storeValues.add(fileName);
				//break comma separated line using ","
				st = new StringTokenizer(fileName, ",");

				if(fileName.contains("NaN"))
				{
					while(st.hasMoreTokens())
					{
						if (tokenNumber > 0)
						{
							writer.append(",");
						}
						currentToken = st.nextToken();

						System.out.println("Line # " + lineNumber + 
								", Token # " + tokenNumber 
								+ ", Token : "+ currentToken);
						
						//for the GUI
						incompleteDatasets.add("Line # " + lineNumber + 
								", Token # " + tokenNumber 
								+ ", Token : "+ currentToken);

						tokenNumber++;
						writer.append(currentToken);
					}
					writer.append("\n");
				}
				//reset token number
				tokenNumber = 0;
			}
			writer.flush();
			writer.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		storeValues.clear();
		return null;
	}


	public void writeAndReplace(BufferedReader br, String fileNameTarget)
	{
		try {
			//storeValues.clear();

			StringTokenizer st = null;

			int lineNumber = 0, tokenNumber = 0;

			FileWriter writer = new FileWriter(fileNameTarget);

			String currentToken = null;

			System.out.println("Replaced Datasets: \n");

			while((fileName = br.readLine()) != null)
			{

				lineNumber++;
				//System.out.println(fileName);
				//fileName.replace(",,", ",NaN,");
				storeValues.add(fileName);
				//break comma separated line using ","
				st = new StringTokenizer(fileName.replace(",,", ",NaN,"), ",");

				while(st.hasMoreTokens())
				{
					if (tokenNumber > 0)
					{
						writer.append(",");	
					}

					currentToken = st.nextToken();
					System.out.println("Line # " + lineNumber + 
							", Token # " + tokenNumber 
							+ ", Token : "+ currentToken);
					
					//for the GUI
					replacedDatasets.add("Line # " + lineNumber + 
							", Token # " + tokenNumber 
							+ ", Token : "+ currentToken);

					tokenNumber++;
					writer.append(currentToken);
				}

				//reset token number
				tokenNumber = 0;
				writer.append("\n");
			}

			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeTrainingFile(int[] influencingColumns, int missingColumn, BufferedReader br, String fileNameTarget)
	{
		try {
			//storeValues.clear();

			StringTokenizer st = null;

			int lineNumber = 0, tokenNumber = 0;

			FileWriter writer = new FileWriter(fileNameTarget);

			String currentToken = null;

			String missingColumnValue = null;

			System.out.println("Training Datasets: \n");

			//write out the input neurons
			while((fileName = br.readLine()) != null)
			{

				lineNumber++;
				//System.out.println(fileName);
				//fileName.replace(",,", ",NaN,");
				storeValues.add(fileName);
				//break comma separated line using ","
				st = new StringTokenizer(fileName, ",");
				int firstTrainingToken = 0;

				while(st.hasMoreTokens())
				{
					currentToken = st.nextToken();

					for(int i = 0; i < influencingColumns.length; i++)
					{
						if (tokenNumber == influencingColumns[i])
						{
							if (firstTrainingToken > 0)
							{
								if (tokenNumber > 0)
								{
									writer.append(",");	
								}	
							}

							firstTrainingToken++;

							writer.append(currentToken);
							System.out.println("Line # " + lineNumber + 
									", Token # " + tokenNumber 
									+ ", Token : "+ currentToken);
							
							//for the GUI
							trainingDatasets.add("Line # " + lineNumber + 
									", Token # " + tokenNumber 
									+ ", Token : "+ currentToken);
						}
					}

					if (tokenNumber == missingColumn)
					{
						missingColumnValue = currentToken;
					}

					tokenNumber++;
				}

				//write the desired output value
				writer.append(","+missingColumnValue);

				//reset token number
				tokenNumber = 0;
				writer.append("\n");
			}
			writer.flush();
			writer.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void writeBaseFile(int[] influencingColumns, int missingColumn, BufferedReader br, String fileNameTarget, String targetIncomplete)
	{
		try {
			//storeValues.clear();

			StringTokenizer st = null;

			int lineNumber = 0, tokenNumber = 0;

			FileWriter writer = new FileWriter(fileNameTarget);

			String currentToken = null;

			System.out.println("Training Datasets: \n");

			//write out the input neurons
			while((targetIncomplete = br.readLine()) != null)
			{

				lineNumber++;
				//System.out.println(fileName);
				//fileName.replace(",,", ",NaN,");
				storeValues.add(targetIncomplete);
				//break comma separated line using ","
				st = new StringTokenizer(targetIncomplete, ",");
				int firstTrainingToken = 0;

				while(st.hasMoreTokens())
				{
					currentToken = st.nextToken();

					for(int i = 0; i < influencingColumns.length; i++)
					{
						if (tokenNumber == influencingColumns[i])
						{
							if (firstTrainingToken > 0)
							{
								if (tokenNumber > 0)
								{
									writer.append(",");	
								}	
							}

							firstTrainingToken++;

							writer.append(currentToken);
							System.out.println("Line # " + lineNumber + 
									", Token # " + tokenNumber 
									+ ", Token : "+ currentToken);
						}
					}
					tokenNumber++;
				}

				//reset token number
				tokenNumber = 0;
				
				//append dummy output value (2) for being able to load the CSV into a Neural DataSet  
				writer.append(",2");
				writer.append("\n");
			}
			writer.flush();
			writer.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void prepareImputation(int numberInputNeurons, int numberOutputNeurons, String trainingFile, boolean networkOnlyPositiveInput, String baseFilePath)
	{
		int numberHiddenNeurons;
		float floatNumberInputNeurons;
		float floatNumberOutputNeurons;
		float floatNumberHiddenNeurons;

		floatNumberInputNeurons = Float.valueOf(numberInputNeurons).floatValue();
		floatNumberOutputNeurons = Float.valueOf(numberOutputNeurons).floatValue();
		floatNumberHiddenNeurons = ((floatNumberInputNeurons / 3) * 2) + floatNumberOutputNeurons;
		numberHiddenNeurons = Math.round(floatNumberHiddenNeurons);

		BasicNetwork network = new BasicNetwork();
		MLDataSet trainingSet;

		trainingSet = TrainingSetUtil.loadCSVTOMemory(CSVFormat.ENGLISH, trainingFile, false, numberInputNeurons, numberOutputNeurons);

		//for value imputation genetic learning should be applied, as although the error rate must be pretty low a minimum of training iterations should be carried out 
		FFANNGeneticAlgorithmJSP ffannga = new FFANNGeneticAlgorithmJSP();

		//with true the network is saved under the imputationFilePath (see genetic training class)
		boolean askForSave = true;
		ffannga.run(network, numberInputNeurons, numberHiddenNeurons, numberOutputNeurons, trainingSet, networkOnlyPositiveInput, getAllowedError(), askForSave, "y");
	}

	public static BasicNetwork openNetwork(String imputationNetworkPath)
	{
		File loadFile = new File (imputationNetworkPath);
		BasicNetwork imputationNetwork = null;
		try {
			imputationNetwork=(BasicNetwork)SerializeObject.load(loadFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imputationNetwork;
	}

	public void run()
	{
		try {
			prepare();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void setImputeFilePath(String filePath)
	{
		imputeFilePath=filePath;
	}

	public static String getImputeFilePath()
	{
		return imputeFilePath;
	}

	public static void setInfluencingColumns(String columns)
	{
		String [] influencingColumnsString;
		influencingColumnsString = new String[columns.split(",").length];
		influencingColumnsString = columns.split(",");

		influencingColumns = new int[columns.split(",").length];

		for (int i = 0; i < influencingColumns.length; i++)
		{
			influencingColumns[i] = Integer.valueOf(influencingColumnsString[i]).intValue();
		}
	}

	public static void setImputeColumn(String column)
	{
		imputeColumn=Integer.valueOf(column).intValue();
	}

	public static int getImputeColumn()
	{
		return imputeColumn;
	}

	public ArrayList getFileValues()
	{
		return this.storeValues;
	}

	public void displayArrayList()
	{
		for(int x=0;x<this.storeValues.size();x++)
		{
			System.out.println(storeValues.get(x));
		}
	}

	public static void setNumberInputNeurons(int neurons)
	{
		numberInputNeurons = neurons;
	}

	public static int getNumberInputNeurons()
	{

		return numberInputNeurons;
	}

	public static void setTargetTrainingFile(String targetTrainingFile)
	{
		targetTraining = targetTrainingFile;
	}

	public static String getTargetTrainingFile()
	{
		return targetTraining;
	}

	public static void setTargetBaseFile(String targetBaseFile)
	{
		targetBase = targetBaseFile;
	}

	public static String getTargetBaseFile()
	{
		return targetBase;
	}

	public static void setImputationNetwork(String networkPath)
	{
		network = networkPath;
	}

	public static String getImputationNetwork()
	{
		return network;
	}
	
	public static ArrayList<String> getCompleteDatasets()
	{
		return completeDatasets;
	}
	
	public static ArrayList<String> getIncompleteDatasets()
	{
		return incompleteDatasets;
	}
	
	public static ArrayList<String> getTrainingDatasets()
	{
		return trainingDatasets;
	}
	
	public static ArrayList<String> getReplacedDatasets()
	{
		return replacedDatasets;
	}
	
	private static void setAllowedError(double e)
	{
		allowedError = e;
	}
	
	public static double getAllowedError()
	{
		return allowedError;
	}
	
	private static void setOutFile(String file)
	{
		outFile = file;
	}
	
	public static String getOutFile()
	{
		return outFile;
	}
}