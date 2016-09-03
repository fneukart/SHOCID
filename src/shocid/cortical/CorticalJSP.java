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
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.util.obj.SerializeObject;
import org.encog.neural.networks.training.anneal.NeuralSimulatedAnnealing;
import org.encog.persist.EncogDirectoryPersistence;

import shocid.utilities.Util;

public class CorticalJSP {

	Properties properties = new Properties();

	String propertiesFilePath = "C:\\Users\\Florian Neukart\\workspace\\SHOCID_310\\programobject.properties";

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

	public CorticalJSP()
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

		//numberHiddenNeurons = 20;
		int numberCortices = 2;

		float floatNumberHiddenNeurons = Float.valueOf(numberHiddenNeurons).floatValue();
		float floatNumberOutputNeurons = Float.valueOf(numberOutputNeurons).floatValue();
		float floatNumberHiddenNeuronsC2 = ((floatNumberHiddenNeurons / 3) * 2) + floatNumberOutputNeurons;
		float floatNumberCommonCortexNeurons = ((floatNumberHiddenNeuronsC2 / 3) * 2) * numberCortices + floatNumberOutputNeurons;

		int numberHiddenNeuronsC2 = Math.round(floatNumberHiddenNeuronsC2);
		int numberCommonCortexNeurons = Math.round(floatNumberCommonCortexNeurons);
		
		System.out.println("tslength: "+trainingSet.size());

		if (onlyPositiveInput)
		{
			BasicLayer cortex1Layer1, cortex1Layer2, cortex2Layer1, cortex2Layer2;
			network = new BasicNetwork();
			network.addLayer(new BasicLayer(new ActivationSigmoid(), true, numberInputNeurons));//0
			network.addLayer(cortex1Layer1 = new BasicLayer(new ActivationSigmoid(), true, numberHiddenNeurons));//1
			network.addLayer(cortex1Layer2 = new BasicLayer(new ActivationSigmoid(), true, numberHiddenNeuronsC2));//2
			network.addLayer(cortex2Layer1 = new BasicLayer(new ActivationSigmoid(), true, numberHiddenNeurons));//3
			network.addLayer(cortex2Layer2 = new BasicLayer(new ActivationSigmoid(), true, numberHiddenNeuronsC2));//4
			network.addLayer(new BasicLayer(new ActivationSigmoid(), true, numberCommonCortexNeurons));//5
			network.addLayer(new BasicLayer(new ActivationSigmoid(), false, numberOutputNeurons));//6

			cortex1Layer1.setContextFedBy(cortex1Layer2);
			cortex2Layer1.setContextFedBy(cortex2Layer2);
			network.getStructure().finalizeStructure();
			network.reset();

			//disable the automatically set connections between the cortexlayers
			for (int nc1 = 0; nc1 < network.getLayerNeuronCount(2); nc1++)
			{
				for (int nc2 = 0; nc2 < network.getLayerNeuronCount(3); nc2++)
				{
					network.enableConnection(2, nc1, 3, nc2, false);
				}
			}

			//enable the connections between the second cortex layer and the commoncortexlayer
			for (int nc1 = 0; nc1 < network.getLayerNeuronCount(2); nc1++)
			{
				for (int nc2 = 0; nc2 < network.getLayerNeuronCount(5); nc2++)
				{
					network.enableConnection(2, nc1, 5, nc2, true);
				}
			}

			//enable the connections between the input layer and the first hidden layer of cortex 2
			for (int nc1 = 0; nc1 < network.getLayerNeuronCount(0); nc1++)
			{
				for (int nc2 = 0; nc2 < network.getLayerNeuronCount(3); nc2++)
				{
					network.enableConnection(0, nc1, 3, nc2, true);
				}
			}
		}
		else
		{
			BasicLayer cortex1Layer1, cortex1Layer2, cortex2Layer1, cortex2Layer2;
			network = new BasicNetwork();
			network.addLayer(new BasicLayer(new ActivationSigmoid(), true, numberInputNeurons));//0
			network.addLayer(cortex1Layer1 = new BasicLayer(new ActivationSigmoid(), true, numberHiddenNeurons));//1
			network.addLayer(cortex1Layer2 = new BasicLayer(new ActivationSigmoid(), true, numberHiddenNeuronsC2));//2
			network.addLayer(cortex2Layer1 = new BasicLayer(new ActivationSigmoid(), true, numberHiddenNeurons));//3
			network.addLayer(cortex2Layer2 = new BasicLayer(new ActivationSigmoid(), true, numberHiddenNeuronsC2));//4
			network.addLayer(new BasicLayer(new ActivationTANH(), true, numberCommonCortexNeurons));//5
			network.addLayer(new BasicLayer(new ActivationTANH(), false, numberOutputNeurons));//6

			cortex1Layer1.setContextFedBy(cortex1Layer2);
			cortex2Layer1.setContextFedBy(cortex2Layer2);
			network.getStructure().finalizeStructure();
			network.reset();

			//disable the automatically set connections between the cortexlayers
			for (int nc1 = 0; nc1 < network.getLayerNeuronCount(2); nc1++)
			{
				for (int nc2 = 0; nc2 < network.getLayerNeuronCount(3); nc2++)
				{
					network.enableConnection(2, nc1, nc2, false);
				}
			}

			//enable the connections between the second cortex layer and the commoncortexlayer
			for (int nc1 = 0; nc1 < network.getLayerNeuronCount(2); nc1++)
			{
				for (int nc2 = 0; nc2 < network.getLayerNeuronCount(5); nc2++)
				{
					network.enableConnection(2, nc1, 5, nc2, true);
				}
			}

			//enable the connections between the input layer and the first hidden layer of cortex 2
			for (int nc1 = 0; nc1 < network.getLayerNeuronCount(0); nc1++)
			{
				for (int nc2 = 0; nc2 < network.getLayerNeuronCount(3); nc2++)
				{
					network.enableConnection(0, nc1, 3, nc2, true);
				}
			}

		}

		CalculateScore score = new TrainingSetScore(trainingSet);
		final double startTemp = 50;
		final double stopTemp = 2; 
		final int cycles = 400;

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
		while (counter < 1453); //allowed missclassification at learning is 5 %
		//while (counter < 5);


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
					//EncogDirectoryPersistence.saveObject(new File(savePath), network);
					//SerializeObject.save(new File(savePath), network);
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
