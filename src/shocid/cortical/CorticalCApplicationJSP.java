package shocid.cortical;

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
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.csv.CSVFormat;
import org.encog.util.obj.SerializeObject;
import org.encog.util.simple.TrainingSetUtil;

import shocid.utilities.Util;

public class CorticalCApplicationJSP {

	static Properties properties = new Properties();

	static String propertiesFilePath = "C:\\Users\\Florian Neukart\\workspace\\SHOCID\\programobject.properties";

	static int epoch = 0;
	String brsavgyn = null;

	static String savePath =null;
	static String logPath = null;

	private static MLDataSet trainingData = null;
	private static BasicNetwork nw = null;
	private static double[][] inputValuesArray = null;
	private static double[][] outputValuesArray = null;
	private static ArrayList<Double> errorArrayList = new ArrayList<Double>();
	private static double[] error = null;
	public static String dateTime = null;
	static String saveDirectory = null;

	public static double overallOutput[][][];
	public static double overallOutputGUI[][][];
	public static double averageExpertGUI[][];

	public static int expertColumns;

	public static int nInNe;
	public static int nHiNe;
	public static int nOuNe;

	private static double[][] outputValuesArrayAgent1 = null;
	private static double[][] outputValuesArrayAgent2 = null;
	private static double[][] outputValuesArrayAgent3 = null;
	private static double[][] outputValuesArrayAgent4 = null;
	private static double[][] outputValuesArrayAgent5 = null;

	private static ArrayList<Double> agent1Errors = new ArrayList<Double>();
	private static ArrayList<Double> agent2Errors = new ArrayList<Double>();
	private static ArrayList<Double> agent3Errors = new ArrayList<Double>();
	private static ArrayList<Double> agent4Errors = new ArrayList<Double>();
	private static ArrayList<Double> agent5Errors = new ArrayList<Double>();

	private static int numberAgents = 0;

	//public FFANNBackPropagation(BasicNetwork network, int numberInputNeurons, int numberHiddenNeurons, int numberOutputNeurons, MLDataSet trainingSet)
	public CorticalCApplicationJSP()
	{
		//run(network, numberInputNeurons, numberHiddenNeurons, numberOutputNeurons, trainingSet);
	}

	public void run(String fileName, String sourceNameAndPath)
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
		logPath = properties.getProperty("application.logPath")+"LOG_ffanncorticalc_"+Util.getSetDateTime()+".txt";

		//get the trained ANN committee
		savePath = properties.getProperty("application.saveCorticalCommitteePath");
		int numberCommitteeMembers = 0;
		try {
			numberCommitteeMembers = Util.numberCommitteeMembers(savePath+fileName);
			setNumberCommitteeMembers(numberCommitteeMembers);
			Util.loadCommitteeAgents(savePath+fileName, getNumberCommitteeMembers());
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		BasicNetwork networks[] = new BasicNetwork[numberCommitteeMembers];
		for (int i = 0; i < numberCommitteeMembers; i++)
		{
			networks[i] = Util.getCommitteeNetworks(i);
		}
		//network = loadSingleAgent(fileName);

		MLDataSet trainingSet = TrainingSetUtil.loadCSVTOMemory(CSVFormat.ENGLISH, sourceNameAndPath, false, Util.getNumberInputNeurons(), 0);

		setCommitteeTrainingSet(trainingSet);
		String inputValues = null;

		// run the neural network
		System.out.println("Neural Network Results:");

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
		outputValuesArrayAgent1 = new double[line][Util.getNumberOutputNeurons()];
		outputValuesArrayAgent2 = new double[line][Util.getNumberOutputNeurons()];
		outputValuesArrayAgent3 = new double[line][Util.getNumberOutputNeurons()];
		outputValuesArrayAgent4 = new double[line][Util.getNumberOutputNeurons()];
		outputValuesArrayAgent5 = new double[line][Util.getNumberOutputNeurons()];
		
		setNumberInputNeurons(Util.getNumberInputNeurons());
		setNumberOutputNeurons(Util.getNumberOutputNeurons());

		//create the logfile for the current training
		File logFile = new File(logPath+"logFile");

		FileWriter outFile = null;

		try {
			outFile = new FileWriter(logFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//initializeOverallOutput
		overallOutput = new double[Util.getNumberOutputNeurons()][getNumberCommitteeMembers()][Util.inputFileLineNumber(sourceNameAndPath)];
		
		for (int n = 0; n < networks.length; n++)
		{
			line = 0;
			setNetwork(networks[n]);
			for(MLDataPair pair: trainingSet)
			{
				final MLData output = getNetwork().compute(pair.getInput());

				System.out.println("Input Line " + line);
				//necessary, as more than one input neurons are likely
				for (int i = 0; i < Util.getNumberInputNeurons(); i++)
				{
					System.out.println(pair.getInput().getData(i));
					setInputValues(line, i, pair.getInput().getData(i));	
				}

				//necessary, as more than one output neurons are likely. the ideal output is the same for each expert, as this must not vary.
				for (int i = 0; i < Util.getNumberOutputNeurons(); i++)
				{
					setOverallOutput(i, output.getData(i), line, n);
					// output for testing the network results before printing the output without the 
					System.out.println("Output Neuron " + i + ":" + "\n" + "actual=" + output.getData(i));


					//set the output values - theses are not the same for all agents
					//set the resulting output values
					setOutputValues(n, line, i, output.getData(i));
				}
				line = line+1;
			}
		}
		expertColumns = line;

		try {
			outFile.close();
			SerializeObject.save(logFile,logFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //closes the file
		
		//finalize the stuff
		committeeOutput();
	}

	public static void committeeOutput()
	{
		double averageExpertValues[][] = new double[expertColumns][Util.getNumberOutputNeurons()];

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

		for (int numberExperts = 0; numberExperts < getNumberCommitteeMembers(); numberExperts++)
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
				averageExpertValues[line][averageExperts] = averageExpertValues[line][averageExperts] / getNumberCommitteeMembers();
				//System.out.println("Column: " + position + " value: " + averageExpertValues[neurons][averageExperts][position]);
				System.out.println("Input line: " + line + " Output neuron: " + averageExperts + " Value: " + averageExpertValues[line][averageExperts]);
			}
		}

		//sets the relevant arrays of all experts for the gui output (arrays with getter are easier to handle than public variables, but as time is short, both is available and will be corrected in the future... maybe)
		setOverallOutputGUI(overallOutput);
		setAverageExpertValuesGUI(averageExpertValues);
	}


	private static String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	//	public static void setCommitteeTrainingSet(MLDataSet trainingSet)
	//	{
	//		committeeTrainingSet = trainingSet;
	//	}
	//
	//	public static MLDataSet getCommitteeTrainingSet()
	//	{
	//		return committeeTrainingSet;
	//	}

	public static void setNumberInputNeurons(int numberIN)
	{
		nInNe = numberIN;
	}

	public static int getNumberInputNeurons()
	{
		return nInNe;
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

	public static void setOverallOutput(int outputNeuron, double expertOutput, int position, int currentExpert)
	{
		overallOutput[outputNeuron][currentExpert][position] = expertOutput;
	}

	public double getOverallOutput(int outputNeuron, int numberExpert, int position)
	{
		return overallOutput[outputNeuron][numberExpert][position];
	}

	public void setCommitteeTrainingSet(MLDataSet trainingSet)
	{
		trainingData = trainingSet;
	}

	public static MLDataSet getCommitteeTrainingSet()
	{
		return trainingData;
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

	private static void setNumberCommitteeMembers(int members)
	{
		numberAgents = members;
	}

	public static int getNumberCommitteeMembers()
	{
		return numberAgents;
	}
	
	public static void setNetwork(BasicNetwork network)
	{
		nw = network;
	}

	public static BasicNetwork getNetwork ()
	{
		return nw;
	}
}