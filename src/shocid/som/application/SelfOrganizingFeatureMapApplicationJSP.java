package shocid.som.application;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.csv.CSVFormat;
import org.encog.util.obj.SerializeObject;
import org.encog.util.simple.TrainingSetUtil;
import shocid.readFile.ReadWithScanner;
import shocid.utilities.Util;


public class SelfOrganizingFeatureMapApplicationJSP {

	Properties properties = new Properties();

	String propertiesFilePath = "C:\\Users\\Florian Neukart\\workspace\\SHOCID\\programobject.properties";

	static int epoch = 0;
	String brsavgyn = null;

	String savePath =null;
	String logPath = null;

	private static MLDataSet trainingData = null;
	private static BasicNetwork nw = null;
	private static BasicNetwork nwt = null;
	private static double[][] inputValuesArray = null;
	private static double[][] outputValuesArray = null;
	private static ArrayList<Double> errorArrayList = new ArrayList<Double>();
	private static double[] error = null;
	private static int[] inputToCluster;
	private static double[][] input;
	
	private static double SOMInput[][];

	public SelfOrganizingFeatureMapApplicationJSP()
	{
		//run(network, numberInputNeurons, numberHiddenNeurons, numberOutputNeurons, trainingSet);
	}

	public void run(String fileName, String sourceNameAndPath, String norm, String normalizationType, String normOutFile)
	{
		BufferedInputStream stream = null;

		try {
			stream = new BufferedInputStream(new FileInputStream(propertiesFilePath));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			properties.load(stream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			stream.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Util.setSetDateTime(getDateTime());
		logPath = properties.getProperty("application.logPath")+"LOG_sofm_"+Util.getSetDateTime()+".txt";
		
		//get the trained ANN
		savePath = properties.getProperty("application.saveSofmAgentPath");
		BasicNetwork network = new BasicNetwork();
		try {
			network = Util.loadSingleAgent(savePath+fileName);
			//network = loadSingleAgent(fileName);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//normalization Start
		
		if (norm.equals("y"))
		{
			sourceNameAndPath = Util.normalize(sourceNameAndPath, normalizationType, normOutFile);
		}
		//normalization End
				
		MLDataSet trainingSet = TrainingSetUtil.loadCSVTOMemory(CSVFormat.ENGLISH, sourceNameAndPath, false, Util.getNumberInputNeurons(), 0);
		
		setTrainingSet(trainingSet);
		String inputValues = null;

		// run the neural network
		System.out.println("Neural Network Results:");

		try
		{
			//array must be filled before the training set is being defined
			SOMInput = new double[ReadWithScanner.lineCount(sourceNameAndPath)][Util.getNumberInputNeurons()];
		}

		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try
		{
			inputToCluster = new int[ReadWithScanner.lineCount(sourceNameAndPath)];
			for (int i = 0; i < ReadWithScanner.lineCount(sourceNameAndPath); i++)
			{
				BasicNeuralData data = new BasicNeuralData(SOMInput[i]);
				System.out.println("Input "+i);
				//				for (int j = 0; j < numberInputNeurons; j++)
				//				{
				//					System.out.println(SOMInput[i][j]);	
				//				}
				setInputToCluster(i,network.winner(data));
				System.out.println("belongs to cluster " + network.winner(data));
			}
		}

		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setNetwork(network);

		int line = 0;

		for(@SuppressWarnings("unused") MLDataPair pair: trainingSet)
		{
			//necessary, as more than one input neurons are likely
			for (int i = 0; i < Util.getNumberInputNeurons(); i++)
			{
				//just a counter for setting line
			}
			line = line+1;
		}

		inputValuesArray = new double[line][Util.getNumberInputNeurons()];
		outputValuesArray = new double[line][Util.getNumberOutputNeurons()];
		int inputv = Util.getNumberInputNeurons();
		int outputv = Util.getNumberOutputNeurons();

		line=0;

		//create the logfile for the current training
		File logFile = new File(logPath+"logFile");

		FileWriter outFile = null;

		try {
			outFile = new FileWriter(logFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for(MLDataPair pair: trainingSet)
		{
			final MLData output = network.compute(pair.getInput());
			//System.out.println("Input Line " + line);
			//necessary, as more than one input neurons are likely
			for (int i = 0; i < Util.getNumberInputNeurons(); i++)
			{
				//System.out.println(pair.getInput().getData(i));
				setInputValues(line, i, pair.getInput().getData(i));
			}

			//necessary, as more than one output neurons are likely. the ideal output is the same for each expert, as this must not vary.
			for (int i = 0; i < Util.getNumberOutputNeurons(); i++)
			{
				inputValues = null;
				for (int iv = 0; iv < Util.getNumberInputNeurons(); iv++)
				{
					if (inputValues == null)
					{
						inputValues = String.valueOf(pair.getInput().getData(iv));	
					}
					else
					{
						inputValues = inputValues + ", " + inputValues + String.valueOf(pair.getInput().getData(iv)); 
					}
				}

				//set the resulting output values
				setOutputValues(line, i, output.getData(i));
			}
			line = line+1;
		}

		try {
			outFile.close();
			SerializeObject.save(logFile,logFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //closes the file

	}

	private String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public void setTrainingSet(MLDataSet trainingSet)
	{
		trainingData = trainingSet;
	}

	public static MLDataSet getTrainingSet()
	{
		return trainingData;
	}

	public void setNetwork(BasicNetwork network)
	{
		nw = network;
	}

	public static BasicNetwork getNetwork ()
	{
		return nw;
	}

	public void setInputValues(int row, int col, double value)
	{
		inputValuesArray[row][col] = value;
	}

	public static double[][] getInputValues()
	{
		return inputValuesArray;
	}

	public static double getInputValues(int row, int col)
	{
		return inputValuesArray[row][col];
	}

	public void setOutputValues(int row, int col, double value)
	{
		outputValuesArray[row][col] = value;
	}

	public static double[][] getOutputValues()
	{
		return outputValuesArray;
	}

	public static double getOutputValues(int row, int col)
	{
		return outputValuesArray[row][col];
	}

	public static void setError(double error)
	{
		errorArrayList.add(error);
	}

	public static double[] getError()
	{		
		error = new double[errorArrayList.size()];
		for (int i=0; i<errorArrayList.size(); i++)
		{
			error[i] = (double)errorArrayList.get(i);
		}
		return error;
	}

	public static void setEpochs(int epochs)
	{
		epoch = epochs;
	}

	public static int getEpochs()
	{
		return epoch;
	}

	public void setTrainedNetwork(BasicNetwork network)
	{
		nwt = network;
	}

	public static BasicNetwork getTrainedNetwork ()
	{
		return nwt;
	}

	public static void setInputToCluster(int input, int cluster)
	{
		inputToCluster[input]=cluster;
	}

	public static int[] getInputToCluster()
	{
		return inputToCluster;
	}

	public static void setSomInput(double[][] somInput)
	{
		input=somInput;
	}

	public static double[][] getSomInput()
	{
		return input;
	}
}