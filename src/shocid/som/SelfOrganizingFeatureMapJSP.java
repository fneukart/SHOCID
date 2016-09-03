package shocid.som;

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

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.util.obj.SerializeObject;
import org.encog.neural.som.training.basic.neighborhood.NeighborhoodSingle;
import org.encog.neural.som.SOM;
import org.encog.neural.som.training.basic.BasicTrainSOM;

import shocid.readFile.ReadWithScanner;
import shocid.utilities.Util;


public class SelfOrganizingFeatureMapJSP {

	Properties properties = new Properties();

	String propertiesFilePath = "C:\\Users\\Florian Neukart\\workspace\\SHOCID_310_withoutDB\\programobject.properties";

	static int epoch = 0;
	String brsavgyn = null;

	String savePath =null;
	String logPath = null;

	private static int numberInputNeurons = 0;
	private static MLDataSet trainingData = null;
	private static SOM nw = null;
	private static SOM nwt = null;
	private static double[][] inputValuesArray = null;
	private static double[][] outputValuesArray = null;
	private static ArrayList<Double> errorArrayList = new ArrayList<Double>();
	private static double[] error = null;
	private static int[] inputToCluster;
	private static double[][] input;

	public SelfOrganizingFeatureMapJSP()
	{
		//run(network, numberInputNeurons, numberHiddenNeurons, numberOutputNeurons, trainingSet);
	}

	public void run(int numberInputNeurons, int numberOutputClusters, MLDataSet trainingSet, boolean onlyPositiveInput, boolean askForSave, String save, String inPath, double[][] SOMInput, double allowedError)
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
		savePath = properties.getProperty("application.saveSofmAgentPath")+"SOFM_"+Util.getSetDateTime()+"_.net";
		logPath = properties.getProperty("application.logPath")+"LOG_sofm_"+Util.getSetDateTime()+".txt";

		/*methods needed for the GUI
		 *=======================================================
		 */
		setNumberInputNeurons(numberInputNeurons);
		setTrainingSet(trainingSet);
		String inputValues = null;

		/*
		 *======================================================= 
		 */

		//the agent info file
		Util.setSaveDirectory(properties.getProperty("application.saveSofmAgentPath"));
		try {
			Util.writeSingleAgentInfo("FFANNSOFM_"+Util.getSetDateTime()+"_.txt", "SOFM_"+Util.getSetDateTime()+"_.net",numberInputNeurons, numberOutputClusters);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Create the neural network
		
		SOM network;
		
		if (onlyPositiveInput)
		{
			network = new SOM(numberInputNeurons,numberOutputClusters);
			network.reset();
			(new RangeRandomizer(0, 1)).randomize(network);
		}
		else
		{
			network = new SOM(numberInputNeurons,numberOutputClusters);
			network.reset();
			(new RangeRandomizer(-1, 1)).randomize(network);
		}

		double learningRate = 0.7;

		BasicTrainSOM train = new BasicTrainSOM(network,learningRate,trainingSet,new NeighborhoodSingle());

		epoch = 0;

		int generationErrorCounter = 1;
		double generationError = 0.0;

		do {

			if (epoch > 1)
			{
				generationError = train.getError();
			}

			train.iteration();
			System.out
			.println("Epoch #" + epoch + " Error:" + train.getError());

			//add the error of the epoch to the ErrorArrayList
			setError(Double.valueOf(train.getError()));
			epoch++;
			//set the number of epochs needed for training
			setEpochs(epoch);

			if (train.getError() == generationError)
			{
				generationErrorCounter++;
			}
			else
			{
				generationErrorCounter = 0;
			}
			
		} //while ((epoch < 5000) && (train.getError() > allowedError));
		while ((generationErrorCounter < 100) && (train.getError() > allowedError));


		train.finishTraining();

		network = (SOM) train.getMethod();

		// neural network output
		System.out.println("Neural Network Results:");
		try
		{
			inputToCluster = new int[ReadWithScanner.lineCount(inPath)];
			for (int i = 0; i < ReadWithScanner.lineCount(inPath); i++)
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
			for (int i = 0; i < numberInputNeurons; i++)
			{
				//just a counter for setting line
			}
			line = line+1;
		}

		inputValuesArray = new double[line][numberInputNeurons];
		outputValuesArray = new double[line][numberOutputClusters];

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
			//final MLDataSet output = network.classify(pair.getInput());
			//System.out.println("Input Line " + line);
			//necessary, as more than one input neurons are likely
			for (int i = 0; i < numberInputNeurons; i++)
			{
				//System.out.println(pair.getInput().getData(i));
				setInputValues(line, i, pair.getInput().getData(i));
			}

			//necessary, as more than one output neurons are likely. the ideal output is the same for each expert, as this must not vary.
			for (int i = 0; i < numberOutputClusters; i++)
			{
				inputValues = null;
				for (int iv = 0; iv < numberInputNeurons; iv++)
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
				//setOutputValues(line, i, output.getData(i));
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

		setTrainedNetwork((SOM) train.getMethod());

		if (askForSave==false)
		{
			if (save.trim().equals("y"))
			{
				try {
					File networkSave = new File(savePath);
					SerializeObject.save(networkSave, (SOM) train.getMethod());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Network saved under " + savePath);
				System.out.println("Operations completed.");
				//System.exit(1);
				//network result = (FeedforwardNetwork)SerializeObject.load("FFANNBP.net");
			}
			else
			{
				System.out.println("Operations completed - network not saved.");
				//System.exit(1);
			}
		}
	}

	private String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public void setNumberInputNeurons(int number)
	{
		numberInputNeurons = number;	
	}

	public static int getNumberInputNeurons()
	{
		return numberInputNeurons;
	}

	public void setTrainingSet(MLDataSet trainingSet)
	{
		trainingData = trainingSet;
	}

	public static MLDataSet getTrainingSet()
	{
		return trainingData;
	}

	public void setNetwork(SOM network)
	{
		nw = network;
	}

	public static SOM getNetwork ()
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

	public void setTrainedNetwork(SOM som)
	{
		nwt = som;
	}

	public static SOM getTrainedNetwork ()
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