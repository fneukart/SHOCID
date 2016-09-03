package shocid.hoann;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.TrainingSetUtil;

import java.io.Serializable;

import shocid.ffann.committee.training.BackPropagationCJSP;
import shocid.ffann.committee.training.RadialBasisCJSP;
import shocid.ffann.committee.training.LevenbergMarquardtCJSP;
import shocid.ffann.committee.training.ManhattanUpdateCJSP;
import shocid.ffann.committee.training.ResilientPropagationCJSP;
import shocid.ffann.committee.training.hybrid.GeneticAlgorithmHCJSP;
import shocid.ffann.committee.training.hybrid.SimulatedAnnealingHCJSP;
//import shocid.ffann.training.adaptive.FFANNAdaptiveGeneticAlgorithmJSP;
import shocid.ffann.training.adaptive.hybrid.FFANNHybridAdaptiveGeneticAlgorithmJSP;
import shocid.ffann.training.adaptive.hybrid.FFANNHybridAdaptiveSimulatedAnnealingJSP;
import shocid.neukart.NeukartHCJSP;
import shocid.neukart.adaptive.NeukartAdaptiveHybridJSP;
import shocid.neatpopulation.*;
import shocid.normalization.NormalizeCSV2CSV;
import shocid.normalization.NormalizeCSV2NDS;
import shocid.elman.ElmanCJSP;
import shocid.jordan.*;
import shocid.readFile.ReadWithScanner;


public class AdaptiveHybridHOANNCommitteeJSP extends Thread implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5095109804079557988L;
	private static int numberOfExperts;
	private static MLDataSet committeeTrainingSet;
	private static int nInNe;
	private static int nHiNe;
	private static int nHi2Ne;
	private static int nOuNe;
	private static int currentExpert;
	private static BasicNetwork network;
	public static BasicNetwork[] trainedCommittee;
	public static String[] trainedCommitteeMemberNames;
	private static int[] expertEpochs;

	public static String inputNeuronsString;  //  @jve:decl-index=0:

	public static String outputNeuronsString;  // 	 @jve:decl-index=0:

	private static boolean onlyPositiveInput;

	private static String inPath = null;

	private static File inFile;

	private static File outFile;

	public static boolean networkOnlyPositiveInput = true;

	private static double SOMInput[][];

	public static void startHOANNCommittee(String norm, String numberAgents, String normalizationType, String manualInputOrFlatfile, String manualOutputOrFlatfile, String numberInputNeuronsString, String numberOutputNeuronsString, String inFile, String outFile, String nwType, String trainingMethod, String saveCommittee, double allowedError) throws Exception {

		//Logging.stopConsoleLogging();

		String[] inputArray = null;
		String[] stringInputNeuronsValuesOD = null;
		double[] doubleInputNeuronsValuesOD = null;
		double[][] doubleInputNeuronsValuesMD = null;
		int numberInputNeurons = 0;//only used with old input reader - must be activated in the source below
		float floatNumberInputNeurons;

		String[] outputArray = null;
		String[] stringOutputNeuronsValuesOD = null;
		double[][] doubleOutputNeuronsValuesMD = null;
		int numberOutputNeurons = 0;//only used with old input reader - must be activated in the source below
		float floatNumberOutputNeurons;

		int inputArrayCounter;
		int numberHiddenNeurons;
		float floatNumberHiddenNeurons;
		int outputArrayCounter;
		int networkType = 0;

		boolean newInputReader = true;

		boolean normalization = false;


		//===============START NORMALIZATION====================

		if (norm.equals("y"))
		{
			normalization = true;

			File inputFile = new File(inFile);
			setInputFile(inputFile);

			if (normalizationType.trim().equals("c2c"))
			{
				File outputFile = new File(outFile);

				//set the input file for normalization and the output file as result

				setOutputFile(outputFile);
			}
		}
		//================END NORMALIZATION====================

		//===============START NETWORK TYPE====================


		//networkType = brmantype.readLine().trim();
		networkType = Integer.valueOf(nwType.trim());

		System.out.println("Reply: "+ networkType);


		//===============END NETWORK TYPE====================
		if (newInputReader == false)
			//used with the old input file reader
		{
			//===============START INPUT NEURONS====================

			if (normalization == false)
			{

				if (manualInputOrFlatfile.trim().equals("f"))
				{
					ReadWithScanner parser = new ReadWithScanner();
					parser.ReadIn(parser);
				}
				//this part is actually not used and may be removed ************/
				else
				{

					//  prompt to enter the values of the input neurons
					System.out.print("Enter the values for the input neurons: ");

					//  open up standard input
					BufferedReader brin = new BufferedReader(new InputStreamReader(System.in));

					//  read the input neurons from the command-line; need to use try/catch with the readLine() method
					try {
						inputNeuronsString = brin.readLine();
					}
					catch (IOException ioe) {
						System.out.println("IO error trying to read the input neurons.");
						System.exit(1);
					}
				}	
				//***************************************************************/
			}

			else
			{
				ReadWithScanner parser = new ReadWithScanner(outFile);
				parser.ReadIn(parser);
			}
			inputArray = inputNeuronsString.split(";"); // splits the input values into rows
			numberInputNeurons = inputArray[0].split(",").length;
			setNumberInputNeurons(numberInputNeurons);

			stringInputNeuronsValuesOD = inputNeuronsString.replace(";",",").split(",");


			doubleInputNeuronsValuesMD = new double[inputArray.length][numberInputNeurons]; //calculates the number of rows for the multidimensional neuron holder array

			doubleInputNeuronsValuesOD = new double[stringInputNeuronsValuesOD.length];
			for (int i=0;i<doubleInputNeuronsValuesOD.length;i++) 
			{
				doubleInputNeuronsValuesOD[i]=Double.valueOf(stringInputNeuronsValuesOD[i]).doubleValue(); //holds all values as double values
			}

			inputArrayCounter = 0;

			for (int r = 0;r < inputArray.length;r++) //write each input value into one input neuron
			{
				for (int l = 0;l < numberInputNeurons;l++)
				{
					doubleInputNeuronsValuesMD[r][l] = doubleInputNeuronsValuesOD[inputArrayCounter]; //e.g. 1.0,0.0;0.0,1.0
					inputArrayCounter = inputArrayCounter + 1;
				}
			}

			//================END INPUT NEURONS====================

			//===============START OUTPUT NEURONS====================

			if (manualInputOrFlatfile.trim().equals("f"))
			{
				ReadWithScanner parser = new ReadWithScanner();
				parser.ReadOut(parser);
			}

			//this part is actually not used and may be removed ************/
			else
			{

				//  prompt to enter the values of the output neurons
				System.out.print("Enter the values for the output neurons: ");

				//  open up standard input
				BufferedReader brout = new BufferedReader(new InputStreamReader(System.in));

				//  read the input neurons from the command-line; need to use try/catch with the readLine() method
				try {
					outputNeuronsString = brout.readLine();
				} catch (IOException ioe) {
					System.out.println("IO error trying to read the output neurons.");
					System.exit(1);
				}
			}

			//***************************************************************/

			outputArray = outputNeuronsString.split(";"); // splits the output values into rows
			numberOutputNeurons = outputArray[0].split(",").length;
			setNumberOutputNeurons(numberOutputNeurons);

			stringOutputNeuronsValuesOD = outputNeuronsString.replace(";",",").split(",");
			doubleOutputNeuronsValuesMD = new double[outputArray.length][numberOutputNeurons]; //calculates the number of rows for the multidimensional neuron holder array


			double doubleOutputNeuronsValuesOD[]=new double[stringOutputNeuronsValuesOD.length];
			for (int i=0;i<doubleOutputNeuronsValuesOD.length;i++) 
			{
				doubleOutputNeuronsValuesOD[i]=Double.valueOf(stringOutputNeuronsValuesOD[i]).doubleValue(); //holds all values as double values
			}

			outputArrayCounter = 0;

			for (int r = 0;r < outputArray.length;r++) //write each output value into one output neuron
			{
				for (int l = 0;l < numberOutputNeurons;l++)
				{
					doubleOutputNeuronsValuesMD[r][l] = doubleOutputNeuronsValuesOD[outputArrayCounter]; //e.g. 1.0,0.0;0.0,1.0
					outputArrayCounter = outputArrayCounter + 1;
				}
			}

			//================END OUTPUT NEURONS====================
		}
		else
		{
			//===============START INPUT AND OUTPUT NEURONS NEW FILEREADER====================

			if (normalization == false)
			{		
				//set the path to the input file containing input and output neurons values
				setFilePath(inFile);
				System.out.println(inFile);
			}

			else
			{
				if (normalizationType.trim().equals("c2c"))
				{
					System.out.println("Reading normalized file " + outFile + " as input...");
					setFilePath(outFile);					
				}

				else
				{
					setFilePath(inFile);
				}
			}

			//normalize the input file
			if (normalization == true && normalizationType.trim().equals("c2c"))
			{
				NormalizeCSV2CSV normalize = new NormalizeCSV2CSV();
				normalize.normalizeMultiplicative(getInputFile(), getOutputFile(), (int) Double.valueOf(numberInputNeuronsString.trim()).doubleValue(), (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue());

			}

			//===============END INPUT AND OUTPUT NEURONS NEW FILEREADER====================

		}
		//================START Number of Experts====================

		setNumberExperts(Integer.valueOf(numberAgents).intValue());
		System.out.println("Reply: "+ numberAgents);

		//=================END Number of Experts====================

		//calculate number of hidden neurons

		floatNumberInputNeurons = Float.valueOf((int) Double.valueOf(numberInputNeuronsString.trim()).doubleValue()).floatValue();
		floatNumberOutputNeurons = Float.valueOf((int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue()).floatValue();
		floatNumberHiddenNeurons = ((floatNumberInputNeurons / 3) * 2) + floatNumberOutputNeurons;
		numberHiddenNeurons = Math.round(floatNumberHiddenNeurons);
		setNumberHiddenNeuronsL1(numberHiddenNeurons);

		setNumberInputNeurons((int) Double.valueOf(numberInputNeuronsString.trim()).doubleValue());
		setNumberOutputNeurons((int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue());


		MLDataSet trainingSet;

		//based on the inputfiletype, the neural trainingset is generated
		if (newInputReader == false)
		{
			//Initial Input Reader - input neurons are separated from output neurons via ";;" 
			trainingSet = new BasicMLDataSet(doubleInputNeuronsValuesMD, doubleOutputNeuronsValuesMD);	
		}

		else
		{
			if (trainingMethod.equals("sofm"))
			{

				try
				{
					//array must be filled before the training set is being defined
					SOMInput = new double[ReadWithScanner.lineCount(getFilePath())][(int) Double.valueOf(numberInputNeuronsString.trim()).doubleValue()];
				}

				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				trainingSet = TrainingSetUtil.loadCSVTOMemorySOM(CSVFormat.ENGLISH, getFilePath(), false, (int) Double.valueOf(numberInputNeuronsString.trim()).doubleValue(), 0, "c");

			}
			else
			{
				trainingSet = TrainingSetUtil.loadCSVTOMemory(CSVFormat.ENGLISH, getFilePath(), false, (int) Double.valueOf(numberInputNeuronsString.trim()).doubleValue(), (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue());	
			}

			if (normalization == true && normalizationType.equals("c2n"))
			{
				NormalizeCSV2NDS normalize = new NormalizeCSV2NDS();
				trainingSet = normalize.normalizeMultiplicative(getInputFile(), (int) Double.valueOf(numberInputNeuronsString.trim()).doubleValue(), (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue());	
			}
		}

		setCommitteeTrainingSet(trainingSet);


		//define if the range of the input is only positive
		setOnlyPositiveInput(true); /*:TODO: das muss automatisch gesetzt werden, je nachdem ob nur positive, oder auch negative inputs vorhanden sind*/


		//set the value oft the outputNeuronsString variable, which defines the number of training sets
		double overallOutput[][][];
		int numberOutputs = 0;

		if (!newInputReader)
		{
			numberOutputs = outputNeuronsString.split(";").length;
		}
		else
		{
			numberOutputs = ReadWithScanner.lineCount(inPath);	
		}

		overallOutput = new double[(int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue()][getNumberExperts()][numberOutputs];

		Thread committeeThreads[];

		switch(networkType)
		{
		case 1:

			if (trainingMethod.equals("b"))
			{		
				BackPropagationCJSP committeeBP[];
				committeeBP = new BackPropagationCJSP[getNumberExperts()];
				trainedCommitteeMemberNames = new String[getNumberExperts()];
				//initialize the epoch array for all agents
				expertEpochs = new int[AdaptiveHybridHOANNCommitteeJSP.getNumberExperts()];

				for (int neurons = 0; neurons < (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue(); neurons++)
				{
					for (int numberExperts = 0; numberExperts < getNumberExperts(); numberExperts++)
					{
						for (int position = 0; position < numberOutputs; position++)	
						{
							overallOutput[neurons][numberExperts][position] = 0;
						}
					}	
				}

				//First initialization of the overallOutput-Variable in the committee Training class
				BackPropagationCJSP.initializeOverallOutput(overallOutput);

				//Thread committeeThreads[];
				committeeThreads = new Thread[getNumberExperts()];
				trainedCommittee = new BasicNetwork[getNumberExperts()];

				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
				{
					String numberExpertString = (new Integer(numberExpert+1)).toString();
					committeeBP[numberExpert] = new BackPropagationCJSP(numberExpertString,getNumberInputNeurons(),getNumberHiddenNeuronsL1(),getNumberOutputNeurons(),getCommitteeTrainingSet(), saveCommittee, allowedError);

					committeeThreads[numberExpert] = new Thread(committeeBP[numberExpert]);
				}

				setCurrentExpert(1);

				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
				{
					if (numberExpert > 0)
					{
						while (committeeThreads[numberExpert-1].isAlive())
						{
							// do nothing
						}
						setCurrentExpert(numberExpert+1);
						committeeThreads[numberExpert].start();
					}
					else
					{
						committeeThreads[numberExpert].start();
					}
				}
				while (committeeThreads[getNumberExperts()-1].isAlive())
				{
					// do nothing
				}
				BackPropagationCJSP.committeeOutput();
				BackPropagationCJSP.saveCommittee(saveCommittee);
			}

			else if (trainingMethod.equals("g"))
			{

				/*=========================================================================================================*/
				//evolve through network structures and make a committee out of the best-performing network structure

				FFANNHybridAdaptiveGeneticAlgorithmJSP ffannga = new FFANNHybridAdaptiveGeneticAlgorithmJSP();
				ffannga.run(getNumberInputNeurons(), getNumberHiddenNeuronsL1(), getNumberOutputNeurons(), trainingSet, networkOnlyPositiveInput, false, saveCommittee, allowedError);


				if (FFANNHybridAdaptiveGeneticAlgorithmJSP.getBestSHLNetworkQuality() < FFANNHybridAdaptiveGeneticAlgorithmJSP.getBestMHLNetworkQuality())
				{
					setNumberHiddenNeuronsL1(FFANNHybridAdaptiveGeneticAlgorithmJSP.getBestHiddenLayer1Neurons());
					setNumberHiddenNeuronsL2(0);
				}
				else
				{
					setNumberHiddenNeuronsL1(FFANNHybridAdaptiveGeneticAlgorithmJSP.getBestHiddenLayer1Neurons());
					setNumberHiddenNeuronsL2(FFANNHybridAdaptiveGeneticAlgorithmJSP.getBestHiddenLayer2Neurons());
				}

				/*=========================================================================================================*/


				GeneticAlgorithmHCJSP committeeGA[];
				committeeGA = new GeneticAlgorithmHCJSP[getNumberExperts()];
				trainedCommitteeMemberNames = new String[getNumberExperts()];
				//initialize the epoch array for all agents
				expertEpochs = new int[AdaptiveHybridHOANNCommitteeJSP.getNumberExperts()];

				for (int neurons = 0; neurons < (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue(); neurons++)
				{
					for (int numberExperts = 0; numberExperts < getNumberExperts(); numberExperts++)
					{
						for (int position = 0; position < numberOutputs; position++)	
						{
							overallOutput[neurons][numberExperts][position] = 0;
						}
					}	
				}

				//First initialization of the overallOutput-Variable in the committee Training class
				GeneticAlgorithmHCJSP.initializeOverallOutput(overallOutput);

				//Thread committeeThreads[];
				committeeThreads = new Thread[getNumberExperts()];
				trainedCommittee = new BasicNetwork[getNumberExperts()];

				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
				{
					String numberExpertString = (new Integer(numberExpert+1)).toString();
					committeeGA[numberExpert] = new GeneticAlgorithmHCJSP(numberExpertString,getNumberInputNeurons(),getNumberHiddenNeuronsL1(),getNumberHiddenNeuronsL2(),getNumberOutputNeurons(),getCommitteeTrainingSet(), saveCommittee, allowedError);

					committeeThreads[numberExpert] = new Thread(committeeGA[numberExpert]);
				}

				setCurrentExpert(1);

				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
				{
					if (numberExpert > 0)
					{
						while (committeeThreads[numberExpert-1].isAlive())
						{
							// do nothing
						}
						setCurrentExpert(numberExpert+1);
						committeeThreads[numberExpert].start();
					}
					else
					{
						committeeThreads[numberExpert].start();
					}
				}
				while (committeeThreads[getNumberExperts()-1].isAlive())
				{
					// do nothing
				}
				GeneticAlgorithmHCJSP.committeeOutput();
				GeneticAlgorithmHCJSP.saveCommittee(saveCommittee);
			}

			else if (trainingMethod.equals("a"))
			{
				
				/*=========================================================================================================*/
				//evolve through network structures and make a committee out of the best-performing network structure

				FFANNHybridAdaptiveSimulatedAnnealingJSP ffannsa = new FFANNHybridAdaptiveSimulatedAnnealingJSP();
				ffannsa.run(getNumberInputNeurons(), getNumberHiddenNeuronsL1(), getNumberOutputNeurons(), trainingSet, networkOnlyPositiveInput, false, saveCommittee, allowedError);


				if (FFANNHybridAdaptiveSimulatedAnnealingJSP.getBestSHLNetworkQuality() < FFANNHybridAdaptiveSimulatedAnnealingJSP.getBestMHLNetworkQuality())
				{
					setNumberHiddenNeuronsL1(FFANNHybridAdaptiveSimulatedAnnealingJSP.getBestHiddenLayer1Neurons());
					setNumberHiddenNeuronsL2(0);
				}
				else
				{
					setNumberHiddenNeuronsL1(FFANNHybridAdaptiveSimulatedAnnealingJSP.getBestHiddenLayer1Neurons());
					setNumberHiddenNeuronsL2(FFANNHybridAdaptiveSimulatedAnnealingJSP.getBestHiddenLayer2Neurons());
				}

				/*=========================================================================================================*/
				SimulatedAnnealingHCJSP committeeSA[];
				committeeSA = new SimulatedAnnealingHCJSP[getNumberExperts()];
				trainedCommitteeMemberNames = new String[getNumberExperts()];
				//initialize the epoch array for all agents
				expertEpochs = new int[AdaptiveHybridHOANNCommitteeJSP.getNumberExperts()];

				for (int neurons = 0; neurons < (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue(); neurons++)
				{
					for (int numberExperts = 0; numberExperts < getNumberExperts(); numberExperts++)
					{
						for (int position = 0; position < numberOutputs; position++)	
						{
							overallOutput[neurons][numberExperts][position] = 0;
						}
					}	
				}

				//First initialization of the overallOutput-Variable in the committee Training class
				SimulatedAnnealingHCJSP.initializeOverallOutput(overallOutput);

				//Thread committeeThreads[];
				committeeThreads = new Thread[getNumberExperts()];
				trainedCommittee = new BasicNetwork[getNumberExperts()];

				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
				{
					String numberExpertString = (new Integer(numberExpert+1)).toString();
					committeeSA[numberExpert] = new SimulatedAnnealingHCJSP(numberExpertString,getNumberInputNeurons(),getNumberHiddenNeuronsL1(),getNumberHiddenNeuronsL2(),getNumberOutputNeurons(),getCommitteeTrainingSet(), saveCommittee, allowedError);

					committeeThreads[numberExpert] = new Thread(committeeSA[numberExpert]);
				}

				setCurrentExpert(1);

				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
				{
					if (numberExpert > 0)
					{
						while (committeeThreads[numberExpert-1].isAlive())
						{
							// do nothing
						}
						setCurrentExpert(numberExpert+1);
						committeeThreads[numberExpert].start();
					}
					else
					{
						committeeThreads[numberExpert].start();
					}
				}
				while (committeeThreads[getNumberExperts()-1].isAlive())
				{
					// do nothing
				}
				SimulatedAnnealingHCJSP.committeeOutput();
				SimulatedAnnealingHCJSP.saveCommittee(saveCommittee);
			}

			else if (trainingMethod.equals("r"))
			{		
				ResilientPropagationCJSP committeeRP[];
				committeeRP = new ResilientPropagationCJSP[getNumberExperts()];
				trainedCommitteeMemberNames = new String[getNumberExperts()];
				//initialize the epoch array for all agents
				expertEpochs = new int[AdaptiveHybridHOANNCommitteeJSP.getNumberExperts()];

				for (int neurons = 0; neurons < (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue(); neurons++)
				{
					for (int numberExperts = 0; numberExperts < getNumberExperts(); numberExperts++)
					{
						for (int position = 0; position < numberOutputs; position++)	
						{
							overallOutput[neurons][numberExperts][position] = 0;
						}
					}	
				}

				//First initialization of the overallOutput-Variable in the committee Training class
				ResilientPropagationCJSP.initializeOverallOutput(overallOutput);

				//Thread committeeThreads[];
				committeeThreads = new Thread[getNumberExperts()];
				trainedCommittee = new BasicNetwork[getNumberExperts()];

				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
				{
					String numberExpertString = (new Integer(numberExpert+1)).toString();
					committeeRP[numberExpert] = new ResilientPropagationCJSP(numberExpertString,getNumberInputNeurons(),getNumberHiddenNeuronsL1(),getNumberOutputNeurons(),getCommitteeTrainingSet(), saveCommittee, allowedError);

					committeeThreads[numberExpert] = new Thread(committeeRP[numberExpert]);
				}

				setCurrentExpert(1);

				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
				{
					if (numberExpert > 0)
					{
						while (committeeThreads[numberExpert-1].isAlive())
						{
							// do nothing
						}
						setCurrentExpert(numberExpert+1);
						committeeThreads[numberExpert].start();
					}
					else
					{
						committeeThreads[numberExpert].start();
					}
				}
				while (committeeThreads[getNumberExperts()-1].isAlive())
				{
					// do nothing
				}
				ResilientPropagationCJSP.committeeOutput();
				ResilientPropagationCJSP.saveCommittee(saveCommittee);
			}


			else if (trainingMethod.equals("m"))
			{		
				ManhattanUpdateCJSP committeeMU[];
				committeeMU = new ManhattanUpdateCJSP[getNumberExperts()];
				trainedCommitteeMemberNames = new String[getNumberExperts()];
				//initialize the epoch array for all agents
				expertEpochs = new int[AdaptiveHybridHOANNCommitteeJSP.getNumberExperts()];

				for (int neurons = 0; neurons < (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue(); neurons++)
				{
					for (int numberExperts = 0; numberExperts < getNumberExperts(); numberExperts++)
					{
						for (int position = 0; position < numberOutputs; position++)	
						{
							overallOutput[neurons][numberExperts][position] = 0;
						}
					}	
				}

				//First initialization of the overallOutput-Variable in the committee Training class
				ManhattanUpdateCJSP.initializeOverallOutput(overallOutput);

				//Thread committeeThreads[];
				committeeThreads = new Thread[getNumberExperts()];
				trainedCommittee = new BasicNetwork[getNumberExperts()];

				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
				{
					String numberExpertString = (new Integer(numberExpert+1)).toString();
					committeeMU[numberExpert] = new ManhattanUpdateCJSP(numberExpertString,getNumberInputNeurons(),getNumberHiddenNeuronsL1(),getNumberOutputNeurons(),getCommitteeTrainingSet(), saveCommittee, allowedError);

					committeeThreads[numberExpert] = new Thread(committeeMU[numberExpert]);
				}

				setCurrentExpert(1);

				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
				{
					if (numberExpert > 0)
					{
						while (committeeThreads[numberExpert-1].isAlive())
						{
							// do nothing
						}
						setCurrentExpert(numberExpert+1);
						committeeThreads[numberExpert].start();
					}
					else
					{
						committeeThreads[numberExpert].start();
					}
				}
				while (committeeThreads[getNumberExperts()-1].isAlive())
				{
					// do nothing
				}
				ManhattanUpdateCJSP.committeeOutput();
				ManhattanUpdateCJSP.saveCommittee(saveCommittee);
			}

			else if (trainingMethod.equals("rad"))
			{		
				RadialBasisCJSP committeeRB[];
				committeeRB = new RadialBasisCJSP[getNumberExperts()];
				trainedCommitteeMemberNames = new String[getNumberExperts()];
				//initialize the epoch array for all agents
				expertEpochs = new int[AdaptiveHybridHOANNCommitteeJSP.getNumberExperts()];

				for (int neurons = 0; neurons < (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue(); neurons++)
				{
					for (int numberExperts = 0; numberExperts < getNumberExperts(); numberExperts++)
					{
						for (int position = 0; position < numberOutputs; position++)	
						{
							overallOutput[neurons][numberExperts][position] = 0;
						}
					}	
				}

				//First initialization of the overallOutput-Variable in the committee Training class
				RadialBasisCJSP.initializeOverallOutput(overallOutput);

				//Thread committeeThreads[];
				committeeThreads = new Thread[getNumberExperts()];
				trainedCommittee = new BasicNetwork[getNumberExperts()];

				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
				{
					String numberExpertString = (new Integer(numberExpert+1)).toString();
					committeeRB[numberExpert] = new RadialBasisCJSP(numberExpertString,getNumberInputNeurons(),getNumberHiddenNeuronsL1(),getNumberOutputNeurons(),getCommitteeTrainingSet(), saveCommittee, allowedError);

					committeeThreads[numberExpert] = new Thread(committeeRB[numberExpert]);
				}

				setCurrentExpert(1);

				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
				{
					if (numberExpert > 0)
					{
						while (committeeThreads[numberExpert-1].isAlive())
						{
							// do nothing
						}
						setCurrentExpert(numberExpert+1);
						committeeThreads[numberExpert].start();
					}
					else
					{
						committeeThreads[numberExpert].start();
					}
				}
				while (committeeThreads[getNumberExperts()-1].isAlive())
				{
					// do nothing
				}
				RadialBasisCJSP.committeeOutput();
				RadialBasisCJSP.saveCommittee(saveCommittee);
			}

			else if (trainingMethod.equals("lma"))
			{		
				LevenbergMarquardtCJSP committeeLM[];
				committeeLM = new LevenbergMarquardtCJSP[getNumberExperts()];
				trainedCommitteeMemberNames = new String[getNumberExperts()];
				//initialize the epoch array for all agents
				expertEpochs = new int[AdaptiveHybridHOANNCommitteeJSP.getNumberExperts()];

				for (int neurons = 0; neurons < (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue(); neurons++)
				{
					for (int numberExperts = 0; numberExperts < getNumberExperts(); numberExperts++)
					{
						for (int position = 0; position < numberOutputs; position++)	
						{
							overallOutput[neurons][numberExperts][position] = 0;
						}
					}	
				}

				//First initialization of the overallOutput-Variable in the committee Training class
				LevenbergMarquardtCJSP.initializeOverallOutput(overallOutput);

				//Thread committeeThreads[];
				committeeThreads = new Thread[getNumberExperts()];
				trainedCommittee = new BasicNetwork[getNumberExperts()];

				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
				{
					String numberExpertString = (new Integer(numberExpert+1)).toString();
					committeeLM[numberExpert] = new LevenbergMarquardtCJSP(numberExpertString,getNumberInputNeurons(),getNumberHiddenNeuronsL1(),getNumberOutputNeurons(),getCommitteeTrainingSet(), saveCommittee, allowedError);

					committeeThreads[numberExpert] = new Thread(committeeLM[numberExpert]);
				}

				setCurrentExpert(1);

				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
				{
					if (numberExpert > 0)
					{
						while (committeeThreads[numberExpert-1].isAlive())
						{
							// do nothing
						}
						setCurrentExpert(numberExpert+1);
						committeeThreads[numberExpert].start();
					}
					else
					{
						committeeThreads[numberExpert].start();
					}
				}
				while (committeeThreads[getNumberExperts()-1].isAlive())
				{
					// do nothing
				}
				LevenbergMarquardtCJSP.committeeOutput();
				LevenbergMarquardtCJSP.saveCommittee(saveCommittee);
			}

			else if (trainingMethod.equals("neukart"))
			{

				/*=========================================================================================================*/
				//evolve through network structures and make a committee out of the best-performing network structure

				NeukartAdaptiveHybridJSP neukartAH = new NeukartAdaptiveHybridJSP();
				neukartAH.run(getNumberInputNeurons(), getNumberHiddenNeuronsL1(), getNumberOutputNeurons(), trainingSet, networkOnlyPositiveInput, false, saveCommittee, allowedError);


				if (NeukartAdaptiveHybridJSP.getBestSHLNetworkQuality() < NeukartAdaptiveHybridJSP.getBestMHLNetworkQuality())
				{
					setNumberHiddenNeuronsL1(NeukartAdaptiveHybridJSP.getBestHiddenLayer1Neurons());
					setNumberHiddenNeuronsL2(0);
				}
				else
				{
					setNumberHiddenNeuronsL1(NeukartAdaptiveHybridJSP.getBestHiddenLayer1Neurons());
					setNumberHiddenNeuronsL2(NeukartAdaptiveHybridJSP.getBestHiddenLayer2Neurons());
				}

				/*=========================================================================================================*/


				NeukartHCJSP committeeNAH[];
				committeeNAH = new NeukartHCJSP[getNumberExperts()];
				trainedCommitteeMemberNames = new String[getNumberExperts()];
				//initialize the epoch array for all agents
				expertEpochs = new int[AdaptiveHybridHOANNCommitteeJSP.getNumberExperts()];

				for (int neurons = 0; neurons < (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue(); neurons++)
				{
					for (int numberExperts = 0; numberExperts < getNumberExperts(); numberExperts++)
					{
						for (int position = 0; position < numberOutputs; position++)	
						{
							overallOutput[neurons][numberExperts][position] = 0;
						}
					}	
				}

				//First initialization of the overallOutput-Variable in the committee Training class
				NeukartHCJSP.initializeOverallOutput(overallOutput);

				//Thread committeeThreads[];
				committeeThreads = new Thread[getNumberExperts()];
				trainedCommittee = new BasicNetwork[getNumberExperts()];

				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
				{
					String numberExpertString = (new Integer(numberExpert+1)).toString();
					committeeNAH[numberExpert] = new NeukartHCJSP(numberExpertString,getNumberInputNeurons(),getNumberHiddenNeuronsL1(),getNumberHiddenNeuronsL2(),getNumberOutputNeurons(),getCommitteeTrainingSet(), saveCommittee, allowedError);

					committeeThreads[numberExpert] = new Thread(committeeNAH[numberExpert]);
				}

				setCurrentExpert(1);

				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
				{
					if (numberExpert > 0)
					{
						while (committeeThreads[numberExpert-1].isAlive())
						{
							// do nothing
						}
						setCurrentExpert(numberExpert+1);
						committeeThreads[numberExpert].start();
					}
					else
					{
						committeeThreads[numberExpert].start();
					}
				}
				while (committeeThreads[getNumberExperts()-1].isAlive())
				{
					// do nothing
				}
				NeukartHCJSP.committeeOutput();
				NeukartHCJSP.saveCommittee(saveCommittee);
			}

			//			else if (trainingMethod.equals("sofm"))
			//			{		
			//				SelfOrganizingFeatureMapCJSP committeeSOFM[];
			//				committeeSOFM = new SelfOrganizingFeatureMapCJSP[getNumberExperts()];
			//				trainedCommitteeMemberNames = new String[getNumberExperts()];
			//				//initialize the epoch array for all agents
			//				expertEpochs = new int[HOANNCommitteeJSP.getNumberExperts()];
			//
			//				for (int neurons = 0; neurons < (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue(); neurons++)
			//				{
			//					for (int numberExperts = 0; numberExperts < getNumberExperts(); numberExperts++)
			//					{
			//						for (int position = 0; position < numberOutputs; position++)	
			//						{
			//							overallOutput[neurons][numberExperts][position] = 0;
			//						}
			//					}	
			//				}
			//
			//				//First initialization of the overallOutput-Variable in the committee Training class
			//				SelfOrganizingFeatureMapCJSP.initializeOverallOutput(overallOutput);
			//
			//				//Thread committeeThreads[];
			//				committeeThreads = new Thread[getNumberExperts()];
			//				trainedCommittee = new BasicNetwork[getNumberExperts()];
			//
			//				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
			//				{
			//					String numberExpertString = (new Integer(numberExpert+1)).toString();
			//					committeeSOFM[numberExpert] = new SelfOrganizingFeatureMapCJSP(numberExpertString,getNumberInputNeurons(),getNumberOutputNeurons(),getCommitteeTrainingSet(), saveCommittee, getFilePath());
			//
			//					committeeThreads[numberExpert] = new Thread(committeeSOFM[numberExpert]);
			//				}
			//
			//				setCurrentExpert(1);
			//
			//				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
			//				{
			//					if (numberExpert > 0)
			//					{
			//						while (committeeThreads[numberExpert-1].isAlive())
			//						{
			//							// do nothing
			//						}
			//						setCurrentExpert(numberExpert+1);
			//						committeeThreads[numberExpert].start();
			//					}
			//					else
			//					{
			//						committeeThreads[numberExpert].start();
			//					}
			//				}
			//				while (committeeThreads[getNumberExperts()-1].isAlive())
			//				{
			//					// do nothing
			//				}
			//				SelfOrganizingFeatureMapCJSP.committeeOutput();
			//				SelfOrganizingFeatureMapCJSP.saveCommittee(saveCommittee);
			//			}

			break;

		case 2:
			if (trainingMethod.equals("elman"))
			{	
				ElmanCJSP committeeElman[];
				committeeElman = new ElmanCJSP[getNumberExperts()];
				trainedCommitteeMemberNames = new String[getNumberExperts()];
				//initialize the epoch array for all agents
				expertEpochs = new int[AdaptiveHybridHOANNCommitteeJSP.getNumberExperts()];

				for (int neurons = 0; neurons < (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue(); neurons++)
				{
					for (int numberExperts = 0; numberExperts < getNumberExperts(); numberExperts++)
					{
						for (int position = 0; position < numberOutputs; position++)	
						{
							overallOutput[neurons][numberExperts][position] = 0;
						}
					}	
				}

				//First initialization of the overallOutput-Variable in the committee Training class
				ElmanCJSP.initializeOverallOutput(overallOutput);

				//Thread committeeThreads[];
				committeeThreads = new Thread[getNumberExperts()];
				trainedCommittee = new BasicNetwork[getNumberExperts()];

				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
				{
					String numberExpertString = (new Integer(numberExpert+1)).toString();
					committeeElman[numberExpert] = new ElmanCJSP(numberExpertString,getNumberInputNeurons(),getNumberHiddenNeuronsL1(),getNumberOutputNeurons(),getCommitteeTrainingSet(), saveCommittee, allowedError);

					committeeThreads[numberExpert] = new Thread(committeeElman[numberExpert]);
				}

				setCurrentExpert(1);

				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
				{
					if (numberExpert > 0)
					{
						while (committeeThreads[numberExpert-1].isAlive())
						{
							// do nothing
						}
						setCurrentExpert(numberExpert+1);
						committeeThreads[numberExpert].start();
					}
					else
					{
						committeeThreads[numberExpert].start();
					}
				}
				while (committeeThreads[getNumberExperts()-1].isAlive())
				{
					// do nothing
				}
				ElmanCJSP.committeeOutput();
				ElmanCJSP.saveCommittee(saveCommittee);

			}
			else if (trainingMethod.equals("jordan"))
			{	
				JordanCJSP committeeJordan[];
				committeeJordan = new JordanCJSP[getNumberExperts()];
				trainedCommitteeMemberNames = new String[getNumberExperts()];
				//initialize the epoch array for all agents
				expertEpochs = new int[AdaptiveHybridHOANNCommitteeJSP.getNumberExperts()];

				for (int neurons = 0; neurons < (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue(); neurons++)
				{
					for (int numberExperts = 0; numberExperts < getNumberExperts(); numberExperts++)
					{
						for (int position = 0; position < numberOutputs; position++)	
						{
							overallOutput[neurons][numberExperts][position] = 0;
						}
					}	
				}

				//First initialization of the overallOutput-Variable in the committee Training class
				JordanCJSP.initializeOverallOutput(overallOutput);

				//Thread committeeThreads[];
				committeeThreads = new Thread[getNumberExperts()];
				trainedCommittee = new BasicNetwork[getNumberExperts()];

				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
				{
					String numberExpertString = (new Integer(numberExpert+1)).toString();
					committeeJordan[numberExpert] = new JordanCJSP(numberExpertString,getNumberInputNeurons(),getNumberHiddenNeuronsL1(),getNumberOutputNeurons(),getCommitteeTrainingSet(), saveCommittee, allowedError);

					committeeThreads[numberExpert] = new Thread(committeeJordan[numberExpert]);
				}

				setCurrentExpert(1);

				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
				{
					if (numberExpert > 0)
					{
						while (committeeThreads[numberExpert-1].isAlive())
						{
							// do nothing
						}
						setCurrentExpert(numberExpert+1);
						committeeThreads[numberExpert].start();
					}
					else
					{
						committeeThreads[numberExpert].start();
					}
				}
				while (committeeThreads[getNumberExperts()-1].isAlive())
				{
					// do nothing
				}
				JordanCJSP.committeeOutput();
				JordanCJSP.saveCommittee(saveCommittee);
			}

			else if (trainingMethod.equals("neukart"))
			{

				/*=========================================================================================================*/
				//evolve through network structures and make a committee out of the best-performing network structure

				NeukartAdaptiveHybridJSP neukartAH = new NeukartAdaptiveHybridJSP();
				neukartAH.run(getNumberInputNeurons(), getNumberHiddenNeuronsL1(), getNumberOutputNeurons(), trainingSet, networkOnlyPositiveInput, false, saveCommittee, allowedError);


				if (NeukartAdaptiveHybridJSP.getBestSHLNetworkQuality() < NeukartAdaptiveHybridJSP.getBestMHLNetworkQuality())
				{
					setNumberHiddenNeuronsL1(NeukartAdaptiveHybridJSP.getBestHiddenLayer1Neurons());
					setNumberHiddenNeuronsL2(0);
				}
				else
				{
					setNumberHiddenNeuronsL1(NeukartAdaptiveHybridJSP.getBestHiddenLayer1Neurons());
					setNumberHiddenNeuronsL2(NeukartAdaptiveHybridJSP.getBestHiddenLayer2Neurons());
				}

				/*=========================================================================================================*/


				NeukartHCJSP committeeNAH[];
				committeeNAH = new NeukartHCJSP[getNumberExperts()];
				trainedCommitteeMemberNames = new String[getNumberExperts()];
				//initialize the epoch array for all agents
				expertEpochs = new int[AdaptiveHybridHOANNCommitteeJSP.getNumberExperts()];

				for (int neurons = 0; neurons < (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue(); neurons++)
				{
					for (int numberExperts = 0; numberExperts < getNumberExperts(); numberExperts++)
					{
						for (int position = 0; position < numberOutputs; position++)	
						{
							overallOutput[neurons][numberExperts][position] = 0;
						}
					}	
				}

				//First initialization of the overallOutput-Variable in the committee Training class
				NeukartHCJSP.initializeOverallOutput(overallOutput);

				//Thread committeeThreads[];
				committeeThreads = new Thread[getNumberExperts()];
				trainedCommittee = new BasicNetwork[getNumberExperts()];

				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
				{
					String numberExpertString = (new Integer(numberExpert+1)).toString();
					committeeNAH[numberExpert] = new NeukartHCJSP(numberExpertString,getNumberInputNeurons(),getNumberHiddenNeuronsL1(),getNumberHiddenNeuronsL2(),getNumberOutputNeurons(),getCommitteeTrainingSet(), saveCommittee, allowedError);

					committeeThreads[numberExpert] = new Thread(committeeNAH[numberExpert]);
				}

				setCurrentExpert(1);

				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
				{
					if (numberExpert > 0)
					{
						while (committeeThreads[numberExpert-1].isAlive())
						{
							// do nothing
						}
						setCurrentExpert(numberExpert+1);
						committeeThreads[numberExpert].start();
					}
					else
					{
						committeeThreads[numberExpert].start();
					}
				}
				while (committeeThreads[getNumberExperts()-1].isAlive())
				{
					// do nothing
				}
				NeukartHCJSP.committeeOutput();
				NeukartHCJSP.saveCommittee(saveCommittee);
			}
			break;

		case 3:
			if (trainingMethod.equals("neat"))
			{
				NEATPopulationCJSP committeeNEAT[];
				committeeNEAT = new NEATPopulationCJSP[getNumberExperts()];
				trainedCommitteeMemberNames = new String[getNumberExperts()];
				//initialize the epoch array for all agents
				expertEpochs = new int[AdaptiveHybridHOANNCommitteeJSP.getNumberExperts()];

				for (int neurons = 0; neurons < (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue(); neurons++)
				{
					for (int numberExperts = 0; numberExperts < getNumberExperts(); numberExperts++)
					{
						for (int position = 0; position < numberOutputs; position++)	
						{
							overallOutput[neurons][numberExperts][position] = 0;
						}
					}	
				}

				//First initialization of the overallOutput-Variable in the committee Training class
				NEATPopulationCJSP.initializeOverallOutput(overallOutput);

				//Thread committeeThreads[];
				committeeThreads = new Thread[getNumberExperts()];
				trainedCommittee = new BasicNetwork[getNumberExperts()];

				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
				{
					String numberExpertString = (new Integer(numberExpert+1)).toString();
					committeeNEAT[numberExpert] = new NEATPopulationCJSP(numberExpertString,getNumberInputNeurons(),getNumberHiddenNeuronsL1(),getNumberOutputNeurons(),getCommitteeTrainingSet(), saveCommittee, allowedError);

					committeeThreads[numberExpert] = new Thread(committeeNEAT[numberExpert]);
				}

				setCurrentExpert(1);

				for (int numberExpert = 0; numberExpert < getNumberExperts(); numberExpert++)
				{
					if (numberExpert > 0)
					{
						while (committeeThreads[numberExpert-1].isAlive())
						{
							// do nothing
						}
						setCurrentExpert(numberExpert+1);
						committeeThreads[numberExpert].start();
					}
					else
					{
						committeeThreads[numberExpert].start();
					}
				}
				while (committeeThreads[getNumberExperts()-1].isAlive())
				{
					// do nothing
				}
				NEATPopulationCJSP.committeeOutput();
				NEATPopulationCJSP.saveCommittee(saveCommittee);
			}
			break;
		}

	}

	public void run()
	{

	}

	public static void setNumberExperts(int numberExperts)
	{
		numberOfExperts = numberExperts; 
	}

	public static int getNumberExperts()
	{
		return numberOfExperts;
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

	public static void setNumberHiddenNeuronsL1(int numberHN)
	{
		nHiNe = numberHN;
	}

	public static int getNumberHiddenNeuronsL1()
	{
		return nHiNe;
	}

	public static void setNumberHiddenNeuronsL2(int numberHN)
	{
		nHi2Ne = numberHN;
	}

	public static int getNumberHiddenNeuronsL2()
	{
		return nHi2Ne;
	}

	public static void setNumberOutputNeurons(int numberON)
	{
		nOuNe = numberON;
	}

	public static int getNumberOutputNeurons()
	{
		return nOuNe;
	}

	public static void setCurrentExpert(int current)
	{
		currentExpert = current;
	}

	public static int getCurrentExpert()
	{
		return currentExpert;
	}

	public void setInputNeuronsString(String ins)
	{
		inputNeuronsString = ins;
	}

	public String getInputNeuronsString()
	{
		return inputNeuronsString;
	}

	public void setOutputNeuronsString(String out)
	{
		outputNeuronsString = out;
	}

	public String getOutputNeuronsString()
	{
		return outputNeuronsString;
	}

	public static void setNetwork(BasicNetwork nw)
	{
		network = nw;
	}

	public BasicNetwork getNetwork()
	{
		return network;
	}

	public static void setOnlyPositiveInput(boolean op)
	{
		onlyPositiveInput = op;
	}

	public static boolean getOnlyPositiveInput()
	{
		return onlyPositiveInput;
	}

	public static void setFilePath(String path)
	{
		inPath = path;
	}

	public static String getFilePath()
	{
		return inPath;
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

	public static void setTrainedCommittee(int agentPosition, BasicNetwork agent)
	{
		trainedCommittee[agentPosition] = agent; 
	}

	public static BasicNetwork[] getTrainedCommittee()
	{
		return trainedCommittee;
	}

	public static void setTrainedCommitteeMemberNames(int agentPosition,String agentName)
	{
		trainedCommitteeMemberNames[agentPosition] = agentName; 
	}

	public static String[] getTrainedCommitteeMemberNames()
	{
		return trainedCommitteeMemberNames;
	}

	//has been modified for the committee
	public static void setExpertEpochs(int expert, int epochs)
	{
		expertEpochs[expert] = epochs;
	}

	public static int getExpertEpochs(int expert)
	{
		return expertEpochs[expert-1];
	}

	//accessed out of TrainingSetUtil
	public static void setSOMInput(double input, int row, int neuron)
	{
		SOMInput[row][neuron] = input;
	}

	public static double[][] getSOMInput()
	{
		return SOMInput;
	}
}

//C:\\workspace\\SHOCID\\test.txt
//C:\\Users\\fneukart\\workspace\\SHOCID\\test.txt
//C:\\workspace\\encog-java-core-2.5.3\\XORTest.txt
//C:\\Users\\Florian Neukart\\workspace\\encog-java-core-2.5.3\\XORTest.txt
//C:\\Users\\fneukart\\workspace\\encog-java-core-2.5.3\\norm.csv
//C:\\Users\\fneukart\\workspace\\encog-java-core-2.5.3\\XORTest.txt