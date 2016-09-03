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

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.mathutil.randomize.Randomizer;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.util.obj.SerializeObject;
import org.encog.neural.networks.training.anneal.NeuralSimulatedAnnealing;
import org.encog.neural.pattern.FeedForwardPattern;

import shocid.utilities.Util;

public class CorticalJSPStd {

		Properties properties = new Properties();

		String propertiesFilePath = "C:\\Users\\Florian Neukart\\workspace\\SHOCID\\programobject.properties";

		static int epoch = 0;
		String brsavgyn = null;

		String savePath =null;
		String logPath = null;

		private static int numberInputNeurons = 0;
		private static MLDataSet trainingData = null;
		private static BasicNetwork nw = null;
		private static BasicNetwork nwt = null;
		private static double[][] inputValuesArray = null;
		private static double[][] outputValuesArray = null;
		private static double[][] idealValuesArray = null;
		private static ArrayList<Double> errorArrayList = new ArrayList<Double>();
		private static double[] error = null;

		public CorticalJSPStd()
		{
			//run(network, numberInputNeurons, numberHiddenNeurons, numberOutputNeurons, trainingSet);
		}

		public void run(BasicNetwork network, int numberInputNeurons, int numberHiddenNeurons, int numberOutputNeurons, MLDataSet trainingSet, boolean onlyPositiveInput, boolean askForSave, String save, double allowedError)
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
			savePath = properties.getProperty("application.saveCorticalAgentPath")+"FFANNCORTICAL_"+Util.getSetDateTime()+"_.net";
			logPath = properties.getProperty("application.logPath")+"LOG_ffanncortical_"+Util.getSetDateTime()+".txt";

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
			Util.setSaveDirectory(properties.getProperty("application.saveCorticalAgentPath"));
			try {
				Util.writeSingleAgentInfo("FFANNCORTICAL_"+Util.getSetDateTime()+"_.txt", "FFANNCORTICAL_"+Util.getSetDateTime()+"_.net",numberInputNeurons, numberOutputNeurons);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			/*=================BEGINNING OF THE OLD STRUCTURE INPUT - due to stability plasticity problem not developed further=============*/
			
//			float floatNumberHiddenNeurons = Float.valueOf(numberHiddenNeurons).floatValue();
//			float floatNumberOutputNeurons = Float.valueOf(numberOutputNeurons).floatValue();
//			float floatNumberHiddenNeuronsC2 = ((floatNumberHiddenNeurons / 3) * 2) + floatNumberOutputNeurons;
//			int numberHiddenNeuronsC2 = Math.round(floatNumberHiddenNeuronsC2);
//			float floatNumberCommonCortexNeurons = (((floatNumberHiddenNeuronsC2 + floatNumberHiddenNeuronsC2) / 3) * 2) + floatNumberOutputNeurons;
//			int numberCommmonCortexNeurons = Math.round(floatNumberCommonCortexNeurons);
			
			/*=================END OF THE OLD STRUCTURE INPUT - due to stability plasticity problem not developed further=============*/
			
			/*=================BEGINNING OF THE NEW STRUCTURE INPUT=============*/
			numberHiddenNeurons = 20;
			int numberCortices = 2;
			float floatNumberHiddenNeurons = Float.valueOf(numberHiddenNeurons).floatValue();
			float floatNumberOutputNeurons = Float.valueOf(numberOutputNeurons).floatValue();
			float floatNumberCommonCortexNeurons = (floatNumberHiddenNeurons * numberCortices) + floatNumberOutputNeurons;
			int numberCommmonCortexNeurons = Math.round(floatNumberCommonCortexNeurons);
			
			/*=================END OF THE NEW STRUCTURE INPUT=============*/
			
			Randomizer randomizer;
			final FeedForwardPattern pattern = new FeedForwardPattern();
			network = (BasicNetwork) pattern.generate();

			if (onlyPositiveInput)
			{
				
				
				/*=================BEGINNING OF THE OLD STRUCTURE - due to stability plasticity problem not developed further=============*/
				
//				BasicLayer sensoryFieldLayer = new BasicLayer(new ActivationSigmoid(), true, numberInputNeurons);
//				BasicLayer cortexOneLayer1 = new BasicLayer(new ActivationSigmoid(), true, numberHiddenNeurons);
//				BasicLayer cortexOneLayer2 = new BasicLayer(new ActivationSigmoid(), true, numberHiddenNeuronsC2);
//				BasicLayer cortexTwoLayer1 = new BasicLayer(new ActivationSigmoid(), true, numberHiddenNeurons);
//				BasicLayer cortexTwoLayer2 = new BasicLayer(new ActivationSigmoid(), true, numberHiddenNeuronsC2);
//				BasicLayer commonCortexLayer = new BasicLayer(new ActivationSigmoid(), true, numberCommmonCortexNeurons);
//				BasicLayer worldLayer = new BasicLayer(new ActivationSigmoid(), true, numberOutputNeurons);
//				
//				BasicLayer cortexOneContextLayer = new ContextLayer(numberHiddenNeuronsC2);
//				BasicLayer cortexTwoContextLayer = new ContextLayer(numberHiddenNeuronsC2);
//
//				network.addLayer(sensoryFieldLayer);
//				network.addLayer(cortexOneLayer1);
//				network.addLayer(cortexOneLayer2);
//				network.addLayer(cortexOneContextLayer);
//				network.addLayer(cortexTwoLayer1);
//				network.addLayer(cortexTwoLayer2);
//				network.addLayer(cortexTwoContextLayer);
//				network.addLayer(commonCortexLayer);
//				network.addLayer(worldLayer);
//
//				sensoryFieldLayer.addNext(cortexOneLayer1, SynapseType.Weighted);
//				sensoryFieldLayer.addNext(cortexTwoLayer1, SynapseType.Weighted);
//				cortexOneLayer1.addNext(cortexOneLayer2, SynapseType.Weighted);
//				cortexOneLayer2.addNext(commonCortexLayer, SynapseType.Weighted);
//				cortexOneLayer2.addNext(cortexOneContextLayer, SynapseType.Weighted);
//				cortexOneContextLayer.addNext(cortexOneLayer1, SynapseType.Weighted);
//				cortexTwoLayer1.addNext(cortexTwoLayer2, SynapseType.Weighted);
//				cortexTwoLayer2.addNext(commonCortexLayer, SynapseType.Weighted);
//				cortexTwoLayer2.addNext(cortexTwoContextLayer, SynapseType.Weighted);
//				cortexTwoContextLayer.addNext(cortexTwoLayer1, SynapseType.Weighted);
//				commonCortexLayer.addNext(worldLayer, SynapseType.Weighted);

				/*=================END OF THE OLD STRUCTURE - due to stability plasticity problem not developed further=============*/
				
				/*=================BEGINNING OF THE NEW STRUCTURE=============*/
				
				BasicLayer sensoryFieldLayer = new BasicLayer(new ActivationSigmoid(), true, numberInputNeurons);
				BasicLayer cortexOneLayer1 = new BasicLayer(new ActivationSigmoid(), true, numberHiddenNeurons);
				BasicLayer cortexTwoLayer1 = new BasicLayer(new ActivationSigmoid(), true, numberHiddenNeurons);
				BasicLayer commonCortexLayer = new BasicLayer(new ActivationTANH(), true, numberCommmonCortexNeurons);
				BasicLayer worldLayer = new BasicLayer(new ActivationTANH(), true, numberOutputNeurons);
				
				network.addLayer(sensoryFieldLayer); //0
				network.addLayer(cortexOneLayer1); //1
				network.addLayer(cortexTwoLayer1); //2
				network.addLayer(commonCortexLayer); //3
				network.addLayer(worldLayer); //4
				
				//disable the automatically set connections between the cortexlayers
				for (int nc1 = 0; nc1 < cortexOneLayer1.getNeuronCount(); nc1++)
				{
					for (int nc2 = 0; nc2 < cortexTwoLayer1.getNeuronCount(); nc2++)
					{
						network.enableConnection(1, nc1, 2, nc2, false);	
					}
				}
				
				//enable the connections between the second cortex layer and the commoncortexlayer
				for (int nc1 = 0; nc1 < cortexOneLayer1.getNeuronCount(); nc1++)
				{
					for (int nc2 = 0; nc2 < commonCortexLayer.getNeuronCount(); nc2++)
					{
						network.enableConnection(1, nc1, 3, nc2, true);	
					}
				}
				
				//set the context layers
				cortexOneLayer1.setContextFedBy(cortexOneLayer1);
				cortexTwoLayer1.setContextFedBy(cortexTwoLayer1);
				
				/*=================END OF THE NEW STRUCTURE=============*/
				
				randomizer = new RangeRandomizer(-1,1);

				network.getStructure().finalizeStructure();
				network.reset();
				
//				System.out.println("Sensory Field to Cortex 1 1: "+sensoryFieldLayer.isConnectedTo(cortexOneLayer1)+"\n");
//				System.out.println("Sensory Field to Cortex 2 1: "+sensoryFieldLayer.isConnectedTo(cortexTwoLayer1)+"\n");
//				System.out.println("Cortex 1 1 to Cortex 1 2: "+cortexOneLayer1.isConnectedTo(cortexOneLayer2)+"\n");
//				System.out.println("Cortex 2 1 to Cortex 2 2: "+cortexTwoLayer1.isConnectedTo(cortexTwoLayer2)+"\n");
//				System.out.println("Cortex 1 2 to Common Cortex: "+cortexOneLayer2.isConnectedTo(commonCortexLayer)+"\n");
//				System.out.println("Cortex 2 2 to Common Cortex: "+cortexTwoLayer2.isConnectedTo(commonCortexLayer)+"\n");
//				System.out.println("Cortex 1 2 to cortexOneContextLayer: "+cortexOneLayer2.isConnectedTo(cortexOneContextLayer)+"\n");
//				System.out.println("Cortex 2 2 to cortexTwoContextLayer: "+cortexTwoLayer2.isConnectedTo(cortexTwoContextLayer)+"\n");
//				System.out.println("cortexOneContextLayer to Cortex 1 1: "+cortexOneContextLayer.isConnectedTo(cortexOneLayer1)+"\n");
//				System.out.println("cortexTwoContextLayer to Cortex 2 1: "+cortexTwoContextLayer.isConnectedTo(cortexTwoLayer1)+"\n");
//				System.out.println("Common Cortex to World: "+commonCortexLayer.isConnectedTo(worldLayer)+"\n");
				

			}
			else
			{
				BasicLayer sensoryFieldLayer = new BasicLayer(new ActivationTANH(), true, numberInputNeurons);
				BasicLayer cortexOneLayer1 = new BasicLayer(new ActivationTANH(), true, numberHiddenNeurons);
				BasicLayer cortexTwoLayer1 = new BasicLayer(new ActivationTANH(), true, numberHiddenNeurons);
				BasicLayer commonCortexLayer = new BasicLayer(new ActivationTANH(), true, numberCommmonCortexNeurons);
				BasicLayer worldLayer = new BasicLayer(new ActivationTANH(), true, numberOutputNeurons);
				
				network.addLayer(sensoryFieldLayer); //0
				network.addLayer(cortexOneLayer1); //1
				network.addLayer(cortexTwoLayer1); //2
				network.addLayer(commonCortexLayer); //3
				network.addLayer(worldLayer); //4
				
				//disable the automatically set connections between the cortexlayers
				for (int nc1 = 0; nc1 < cortexOneLayer1.getNeuronCount(); nc1++)
				{
					for (int nc2 = 0; nc2 < cortexTwoLayer1.getNeuronCount(); nc2++)
					{
						network.enableConnection(1, nc1, 2, nc2, false);	
					}
				}
				
				//enable the connections between the second cortex layer and the commoncortexlayer
				for (int nc1 = 0; nc1 < cortexOneLayer1.getNeuronCount(); nc1++)
				{
					for (int nc2 = 0; nc2 < commonCortexLayer.getNeuronCount(); nc2++)
					{
						network.enableConnection(1, nc1, 3, nc2, true);	
					}
				}

				randomizer = new RangeRandomizer(-1,1);

				network.getStructure().finalizeStructure();
				network.reset();
			}

			
			CalculateScore score = new TrainingSetScore(trainingSet);
//
//			int populationSize = 5000;
//			double mutationPercent = 0.2;
//			double percentToMate = 0.25;


//			// train the neural committee member
			//NeuralGeneticAlgorithm train = new NeuralGeneticAlgorithm(network, randomizer, score, populationSize, mutationPercent, percentToMate);
			
			//CalculateScore score = new TrainingSetScore(trainingSet);

			final double startTemp = 30;
			final double stopTemp = 2; 
			final int cycles = 300;

			// train the neural network through simulated annealing
			final NeuralSimulatedAnnealing train = new NeuralSimulatedAnnealing(network, score, startTemp, stopTemp, cycles);

			epoch = 1;
			
			int generationErrorCounter = 1;
			double generationError = 0.0;
			/*=====================for the blackscholes test==========================*/
			double allowedDeviation = 0.05912571716509050000;
			int counter = 0;
			/*=====================for the blackscholes test==========================*/

			//the neural network is trained until the error rate has not been found for 100 generations
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
				
				/*=====================for the blackscholes test==========================*/
				
				
//				int bcline = 0;
//				
//				for(NeuralDataPair pair: trainingSet)
//				{
//					bcline = bcline+1;
//					System.out.println("training set size: "+bcline);
//				}
				
				
				counter = 0;
				for(MLDataPair pair: trainingSet)
				{
					final MLData output = network.compute(pair.getInput());
					//necessary, as more than one output neurons are likely. the ideal output is the same for each expert, as this must not vary.
					for (int i = 0; i < numberOutputNeurons; i++)
					{
						if(output.getData(i) <= Double.valueOf(pair.getIdeal().getData(i)).doubleValue()+allowedDeviation
						&&output.getData(i) >= Double.valueOf(pair.getIdeal().getData(i)).doubleValue()-allowedDeviation
						)
						{
							counter = counter+1;
						}
					}
//					bcline = bcline+1;
				}
				System.out.println(counter);
				
			
				/*=====================for the blackscholes test==========================*/
				
			} //while ((generationErrorCounter < 1000) && (train.getError() > allowedError) && epoch < 250000);
			while (counter < 1515);
			

			train.finishTraining();

			network = train.getMethod();

			// test the neural network
			System.out.println("Neural Network Results:");
			//		for(NeuralDataPair pair: trainingSet ) {
			//			final NeuralData output = network.compute(pair.getInput());
			//			System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1)
			//					+ ", actual=" + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
			//		}


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
			outputValuesArray = new double[line][numberOutputNeurons];
			idealValuesArray = new double[line][numberOutputNeurons];

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

					// output for testing the network results before printing the output without the 
					System.out.println("Output Neuron " + i + ":" + "\n" + "actual=" + output.getData(i) + ", ideal=" + pair.getIdeal().getData(i));

					//save the results to the log file
					try {
						outFile.append("Output Neuron " + i + ":" + "\n" + "actual=" + output.getData(i) + ", ideal=" + pair.getIdeal().getData(i)+"\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					//set the ideal values
					setIdealValues(line, i, pair.getIdeal().getData(i));

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

			setTrainedNetwork(train.getMethod());

			if (askForSave==false)
			{
				if (save.trim().equals("y"))
				{
					try {
						File networkSave = new File(savePath);
						SerializeObject.save(networkSave, (BasicNetwork)train.getMethod());
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

	}
