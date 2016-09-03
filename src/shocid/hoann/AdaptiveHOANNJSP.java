package shocid.hoann;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

import org.encog.ml.data.MLDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.TrainingSetUtil;

import shocid.elman.ElmanAdaptiveGeneticAlgorithmJSP;
import shocid.ffann.training.adaptive.FFANNAdaptiveBackPropagationJSP;
import shocid.ffann.training.adaptive.FFANNAdaptiveGeneticAlgorithmJSP;
import shocid.ffann.training.adaptive.FFANNAdaptiveLevenbergMarquardtJSP;
import shocid.ffann.training.adaptive.FFANNAdaptiveManhattanUpdateJSP;
import shocid.ffann.training.adaptive.FFANNAdaptiveResilientPropagationJSP;
import shocid.ffann.training.adaptive.FFANNAdaptiveSimulatedAnnealingJSP;
import shocid.jordan.JordanAdaptiveGeneticAlgorithmJSP;
import shocid.neatpopulation.NEATPopulationJSP;
import shocid.neukart.adaptive.NeukartAdaptiveJSP;
import shocid.normalization.NormalizeCSV2CSV;
import shocid.normalization.NormalizeCSV2NDS;
import shocid.prediction.ANNPrediction;
import shocid.readFile.ReadWithScanner;


/**
 * 
 * @author Florian Neukart
 * 
 */

public class AdaptiveHOANNJSP implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3713570090972551009L;

	public static String inputNeuronsString;  //  @jve:decl-index=0:

	public static String outputNeuronsString;  //  @jve:decl-index=0:

	public static boolean networkOnlyPositiveInput = true;

	public static String inPath = null;

	public static File inFile;

	public static File outFile;

	private static String[][] values = null;

	public static void startHOANN(String predictiveOrPattern, String norm, String normalizationType, String manualInputOrFlatfile, String manualOutputOrFlatfile, String numberInputNeuronsString, String numberOutputNeuronsString, String inFile, String outFile, String nwType, String trainingMethod, String saveNetwork, double allowedError) throws Exception {
		//Logging.stopConsoleLogging();
		BasicNetwork network = new BasicNetwork();

		int networkType = 0;

		String[] inputArray = null;
		String[] stringInputNeuronsValuesOD = null;
		int numberInputNeurons = 0; //only used with old input reader - must be activated in the source below
		int numberOutputNeurons = 0; //only used with old input reader - must be activated in the source below
		//String inputNeuronsString = null;
		double[] doubleInputNeuronsValuesOD = null;
		double[][] doubleInputNeuronsValuesMD = null;
		float floatNumberInputNeurons;

		String[] outputArray = null;
		String[] stringOutputNeuronsValuesOD = null;
		//String outputNeuronsString = null;
		double[][] doubleOutputNeuronsValuesMD = null;
		float floatNumberOutputNeurons;

		int inputArrayCounter;
		int numberHiddenNeurons;
		float floatNumberHiddenNeurons;
		int outputArrayCounter;

		boolean newInputReader = true;

		boolean normalization = false;

		//===============Predictive Analysis? Start====================


		if (predictiveOrPattern.equals("pa"))
		{
			ANNPrediction.startPrediction();
		}

		else if (predictiveOrPattern.equals("prt"))
		{
			//===============Predictive Analysis? End======================

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

				//this part is actually not used and may be removed ************/
				if (normalization == false)
				{
					if (manualInputOrFlatfile.trim().equals("f"))
					{
						ReadWithScanner parser = new ReadWithScanner();
						parser.ReadIn(parser);
					}

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
				}

				//***************************************************************/

				else
				{
					ReadWithScanner parser = new ReadWithScanner(outFile);
					parser.ReadIn(parser);
				}

				inputArray = inputNeuronsString.split(";"); // splits the input values into rows
				numberInputNeurons = inputArray[0].split(",").length;

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


				if (manualOutputOrFlatfile.trim().equals("f"))
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

			//calculate number of hidden neurons

			floatNumberInputNeurons = Float.valueOf(numberInputNeuronsString.trim()).floatValue();
			floatNumberOutputNeurons = Float.valueOf(numberOutputNeuronsString.trim()).floatValue();
			floatNumberHiddenNeurons = ((floatNumberInputNeurons / 3) * 2) + floatNumberOutputNeurons;
			numberHiddenNeurons = Math.round(floatNumberHiddenNeurons);

			MLDataSet trainingSet;

			//based on the inputfiletype, the neural trainingset is generated
			if (newInputReader == false)
			{
				//Initial Input Reader - input neurons are separated from output neurons via ";;" 
				trainingSet = new BasicNeuralDataSet(doubleInputNeuronsValuesMD, doubleOutputNeuronsValuesMD);	
			}
			else
			{
				//New Input Reader, input neurons are separated from output neurons via numeric parameters
				trainingSet = TrainingSetUtil.loadCSVTOMemory(CSVFormat.ENGLISH, getFilePath(), false, (int) Double.valueOf(numberInputNeuronsString.trim()).doubleValue(), (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue());

				if (normalization == true && normalizationType.trim().equals("c2n"))
				{
					NormalizeCSV2NDS normalize = new NormalizeCSV2NDS();
					trainingSet = normalize.normalizeMultiplicative(getInputFile(), (int) Double.valueOf(numberInputNeuronsString.trim()).doubleValue(), (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue());
					System.out.println("Normalization type: "+normalizationType.trim()+"\n");
					System.out.println(inFile+", "+outFile);
				}
			}

			boolean askForSave = false;

			//===============Start NETWORK TYPE definition====================
			switch(networkType)
			{
			case 1:

				//					//  prompt to enter the training method
				//					System.out.print("Choose training method: " + "\n" +
				//							"backpropagation(b)," + "\n" +
				//							"genetic(g)," + "\n" +
				//							"simulated annealing(a)," + "\n" +
				//							"resilient propagation(r)," + "\n" +
				//							"Manhattan update rule(m)," + "\n" +
				//							"radial basis function (rad)," + "\n" +
				//					"levenberg marquardt(lma)");
				//
				//					//  open up standard input
				//					BufferedReader brtrain = new BufferedReader(new InputStreamReader(System.in));
				//
				//					//  read the input neurons from the command-line; need to use try/catch with the readLine() method
				//					try {
				//						brtrainm = brtrain.readLine();
				//					}
				//					catch (IOException ioe) {
				//						System.out.println("IO error trying to read the input neurons.");
				//						System.exit(1);
				//					}

				if (trainingMethod.trim().equals("b"))
				{		
					FFANNAdaptiveBackPropagationJSP ffannbp = new FFANNAdaptiveBackPropagationJSP();
					ffannbp.run((int) Double.valueOf(numberInputNeuronsString.trim()).doubleValue(), numberHiddenNeurons, (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue(), trainingSet, networkOnlyPositiveInput, askForSave, saveNetwork, allowedError);
				}

				else if (trainingMethod.trim().equals("g"))
				{
					FFANNAdaptiveGeneticAlgorithmJSP ffannga = new FFANNAdaptiveGeneticAlgorithmJSP();
					ffannga.run((int) Double.valueOf(numberInputNeuronsString.trim()).doubleValue(), numberHiddenNeurons, (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue(), trainingSet, networkOnlyPositiveInput, askForSave, saveNetwork, allowedError);
				}

				else if (trainingMethod.trim().equals("a"))
				{
					FFANNAdaptiveSimulatedAnnealingJSP ffannsa = new FFANNAdaptiveSimulatedAnnealingJSP();
					ffannsa.run((int) Double.valueOf(numberInputNeuronsString.trim()).doubleValue(), numberHiddenNeurons, (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue(), trainingSet, networkOnlyPositiveInput, askForSave, saveNetwork, allowedError);
				}

				else if (trainingMethod.trim().equals("r"))
				{		
					FFANNAdaptiveResilientPropagationJSP ffannrp = new FFANNAdaptiveResilientPropagationJSP();
					ffannrp.run((int) Double.valueOf(numberInputNeuronsString.trim()).doubleValue(), numberHiddenNeurons, (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue(), trainingSet, networkOnlyPositiveInput, askForSave, saveNetwork, allowedError);
				}

				else if (trainingMethod.trim().equals("m"))
				{
					FFANNAdaptiveManhattanUpdateJSP ffannmu = new FFANNAdaptiveManhattanUpdateJSP();
					ffannmu.run((int) Double.valueOf(numberInputNeuronsString.trim()).doubleValue(), numberHiddenNeurons, (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue(), trainingSet, networkOnlyPositiveInput, askForSave, saveNetwork, allowedError);
				}

				else if (trainingMethod.trim().equals("lma"))
				{
					FFANNAdaptiveLevenbergMarquardtJSP ffannlma = new FFANNAdaptiveLevenbergMarquardtJSP();
					ffannlma.run((int) Double.valueOf(numberInputNeuronsString.trim()).doubleValue(), numberHiddenNeurons, (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue(), trainingSet, networkOnlyPositiveInput, askForSave, saveNetwork, allowedError);
				}
				
				else if (trainingMethod.trim().equals("neukart"))
				{
					NeukartAdaptiveJSP ffannneukart = new NeukartAdaptiveJSP();
					ffannneukart.run((int) Double.valueOf(numberInputNeuronsString.trim()).doubleValue(), numberHiddenNeurons, (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue(), trainingSet, networkOnlyPositiveInput, askForSave, saveNetwork, allowedError);
				}

				break;

			case 2://predictive ANNs
				if (trainingMethod.trim().equals("elman"))
				{
					ElmanAdaptiveGeneticAlgorithmJSP elman = new ElmanAdaptiveGeneticAlgorithmJSP();
					elman.run((int) Double.valueOf(numberInputNeuronsString.trim()).doubleValue(), numberHiddenNeurons, (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue(), trainingSet, networkOnlyPositiveInput, askForSave, saveNetwork, allowedError);
				}

				else if (trainingMethod.trim().equals("jordan"))
				{
					JordanAdaptiveGeneticAlgorithmJSP jordan = new JordanAdaptiveGeneticAlgorithmJSP();
					jordan.run((int) Double.valueOf(numberInputNeuronsString.trim()).doubleValue(), numberHiddenNeurons, (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue(), trainingSet, networkOnlyPositiveInput, askForSave, saveNetwork, allowedError);
				}
				
				else if (trainingMethod.trim().equals("neukart"))
				{
					NeukartAdaptiveJSP ffannneukart = new NeukartAdaptiveJSP();
					ffannneukart.run((int) Double.valueOf(numberInputNeuronsString.trim()).doubleValue(), numberHiddenNeurons, (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue(), trainingSet, networkOnlyPositiveInput, askForSave, saveNetwork, allowedError);
				}
				break;

			case 3://evolving ANN like my adaptive, genetic ANN developed before, but another approach - neat is already adaptive: no need to call it here
				if (trainingMethod.trim().equals("neat"))
				{
					NEATPopulationJSP neat = new NEATPopulationJSP();
					neat.run(network, (int) Double.valueOf(numberInputNeuronsString.trim()).doubleValue(), numberHiddenNeurons, (int) Double.valueOf(numberOutputNeuronsString.trim()).doubleValue(), trainingSet, networkOnlyPositiveInput, askForSave, saveNetwork, allowedError);
				}
				break;
			}
			//===============End NETWORK TYPE definition====================

		}
		else
		{
			System.out.println("This was no valid input - choose pa or prt");
			System.exit(1);
		}
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
}
//C:\\Users\\Florian Neukart\\workspace\\SHOCID\\test.txt
//C:\\Users\\fneukart\\workspace\\SHOCID\\test.txt
//C:\\Users\\fneukart\\workspace\\SHOCID\\elmantest.txt
//C:\\Users\\fneukart\\workspace\\SHOCID\\FFANNs\\
//C:\\workspace\\SHOCID\\test.txt
//C:\\users\\fneukart\\workspace\\SHOCID\\test3.csv
//C:\\workspace\\encog-java-core-2.5.3\\networks\\
//C:\\workspace\\encog-java-core-2.5.3\\XORTest.txt
//C:\\Users\\Florian Neukart\\workspace\\SHOCID\\XORTest.txt
//C:\\Users\\Florian Neukart\\workspace\\encog-java-core-2.5.3\\XORSomtest.txt
//C:\\Users\\fneukart\\workspace\\encog-java-core-2.5.3\\XORTest.txt
//C:\\Users\\fneukart\\workspace\\encog-java-core-2.5.3\\xorin.csv