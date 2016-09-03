package shocid.ffann.committee.training;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.encog.mathutil.randomize.NguyenWidrowRandomizer;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.lma.LevenbergMarquardtTraining;
import org.encog.ml.train.strategy.RequiredImprovementStrategy;
import org.encog.neural.pattern.FeedForwardPattern;
import org.encog.util.obj.SerializeObject;
import shocid.hoann.HOANNCommitteeJSP;
import shocid.utilities.Util;


public class LevenbergMarquardtCJSP extends Thread{

	static Properties properties = new Properties();

	String propertiesFilePath = "C:\\Users\\Florian Neukart\\workspace\\SHOCID\\programobject.properties";

	static int epoch = 0;
	static String brsavbyn = null;
	static String savePath =null;
	String logPath = null;
	public static MLDataSet committeeTrainingSet;
	public static int nInNe;
	public static int nHiNe;
	public static int nOuNe;
	public static double overallOutput[][][];
	public static double overallOutputGUI[][][];
	public static double averageExpertGUI[][];

	private String tName;
	public static int expertColumns;

	private static int numberInputNeurons = 0;
	private static MLDataSet trainingData = null;
	private static BasicNetwork nw = null;
	private static BasicNetwork nwt = null;
	private static double[][] inputValuesArray = null;
	private static double[][] outputValuesArrayAgent1 = null;
	private static double[][] outputValuesArrayAgent2 = null;
	private static double[][] outputValuesArrayAgent3 = null;
	private static double[][] outputValuesArrayAgent4 = null;
	private static double[][] outputValuesArrayAgent5 = null;

	private static double[][] idealValuesArray = null;
	private static ArrayList<Double> errorArrayList = new ArrayList<Double>();
	private static double[] error = null;

	//as a maximum of 5 agents is allowed, this method to get five differently-sized arrays can be done.
	private static HashMap<Integer, Double> agent1EpochErrors = new HashMap<Integer, Double>();
	private static HashMap<Integer, Double> agent2EpochErrors = new HashMap<Integer, Double>();
	private static HashMap<Integer, Double> agent3EpochErrors = new HashMap<Integer, Double>();
	private static HashMap<Integer, Double> agent4EpochErrors = new HashMap<Integer, Double>();
	private static HashMap<Integer, Double> agent5EpochErrors = new HashMap<Integer, Double>();

	private static ArrayList<Double> agent1Errors = new ArrayList<Double>();
	private static ArrayList<Double> agent2Errors = new ArrayList<Double>();
	private static ArrayList<Double> agent3Errors = new ArrayList<Double>();
	private static ArrayList<Double> agent4Errors = new ArrayList<Double>();
	private static ArrayList<Double> agent5Errors = new ArrayList<Double>();

	public static boolean onlyPositiveInput = HOANNCommitteeJSP.getOnlyPositiveInput();
	
	static double allowedError = 0.0;

	public LevenbergMarquardtCJSP(String name, int numberInputNeurons, int numberHiddenNeurons, int numberOutputNeurons, MLDataSet trainingSet, String save, double allowedError)
	{
		super(name);
		tName = name;
		setNumberInputNeurons(numberInputNeurons);
		setNumberHiddenNeurons(numberHiddenNeurons);
		setNumberOutputNeurons(numberOutputNeurons);
		setCommitteeTrainingSet(trainingSet);
		setAllowedError(allowedError);
	}

	public void run(int numberInputNeurons, int numberHiddenNeurons, int numberOutputNeurons, MLDataSet trainingSet, double allowedError) throws Exception
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
		savePath = properties.getProperty("application.saveLevenbergMarquardtCommitteePath")+"FFANNLMC_"+Util.getSetDateTime()+"_Member_";
		logPath = properties.getProperty("application.logPath")+"LOG_ffannlmc_"+Util.getSetDateTime()+".txt";

		/*methods needed for the GUI
		 *=======================================================
		 */
		setNumberInputNeurons(numberInputNeurons);
		setTrainingSet(trainingSet);
		String inputValues = null;


		/*
		 *======================================================= 
		 */
		BasicNetwork nw = new BasicNetwork();
		final FeedForwardPattern pattern = new FeedForwardPattern();
		pattern.setInputNeurons(numberInputNeurons);
		pattern.setOutputNeurons(numberOutputNeurons);

		nw = (BasicNetwork) pattern.generate();
		nw.reset();

		(new NguyenWidrowRandomizer()).randomize(nw);

		// train the neural network
		final LevenbergMarquardtTraining train = new LevenbergMarquardtTraining(nw, trainingSet);

		// reset if improve is less than 1% over 5 cycles
		train.addStrategy(new RequiredImprovementStrategy(5));

		//EncogUtility.trainToError(train, nw, trainingSet, 0.01);

		epoch = 1;

		int currentExpert = HOANNCommitteeJSP.getCurrentExpert()-1;

		int generationErrorCounter = 1;
		double generationError = 0.0;

		do
		{
			if (epoch > 1)
			{
				generationError = train.getError();
			}
			
			train.iteration();
			//System.out.println("Epoch #" + epoch + " Error:" + train.getError());

			switch(HOANNCommitteeJSP.getCurrentExpert())
			{
			case 1:
				agent1EpochErrors.put(epoch, train.getError());
				agent1Errors.add(train.getError());
				break;
			case 2:
				agent2EpochErrors.put(epoch, train.getError());
				agent2Errors.add(train.getError());
				break;
			case 3:
				agent3EpochErrors.put(epoch, train.getError());
				agent3Errors.add(train.getError());
				break;
			case 4:
				agent4EpochErrors.put(epoch, train.getError());
				agent4Errors.add(train.getError());
				break;
			case 5:
				agent5EpochErrors.put(epoch, train.getError());
				agent5Errors.add(train.getError());
				break;
			}

			epoch++;

			HOANNCommitteeJSP.setExpertEpochs(currentExpert, epoch);
			
			if (train.getError() == generationError)
			{
				generationErrorCounter++;
			}
			else
			{
				generationErrorCounter = 0;
			}
		}
		//while((epoch < 25000) && (train.getError() > 0.001));
		while(generationErrorCounter < 100 && (train.getError() > allowedError));

		train.finishTraining();

		nw = (BasicNetwork) train.getMethod();

		System.out.println("Expert " + HOANNCommitteeJSP.getCurrentExpert() + " has " + numberInputNeurons + " input neurons, " + numberHiddenNeurons + " hidden neurons, "+ numberOutputNeurons + " output neurons, and an error rate of about " + train.getError()*100 + "%");

		// test the neural network
		//double array with <number of training data sets columns>. each column must contain the mean value of all experts at the desired position
		int line = 0;

		for(MLDataPair pair: trainingSet)
		{
			//necessary, as more than one input neurons are likely
			for (int i = 0; i < numberInputNeurons; i++)
			{
				//just a counter for setting line
			}
			line = line+1;
		}

		inputValuesArray = new double[line][numberInputNeurons];
		outputValuesArrayAgent1 = new double[line][numberOutputNeurons];
		outputValuesArrayAgent2 = new double[line][numberOutputNeurons];
		outputValuesArrayAgent3 = new double[line][numberOutputNeurons];
		outputValuesArrayAgent4 = new double[line][numberOutputNeurons];
		outputValuesArrayAgent5 = new double[line][numberOutputNeurons];
		idealValuesArray = new double[line][numberOutputNeurons];

		line=0;

		for(MLDataPair pair: trainingSet )
		{
			final MLData output = nw.compute(pair.getInput());
			/*
			setOverallOutput(output.getData(0), line);
			// output for testing the network results before printing the output without the 
			System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1) + ", actual=" + output.getData(0) + ", ideal=" + pair.getIdeal().getData(0));
			line = line+1;
			 */

			System.out.println("Input Line " + line);
			//necessary, as more than one input neurons are likely
			for (int i = 0; i < numberInputNeurons; i++)
			{
				System.out.println(pair.getInput().getData(i));
				setInputValues(line, i, pair.getInput().getData(i));	
			}

			//necessary, as more than one output neurons are likely. the ideal output is the same for each expert, as this must not vary.
			for (int i = 0; i < numberOutputNeurons; i++)
			{
				setOverallOutput(i, output.getData(i), line);
				// output for testing the network results before printing the output without the 
				System.out.println("Output Neuron " + i + ":" + "\n" + "actual=" + output.getData(i) + ", ideal=" + pair.getIdeal().getData(i));


				//set the ideal values - these are the same for all agents
				setIdealValues(line, i, pair.getIdeal().getData(i));

				//set the output values - theses are not the same for all agents
				//set the resulting output values
				setOutputValues(HOANNCommitteeJSP.getCurrentExpert()-1, line, i, output.getData(i));
			}
			line = line+1;
		}
		expertColumns = line;

		//this method has been implemented during developing the GUI - an array with a getter method is always easier to handle (TODO: use getter methods also in HOANNCommitteeJSP).
		//HOANNCommitteeJSP.trainedCommittee[HOANNCommitteeJSP.getCurrentExpert()-1] = (BasicNetwork) train.getMethod();
		HOANNCommitteeJSP.setTrainedCommittee(HOANNCommitteeJSP.getCurrentExpert()-1,(BasicNetwork) train.getMethod());
	}

	@Override
	public void run()
	{
		try
		{
			//run(getNetwork(), getNumberInputNeurons(), getNumberHiddenNeurons(), getNumberOutputNeurons(), getCommitteeTrainingSet());
			run(getNumberInputNeurons(), getNumberHiddenNeurons(), getNumberOutputNeurons(), getCommitteeTrainingSet(), getAllowedError());
		}
		catch (Exception e)
		{
			System.out.println( "An error occured in " + tName );
			e.printStackTrace();
		}
		finally
		{
			System.out.println( tName + " has finished calculations..." );

			//method implemented during developing the GUI:
			//			HOANNCommitteeJSP.trainedCommitteeMemberNames[HOANNCommitteeJSP.getCurrentExpert()-1] = tName;
			HOANNCommitteeJSP.setTrainedCommitteeMemberNames(HOANNCommitteeJSP.getCurrentExpert()-1, tName);
		}
	}


	/*the averageExpertValues array, which contains the average values of all experts, is filled
	 * after filling the array, the average values are calculated
	 * all expert values are printed out
	 *  
	 */
	public static void committeeOutput()
	{
		double averageExpertValues[][] = new double[expertColumns][HOANNCommitteeJSP.getNumberOutputNeurons()];

		/*Print out all expert values - just for testing the overall output array
		 * the initial output was already printed out
		 *
		System.out.println("All expert values: ");
		for (int numberExperts = 0; numberExperts < HOANNCommitteeJSP.getNumberExperts(); numberExperts++)
		{
			System.out.println("Expert: " + numberExperts);
			for (int position = 0; position < expertColumns; position++)
			{
				System.out.println(" column: " + position + " value: " + overallOutput[numberExperts][position]);
			}	
		}
		 */

		//fills the sum of all values into the averageExpertValues array

		for (int numberExperts = 0; numberExperts < HOANNCommitteeJSP.getNumberExperts(); numberExperts++)
		{
			for (int line = 0; line < expertColumns; line ++)
			{
				for (int currentOutputNeuron = 0; currentOutputNeuron < getNumberOutputNeurons(); currentOutputNeuron ++)
				{ 
					averageExpertValues[line][currentOutputNeuron] = averageExpertValues[line][currentOutputNeuron] + overallOutput[currentOutputNeuron][numberExperts][line];
					//					System.out.println("average at line: "+line+" and output neuron: "+currentOutputNeuron+" is "+averageExpertValues[line][currentOutputNeuron]+"\n");
					//					System.out.println("overall at expert: "+numberExperts+" and line: "+ +line+" and output neuron: "+currentOutputNeuron+" is "+averageExpertValues[line][currentOutputNeuron]+"\n");
				}
			}
		}	

		//calculates the average expert values in the averageExpertValues array and prints out the average expert values
		System.out.println("Average Expert Values: ");
		for (int line = 0; line < expertColumns; line++)	

		{
			for (int averageExperts = 0; averageExperts < getNumberOutputNeurons(); averageExperts++)		
			{
				//averageExpertValues[neurons][averageExperts][position] = averageExpertValues[neurons][averageExperts][position] / HOANNCommitteeJSP.getNumberExperts();
				averageExpertValues[line][averageExperts] = averageExpertValues[line][averageExperts] / HOANNCommitteeJSP.getNumberExperts();
				//System.out.println("Column: " + position + " value: " + averageExpertValues[neurons][averageExperts][position]);
				System.out.println("Input line: " + line + " Output neuron: " + averageExperts + " Value: " + averageExpertValues[line][averageExperts]);
			}
		}

		//sets the relevant arrays of all experts for the gui output (arrays with getter are easier to handle than public variables, but as time is short, both is available and will be corrected in the future... maybe)
		setOverallOutputGUI(overallOutput);
		setAverageExpertValuesGUI(averageExpertValues);
	}

	/* method for saving the committee, meaning saving every expert.
	 * 
	 */
	public static void saveCommittee(String save)
	{
		if (save.trim().equals("y"))
		{
			//the agent info file
			Util.setSaveDirectory(properties.getProperty("application.saveLevenbergMarquardtCommitteePath"));
			try {
				String fileName = properties.getProperty("application.saveLevenbergMarquardtCommitteePath")+"FFANNLMC_"+Util.getSetDateTime()+".txt";
				Util.writeCommitteeInfo(fileName, savePath, getNumberInputNeurons(), getNumberOutputNeurons());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				//SerializeObject.save("C:\\workspace\\encog-java-core-2.5.3\\networks\\FFANNBP.net", (BasicNetwork) train.getMethod());
				for (int i=0; i< HOANNCommitteeJSP.trainedCommittee.length; i++)
				{
					File networkSave = new File(savePath+HOANNCommitteeJSP.getTrainedCommitteeMemberNames()[i]+".net");
					SerializeObject.save(networkSave, HOANNCommitteeJSP.getTrainedCommittee()[i]);	
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Committee members saved under " + savePath + "*.net");
			//network result = (FeedforwardNetwork)SerializeObject.load("FFANNBP.net");
		}
		System.out.println("Operations completed.");
		//System.exit(1);
	}

	private static String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public static void setCommitteeTrainingSet(MLDataSet trainingSet)
	{
		committeeTrainingSet = trainingSet;
	}

	public static MLDataSet getCommitteeTrainingSet()
	{
		return committeeTrainingSet;
	}

	public static void setNumberInputNeurons(int numberIN)
	{
		nInNe = numberIN;
	}

	public static int getNumberInputNeurons()
	{
		return nInNe;
	}

	public static void setNumberHiddenNeurons(int numberHN)
	{
		nHiNe = numberHN;
	}

	public int getNumberHiddenNeurons()
	{
		return nHiNe;
	}

	public static void setNumberOutputNeurons(int numberON)
	{
		nOuNe = numberON;
	}

	public static int getNumberOutputNeurons()
	{
		return nOuNe;
	}

	public static void initializeOverallOutput(double output[][][])
	{
		overallOutput=output;
	}

	public static void setOverallOutput(int outputNeuron, double expertOutput, int position)
	{
		overallOutput[outputNeuron][HOANNCommitteeJSP.getCurrentExpert()-1][position] = expertOutput;
	}

	public double getOverallOutput(int outputNeuron, int numberExpert, int position)
	{
		return overallOutput[outputNeuron][numberExpert][position];
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

	public void setOutputValues(int agent, int row, int col, double value)
	{
		switch(agent)
		{
		case 0:
			outputValuesArrayAgent1[row][col] = value;
			break;
		case 1:
			outputValuesArrayAgent2[row][col] = value;
			break;
		case 2:
			outputValuesArrayAgent3[row][col] = value;
			break;
		case 3:
			outputValuesArrayAgent4[row][col] = value;
			break;
		case 4:
			outputValuesArrayAgent5[row][col] = value;
			break;
		}
	}

	public static double[][] getOutputValues(int agent)
	{
		//all agents have the same size
		double[][] requestedOutputValuesArray = new double[outputValuesArrayAgent1.length][outputValuesArrayAgent1[0].length];

		switch(agent)
		{
		case 0:
			requestedOutputValuesArray = outputValuesArrayAgent1;
			break;
		case 1:
			requestedOutputValuesArray = outputValuesArrayAgent2;
			break;
		case 2:
			requestedOutputValuesArray = outputValuesArrayAgent3;
			break;
		case 3:
			requestedOutputValuesArray = outputValuesArrayAgent4;
			break;
		case 4:
			requestedOutputValuesArray = outputValuesArrayAgent5;
			break;
		}
		return requestedOutputValuesArray;
	}

	public static double getOutputValues(int agent, int row, int col)
	{
		double[][] requestedOutputValuesArray = new double[outputValuesArrayAgent1.length][outputValuesArrayAgent1[0].length];

		switch(agent)
		{
		case 0:
			requestedOutputValuesArray = outputValuesArrayAgent1;
			break;
		case 1:
			requestedOutputValuesArray = outputValuesArrayAgent2;
			break;
		case 2:
			requestedOutputValuesArray = outputValuesArrayAgent3;
			break;
		case 3:
			requestedOutputValuesArray = outputValuesArrayAgent4;
			break;
		case 4:
			requestedOutputValuesArray = outputValuesArrayAgent5;
			break;
		}

		return requestedOutputValuesArray[row][col];
	}

	public void setIdealValues(int row, int col, double value)
	{
		idealValuesArray[row][col] = value;
	}

	public static double[][] getIdealValues()
	{
		return idealValuesArray;
	}

	public static double getIdealValues(int row, int col)
	{
		return idealValuesArray[row][col];
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

	public static void setOverallOutputGUI(double[][][] overall)
	{
		overallOutputGUI = overall;
	}

	public static double[][][] getOverallOutputGUI()
	{
		return overallOutputGUI;
	}

	public static void setAverageExpertValuesGUI(double[][] average)
	{
		averageExpertGUI = average;
	}

	public static double[][] getAverageExpertValuesGUI()
	{
		return averageExpertGUI;
	}

	public static HashMap<Integer, Double> getAgentEpochErrors(int agentNumber)
	{
		HashMap<Integer, Double> requestedAgentErrors = new HashMap<Integer, Double>();
		switch(agentNumber)
		{
		case 1:
			requestedAgentErrors = agent1EpochErrors;
			break;
		case 2:
			requestedAgentErrors = agent2EpochErrors;
			break;
		case 3:
			requestedAgentErrors = agent3EpochErrors;
			break;
		case 4:
			requestedAgentErrors = agent4EpochErrors;
			break;
		case 5:
			requestedAgentErrors = agent5EpochErrors;
			break;
		}
		return requestedAgentErrors;
	}

	public static ArrayList<Double> getAgentErrors(int agentNumber)
	{
		ArrayList<Double> requestedAgentErrors = new ArrayList<Double>();
		switch(agentNumber)
		{
		case 1:
			requestedAgentErrors = agent1Errors;
			break;
		case 2:
			requestedAgentErrors = agent2Errors;
			break;
		case 3:
			requestedAgentErrors = agent3Errors;
			break;
		case 4:
			requestedAgentErrors = agent4Errors;
			break;
		case 5:
			requestedAgentErrors = agent5Errors;
			break;
		}
		return requestedAgentErrors;
	}
	
	private static void setAllowedError(double error)
	{
		allowedError = error;
	}
	
	private static double getAllowedError()
	{
		return allowedError;
	}
}