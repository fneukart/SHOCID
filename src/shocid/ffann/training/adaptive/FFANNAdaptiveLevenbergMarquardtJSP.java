package shocid.ffann.training.adaptive;

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
import java.util.HashMap;
import java.util.Properties;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.mathutil.randomize.NguyenWidrowRandomizer;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.strategy.RequiredImprovementStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.util.obj.SerializeObject;
import org.encog.neural.networks.training.lma.LevenbergMarquardtTraining;
import org.encog.neural.pattern.FeedForwardPattern;


public class FFANNAdaptiveLevenbergMarquardtJSP {

	Properties properties = new Properties();

	String propertiesFilePath = "C:\\Users\\Florian Neukart\\workspace\\SHOCID\\programobject.properties";

	static int epochSL = 0;
	static int epochML = 0;
	static int epochBest = 0;
	String brsavgyn = null;

	String savePath =null;
	String logPath = null;

	String imputationPath = "imputation\\imputation_FFANNLM.net";
	private static int numberInputNeurons = 0;
	private static MLDataSet trainingData = null;
	private static BasicNetwork nw = null;
	private static BasicNetwork nwt = null;
	private static double[][][] inputValuesArraySHL = null;
	private static double[][][] inputValuesArrayMHL = null;
	private static double[][][] outputValuesArraySHL = null;
	private static double[][][] outputValuesArrayMHL = null;
	private static double[][][] idealValuesArraySHL = null;
	private static double[][][] idealValuesArrayMHL = null;
	private static double[][] outputValuesArrayBestSHL = null;
	private static double[][] outputValuesArrayBestMHL = null;
	private static double[][] outputValuesArrayBestOverall = null;
	private static ArrayList<Double> errorArrayList = new ArrayList<Double>();
	private static ArrayList<BasicNetwork> adaptiveNetworkArraySHL = new ArrayList<BasicNetwork>();
	private static ArrayList<BasicNetwork> adaptiveNetworkArrayMHL = new ArrayList<BasicNetwork>();
	private static HashMap<String, Integer> adaptiveNetworkArrayAndHiddenNeuronsSHL = new HashMap<String, Integer>(); //the hashmap containing the network names and the hidden neurons of the single-hidden-layer ANNs
	private static HashMap<String, Integer> adaptiveNetworkArrayAndHiddenNeuronsMHL1 = new HashMap<String, Integer>(); //the hashmap containing the network names and the hidden neurons of the multi-hidden-layer ANNs first layer
	private static HashMap<String, Integer> adaptiveNetworkArrayAndHiddenNeuronsMHL2 = new HashMap<String, Integer>(); //the hashmap containing the network names and the hidden neurons of the multi-hidden-layer ANNs second layer
	int numberSHLNetworks = 0;
	int numberMHLNetworks = 0;
	static double smallestErrorNetworkSHL = 100.0; //holds the smallest error value - initialized with 100 as the verification within the training compares the actual one with the last one
	static double smallestErrorNetworkMHL = 100.0; //holds the smallest error value - initialized with 100 as the verification within the training compares the actual one with the last one
	static int smallestErrorNetworkNumberSHL = 0; //holds the network number with the smallest error value
	static int smallestErrorNetworkNumberMHL = 0; //holds the network number with the smallest error value

	private static double[] error = null;
	private static double[] errorSL = null;
	private static double[] errorML = null;
	private static double[] errorBestNetworkSHL = null;
	private static double[] errorBestNetworkMHL = null;
	private static double[] errorBestNetwork = null;
	static ArrayList<Double>[] errorArrayListSL = null;//new (ArrayList<Double>[])new ArrayList[10];
	static ArrayList<Double>[] errorArrayListML = null;//new (ArrayList<Double>[])new ArrayList[10];
	static HashMap<Integer, Integer> epochArrayListSL = new HashMap<Integer, Integer>();
	static HashMap<Integer, Integer> epochArrayListML = new HashMap<Integer, Integer>();

	private static int bestNetworkHiddenNeurons1 = 0;
	private static int bestNetworkHiddenNeurons2 = 0;
	private static double bestNetworkQualitySHL = 100.0;
	private static double bestNetworkQualityMHL = 100.0;
	private static double bestNetworkQualityOverall = 0.0;
	private static double[] qualityCalculationArray;
	private static double[] qualitySHLArray;
	private static double[] qualityMHLArray;
	private static BasicNetwork bestSHLNetwork = new BasicNetwork();
	private static BasicNetwork bestMHLNetwork = new BasicNetwork();
	private static BasicNetwork bestNetwork = new BasicNetwork();
	
	public FFANNAdaptiveLevenbergMarquardtJSP()
	{
		//run(network, numberInputNeurons, numberHiddenNeurons, numberOutputNeurons, trainingSet);
	}


	public void run(int numberInputNeurons, int numberHiddenNeurons, int numberOutputNeurons, MLDataSet trainingSet, boolean onlyPositiveInput, boolean askForSave, String save, double allowedError)
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

		savePath = properties.getProperty("application.saveAdaptiveLevenbergMarquardtAgentPath")+"FFANNLMAA_"+getDateTime()+"_.net";
		logPath = properties.getProperty("application.logPath")+"LOG_ffannlmaa_"+getDateTime()+".txt";

		/*methods needed for the GUI
		 *=======================================================
		 */
		setNumberInputNeurons(numberInputNeurons);
		setTrainingSet(trainingSet);
		String inputValues = null;


		/*
		 *======================================================= 
		 */

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

		//defines the number of lines in an array for storing and calculating the quality values
		defineQualityCalculationArray(line + 1);


		//==================================================================determination of single hidden layer ANNs and variables START==================================================================

		//double calculations of the first hidden layer's maximum and minimum number of neurons
		double doubleNumberInputNeurons = Double.valueOf(numberInputNeurons).doubleValue();
		double nh1minDouble = (doubleNumberInputNeurons / 3) * 2;
		double nh1maxDouble = (doubleNumberInputNeurons / 3) * 4;

		//integer values of the first hidden layer's maximum and minimum number of neurons
		int nh1min = (int) Math.round(nh1minDouble); // minimum number of hidden neurons per layer
		int nh1max = (int) Math.round(nh1maxDouble); // maximum number of hidden neurons per layer
		int networkCounter = 0;


		// operations for ANNs with 1 hidden layer down to the lower boundary
		for (int n = nh1min; n <= numberHiddenNeurons; n++)
		{
			//set the unique network key for the hashmap
			String networkName = "network"+String.valueOf(networkCounter);

			//adds the current network to the list of generated networks
			adaptiveNetworkArrayAndHiddenNeuronsSHL.put(networkName, n);
			//increase network counter by 1
			networkCounter = networkCounter+1;
		}

		// operations for ANNs with 1 hidden layer up to the upper boundary
		for (int n = numberHiddenNeurons; n < nh1max; n++)
		{
			//set the unique network key for the hashmap
			String networkName = "network"+String.valueOf(networkCounter);

			//adds the current network to the list of generated networks
			adaptiveNetworkArrayAndHiddenNeuronsSHL.put(networkName, n);
			//increase network counter by 1
			networkCounter = networkCounter+1;
		}

		//determines the number of the created single hidden layer ANNs
		numberSHLNetworks = networkCounter;
		defineQualitySHLArray(networkCounter);

		//defines the single layer arrays of error-array lists
		errorArrayListSL = (ArrayList<Double>[])new ArrayList[networkCounter];
		
		//resets the networkCounter
		networkCounter = 0;
		//==================================================================determination of single hidden layer ANNs and variables  END==================================================================

		//==================================================================determination of multiple hidden layer ANNs and variables  START==================================================================
		//this considers the number of the hashmap's single layer ANNs
		for (int currentSLNetwork = 0; currentSLNetwork < numberSHLNetworks; currentSLNetwork++)
		{
			String currentSLNetworkKey = "network"+String.valueOf(currentSLNetwork);

			//get the number of the current network's hidden layer's neurons 
			int currentSLNetworkHiddenNeurons = adaptiveNetworkArrayAndHiddenNeuronsSHL.get(currentSLNetworkKey);

			double currentNetworknh2minDouble = ((Double.valueOf(adaptiveNetworkArrayAndHiddenNeuronsSHL.get(currentSLNetworkKey).doubleValue()) / 3) * 2);
			double currentNetworknh2maxDouble = ((Double.valueOf(adaptiveNetworkArrayAndHiddenNeuronsSHL.get(currentSLNetworkKey).doubleValue()) / 3) * 4);

			int currentNetworknh2min = (int) Math.round(currentNetworknh2minDouble); // minimum number of hidden neurons per layer
			int currentNetworknh2max = (int) Math.round(currentNetworknh2maxDouble); // minimum number of hidden neurons per layer

			//calculation and training for all ANNs where the number of the hidden neurons of the second layer is smaller than the number of the hidden neurons of the first layer
			for (int n = currentNetworknh2min; n <= currentSLNetworkHiddenNeurons; n++)
			{					
				networkCounter = networkCounter+1;
			}

			//calculation and training for all ANNs where the number of the hidden neurons of the second layer is larger than the number of the hidden neurons of the first layer
			for (int n = currentSLNetworkHiddenNeurons; n < currentNetworknh2max; n++)
			{	
				networkCounter = networkCounter+1;
			}
		}

		//determines the number of the created multiple hidden layer ANNs
		numberMHLNetworks = networkCounter + 1;
		defineQualityMHLArray(networkCounter + 1);
		
		//defines the multi layer arrays of error-array lists
		errorArrayListML = (ArrayList<Double>[])new ArrayList[networkCounter + 1];
		//==================================================================determination of multiple hidden layer ANNs and variables  END==================================================================

		//the HashMap is being used later in the calculations again and therefore has to be cleared
		adaptiveNetworkArrayAndHiddenNeuronsSHL.clear();

		inputValuesArraySHL = new double[numberSHLNetworks][line][numberInputNeurons];
		inputValuesArrayMHL = new double[numberMHLNetworks][line][numberInputNeurons];

		outputValuesArraySHL = new double[numberSHLNetworks][line][numberOutputNeurons];
		outputValuesArrayMHL = new double[numberMHLNetworks][line][numberOutputNeurons];

		outputValuesArrayBestSHL = new double[line][numberOutputNeurons]; //will hold the output values of the best SLHANN
		outputValuesArrayBestMHL = new double[line][numberOutputNeurons]; //will hold the output values of the best MLHANN

		idealValuesArraySHL = new double[numberSHLNetworks][line][numberOutputNeurons];
		idealValuesArrayMHL = new double[numberMHLNetworks][line][numberOutputNeurons];




		//==================================================================single hidden layer ANN calculation START==================================================================
		networkCounter = 0;

		// operations for ANNs with 1 hidden layer down to the lower boundary
		for (int n = nh1min; n <= numberHiddenNeurons; n++)
		{
			BasicNetwork network = new BasicNetwork();
			final FeedForwardPattern pattern = new FeedForwardPattern();
			network = (BasicNetwork) pattern.generate();
			
			if (onlyPositiveInput)
			{
				network.addLayer(new BasicLayer(new ActivationSigmoid(),true,numberInputNeurons));
				network.addLayer(new BasicLayer(new ActivationSigmoid(),true,n));
				network.addLayer(new BasicLayer(new ActivationSigmoid(),true,numberOutputNeurons));
			}
			else
			{
				network.addLayer(new BasicLayer(new ActivationTANH(),true,numberInputNeurons));
				network.addLayer(new BasicLayer(new ActivationTANH(),true,n));
				network.addLayer(new BasicLayer(new ActivationTANH(),true,numberOutputNeurons));
			}
			
			network.getStructure().finalizeStructure();
			network.reset();
			
			(new NguyenWidrowRandomizer()).randomize(network);


			// train the neural network
			final LevenbergMarquardtTraining train = new LevenbergMarquardtTraining(network, trainingSet);

			// reset if improve is less than 1% over 5 cycles
			train.addStrategy(new RequiredImprovementStrategy(5));
			
			int epoch = 1;

			//the definition of the array of arraylists (done beforehand with errorArrayListML = (ArrayList<Double>[])new ArrayList[networkCounter];) is not enough
			errorArrayListSL[networkCounter] = new ArrayList<Double>();
			do {
				train.iteration();
				//System.out.println("Epoch #" + epochSL + " Error:" + train.getError());

				//add the error of the epoch to the ErrorArrayList
				setError(Double.valueOf(train.getError()));
				setErrorsSL(networkCounter, Double.valueOf(train.getError()));
				epoch++;
			} while ((epoch < 5000) && (train.getError() > allowedError));
			
			//sets the epochs of the current ANN
			setEpochsSL(networkCounter, epoch);

			train.finishTraining();

			network = (BasicNetwork) train.getMethod();

			// test the neural network
			System.out.println("Results SHL Network # "+networkCounter+" with "+ n +" hidden neurons:");
			//		for(MLDataPair pair: trainingSet ) {
			//			final MLData output = network.compute(pair.getInput());
			//			System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1)
			//					+ ", actual=" + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
			//		}

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

			quality = calculateANNQuality(getQualityCalculationArray());

			// set the current network's quality
			setQualitySHLArray(networkCounter, quality);

			setNetwork(network);

			line=0;

			//create the logfile for the current training
			File logFile = new File(logPath+logPath+"logFile");

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
					setInputValuesSHL(networkCounter, line, i, pair.getInput().getData(i));
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
					setIdealValuesSHL(networkCounter, line, i, pair.getIdeal().getData(i));

					//set the resulting output values
					setOutputValuesSHL(networkCounter, line, i, output.getData(i));
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

			setTrainedNetwork((BasicNetwork) train.getMethod());


			//set the unique network key for the hashmap
			String networkName = "network"+String.valueOf(networkCounter);

			//adds the current network to the list of generated networks
			setAdaptiveNetworkSHL(getTrainedNetwork());
			adaptiveNetworkArrayAndHiddenNeuronsSHL.put(networkName, n);

			//if the current quality is smaller than the set one, replace the error value and the network number
			if (quality < getBestSHLNetworkQuality())
			{
				setBestSHLNetworkQuality(quality);
				setSmallestErrorSHL(train.getError());
				setSmallestErrorNetworkNumberSHL(networkCounter);
				setBestEpochsSL(epoch);
				setErrorBestNetworkSHL(getError());
				setBestSHLNetwork(getTrainedNetwork());
				setOutputValuesBestSHL(getOutputValuesSHL(networkCounter));
			}

			//increase network counter by 1
			networkCounter = networkCounter+1;
			errorArrayList.clear();
		}

		// operations for ANNs with 1 hidden layer up to the upper boundary
		for (int n = numberHiddenNeurons; n < nh1max; n++)
		{
			BasicNetwork network = new BasicNetwork();
			final FeedForwardPattern pattern = new FeedForwardPattern();
			network = (BasicNetwork) pattern.generate();

			if (onlyPositiveInput)
			{
				network.addLayer(new BasicLayer(new ActivationSigmoid(),true,numberInputNeurons));
				network.addLayer(new BasicLayer(new ActivationSigmoid(),true,n));
				network.addLayer(new BasicLayer(new ActivationSigmoid(),true,numberOutputNeurons));
			}
			else
			{
				network.addLayer(new BasicLayer(new ActivationTANH(),true,numberInputNeurons));
				network.addLayer(new BasicLayer(new ActivationTANH(),true,n));
				network.addLayer(new BasicLayer(new ActivationTANH(),true,numberOutputNeurons));
			}

			
			network.getStructure().finalizeStructure();
			network.reset();

			(new NguyenWidrowRandomizer()).randomize(network);
			
			// train the neural network
			final LevenbergMarquardtTraining train = new LevenbergMarquardtTraining(network, trainingSet);

			// reset if improve is less than 1% over 5 cycles
			train.addStrategy(new RequiredImprovementStrategy(5));

			int epoch = 1;
			
			//the definition of the array of arraylists (done beforehand with errorArrayListML = (ArrayList<Double>[])new ArrayList[networkCounter];) is not enough
			errorArrayListSL[networkCounter] = new ArrayList<Double>();
			do {
				train.iteration();
				//System.out.println("Epoch #" + epochSL + " Error:" + train.getError());

				//add the error of the epoch to the ErrorArrayList
				setError(Double.valueOf(train.getError()));
				setErrorsSL(networkCounter, Double.valueOf(train.getError()));
				epoch++;
			} while ((epoch < 5000) && (train.getError() > allowedError));
			
			//sets the epochs of the current ANN
			setEpochsSL(networkCounter, epoch);

			train.finishTraining();

			network = (BasicNetwork) train.getMethod();

			// test the neural network
			System.out.println("Results SHL Network # "+networkCounter+" with "+ n +" hidden neurons:");
			//		for(MLDataPair pair: trainingSet ) {
			//			final MLData output = network.compute(pair.getInput());
			//			System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1)
			//					+ ", actual=" + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
			//		}

			//calculate the quality of the current ANN
			double quality = 0.0;
			int currentLine = 0;
			for(MLDataPair pair: trainingSet )
			{
				final MLData output = network.compute(pair.getInput());

				quality = (pair.getIdeal().getData(0) - output.getData(0));

				if (quality < 0)
				{
					quality*=-1;
				}

				//set the quality of the current output in the calculation array
				setQualityCalculationArray(currentLine, quality);
				currentLine+=1;
			}

			quality = calculateANNQuality(getQualityCalculationArray());

			// set the current network's quality
			setQualitySHLArray(networkCounter, quality);

			setNetwork(network);

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
					setInputValuesSHL(networkCounter, line, i, pair.getInput().getData(i));
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
					setIdealValuesSHL(networkCounter, line, i, pair.getIdeal().getData(i));

					//set the resulting output values
					setOutputValuesSHL(networkCounter, line, i, output.getData(i));
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

			setTrainedNetwork((BasicNetwork) train.getMethod());


			//set the unique network key for the hashmap
			String networkKey = "network"+String.valueOf(networkCounter);

			//adds the current network to the list of generated networks
			setAdaptiveNetworkSHL(getTrainedNetwork());
			adaptiveNetworkArrayAndHiddenNeuronsSHL.put(networkKey, n); //sets the unique network name and the number of hidden neurons for this layer

			//if the current quality is smaller than the set one, replace the error value and the network number
			if (quality < getBestSHLNetworkQuality())
			{
				setBestSHLNetworkQuality(quality);
				setSmallestErrorSHL(train.getError());
				setSmallestErrorNetworkNumberSHL(networkCounter);
				setBestEpochsSL(epoch);
				setErrorBestNetworkSHL(getError());
				setBestSHLNetwork(getTrainedNetwork());
				setOutputValuesBestSHL(getOutputValuesSHL(networkCounter));
			}

			//increase network counter by 1
			networkCounter = networkCounter+1;
			errorArrayList.clear();
		}
		//	what's this? i don't remember...
		//setOutputValuesBestSHL(getOutputValuesSHL(networkCounter-1));

		//resets the networkCounter
		networkCounter = 0;
		//==================================================================single hidden layer ANN calculation END==================================================================

		//==================================================================multiple hidden layer ANN calculation START==================================================================
		//this considers the number of the hashmap's single layer ANNs
		for (int currentSLNetwork = 0; currentSLNetwork < numberSHLNetworks; currentSLNetwork++)
		{
			String currentSLNetworkKey = "network"+String.valueOf(currentSLNetwork);

			//get the number of the current network's hidden layer's neurons 
			int currentSLNetworkHiddenNeurons = adaptiveNetworkArrayAndHiddenNeuronsSHL.get(currentSLNetworkKey);

			double currentNetworknh2minDouble = ((adaptiveNetworkArrayAndHiddenNeuronsSHL.get(currentSLNetworkKey) / 3) * 2);
			double currentNetworknh2maxDouble = ((adaptiveNetworkArrayAndHiddenNeuronsSHL.get(currentSLNetworkKey) / 3) * 4);

			int currentNetworknh2min = (int) Math.round(currentNetworknh2minDouble); // minimum number of hidden neurons per layer
			int currentNetworknh2max = (int) Math.round(currentNetworknh2maxDouble); // minimum number of hidden neurons per layer

			//calculation and training for all ANNs where the number of the hidden neurons of the second layer is smaller than the number of the hidden neurons of the first layer

			for (int n = currentSLNetworkHiddenNeurons; n >= currentNetworknh2min; n--)
			{
				BasicNetwork network = new BasicNetwork();
				final FeedForwardPattern pattern = new FeedForwardPattern();
				network = (BasicNetwork) pattern.generate();

				if (onlyPositiveInput)
				{
					network.addLayer(new BasicLayer(new ActivationSigmoid(),true,numberInputNeurons));
					//define the first layer with the number of hidden neurons also the first sl ANN in the array has
					network.addLayer(new BasicLayer(new ActivationSigmoid(),true,currentSLNetworkHiddenNeurons));
					//set the neurons of the second hidden layer according to the 
					network.addLayer(new BasicLayer(new ActivationSigmoid(),true,n));
					network.addLayer(new BasicLayer(new ActivationSigmoid(),true,numberOutputNeurons));
				}
				else
				{
					network.addLayer(new BasicLayer(new ActivationTANH(),true,numberInputNeurons));
					//define the first layer with the number of hidden neurons also the first sl ANN in the array has
					network.addLayer(new BasicLayer(new ActivationTANH(),true,currentSLNetworkHiddenNeurons));
					//set the neurons of the second hidden layer according to the 
					network.addLayer(new BasicLayer(new ActivationTANH(),true,n));
					network.addLayer(new BasicLayer(new ActivationTANH(),true,numberOutputNeurons));
				}

				
				network.getStructure().finalizeStructure();
				network.reset();

				(new NguyenWidrowRandomizer()).randomize(network);
				
				// train the neural network
				final LevenbergMarquardtTraining train = new LevenbergMarquardtTraining(network, trainingSet);

				// reset if improve is less than 1% over 5 cycles
				train.addStrategy(new RequiredImprovementStrategy(5));

				int epoch = 1;
				
				//the definition of the array of arraylists (done beforehand with errorArrayListML = (ArrayList<Double>[])new ArrayList[networkCounter];) is not enough
				errorArrayListML[networkCounter] = new ArrayList<Double>();
				do {
					train.iteration();
					//System.out.println("Epoch #" + epochML + " Error:" + train.getError());

					//add the error of the epoch to the ErrorArrayList
					setError(Double.valueOf(train.getError()));
					setErrorsML(networkCounter, Double.valueOf(train.getError()));
					epoch++;
				} while ((epoch < 5000) && (train.getError() > allowedError));
				
				//sets the epochs of the current ANN
				setEpochsML(networkCounter, epoch);

				train.finishTraining();

				network = (BasicNetwork) train.getMethod();

				// test the neural network
				System.out.println("Results MHL Network # "+networkCounter+" with "+ currentSLNetworkHiddenNeurons + " hidden L1 and "+ n +" hidden L2 neurons:");
				//		for(MLDataPair pair: trainingSet ) {
				//			final MLData output = network.compute(pair.getInput());
				//			System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1)
				//					+ ", actual=" + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
				//		}

				//calculate the quality of the current ANN
				double quality = 0.0;
				int currentLine = 0;
				for(MLDataPair pair: trainingSet )
				{
					final MLData output = network.compute(pair.getInput());

					quality = (pair.getIdeal().getData(0) - output.getData(0));

					if (quality < 0)
					{
						quality*=-1;
					}

					//set the quality of the current output in the calculation array
					setQualityCalculationArray(currentLine, quality);
					currentLine+=1;
				}

				quality = calculateANNQuality(getQualityCalculationArray());

				// set the current network's quality
				setQualityMHLArray(networkCounter, quality);

				setNetwork(network);


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
						setInputValuesMHL(networkCounter, line, i, pair.getInput().getData(i));
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
						setIdealValuesMHL(networkCounter, line, i, pair.getIdeal().getData(i));

						//set the resulting output values
						setOutputValuesMHL(networkCounter, line, i, output.getData(i));
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

				setTrainedNetwork((BasicNetwork) train.getMethod());

				//set the unique network key for the hashmap
				String networkKey = "network"+String.valueOf(networkCounter);

				//adds the current network to the list of generated networks
				setAdaptiveNetworkMHL(getTrainedNetwork());
				adaptiveNetworkArrayAndHiddenNeuronsMHL1.put(networkKey, currentSLNetworkHiddenNeurons); // set the first hidden layer
				adaptiveNetworkArrayAndHiddenNeuronsMHL2.put(networkKey, n); // set the second hidden layer

				//if the current quality is smaller than the set one, replace the error value and the network number
				if (quality < getBestMHLNetworkQuality())
				{
					setBestMHLNetworkQuality(quality);
					setSmallestErrorMHL(train.getError());
					setSmallestErrorNetworkNumberMHL(networkCounter);
					setBestEpochsML(epoch);
					setErrorBestNetworkMHL(getError());
					setBestMHLNetwork(getTrainedNetwork());
					setOutputValuesBestMHL(getOutputValuesMHL(networkCounter));
				}

				networkCounter = networkCounter+1;
				errorArrayList.clear();
			}

			//calculation and training for all ANNs where the number of the hidden neurons of the second layer is larger than the number of the hidden neurons of the first layer
			for (int n = currentSLNetworkHiddenNeurons; n < currentNetworknh2max; n++)
			{
				BasicNetwork network = new BasicNetwork();
				final FeedForwardPattern pattern = new FeedForwardPattern();
				network = (BasicNetwork) pattern.generate();

				if (onlyPositiveInput)
				{
					network.addLayer(new BasicLayer(new ActivationSigmoid(),true,numberInputNeurons));
					//define the first layer with the number of hidden neurons also the first sl ANN in the array has
					network.addLayer(new BasicLayer(new ActivationSigmoid(),true,currentSLNetworkHiddenNeurons));
					//set the neurons of the second hidden layer according to the 
					network.addLayer(new BasicLayer(new ActivationSigmoid(),true,n));
					network.addLayer(new BasicLayer(new ActivationSigmoid(),true,numberOutputNeurons));
				}
				else
				{
					network.addLayer(new BasicLayer(new ActivationTANH(),true,numberInputNeurons));
					//define the first layer with the number of hidden neurons also the first sl ANN in the array has
					network.addLayer(new BasicLayer(new ActivationTANH(),true,currentSLNetworkHiddenNeurons));
					//set the neurons of the second hidden layer according to the 
					network.addLayer(new BasicLayer(new ActivationTANH(),true,n));
					network.addLayer(new BasicLayer(new ActivationTANH(),true,numberOutputNeurons));
				}

				
				network.getStructure().finalizeStructure();
				network.reset();
				
				(new NguyenWidrowRandomizer()).randomize(network);

				// train the neural network
				final LevenbergMarquardtTraining train = new LevenbergMarquardtTraining(network, trainingSet);

				// reset if improve is less than 1% over 5 cycles
				train.addStrategy(new RequiredImprovementStrategy(5));
				
				int epoch = 1;
				
				//the definition of the array of arraylists (done beforehand with errorArrayListML = (ArrayList<Double>[])new ArrayList[networkCounter];) is not enough
				errorArrayListML[networkCounter] = new ArrayList<Double>();
				do {
					train.iteration();
					//System.out.println("Epoch #" + epochML + " Error:" + train.getError());

					//add the error of the epoch to the ErrorArrayList
					setError(Double.valueOf(train.getError()));
					setErrorsML(networkCounter, Double.valueOf(train.getError()));
					epoch++;
				} while ((epoch < 5000) && (train.getError() > allowedError));
				
				//sets the epochs of the current ANN
				setEpochsML(networkCounter, epoch);

				train.finishTraining();

				network = (BasicNetwork) train.getMethod();

				// test the neural network
				System.out.println("Results MHL Network # "+networkCounter+" with "+ currentSLNetworkHiddenNeurons + " hidden L1 and "+ n +" hidden L2 neurons:");
				//		for(MLDataPair pair: trainingSet ) {
				//			final MLData output = network.compute(pair.getInput());
				//			System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1)
				//					+ ", actual=" + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
				//		}

				//calculate the quality of the current ANN
				double quality = 0.0;
				int currentLine = 0;
				for(MLDataPair pair: trainingSet )
				{
					final MLData output = network.compute(pair.getInput());

					quality = (pair.getIdeal().getData(0) - output.getData(0));

					if (quality < 0)
					{
						quality*=-1;
					}

					//set the quality of the current output in the calculation array
					setQualityCalculationArray(currentLine, quality);
					currentLine+=1;
				}

				quality = calculateANNQuality(getQualityCalculationArray());

				// set the current network's quality
				setQualityMHLArray(networkCounter, quality);

				setNetwork(network);

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
						setInputValuesMHL(networkCounter, line, i, pair.getInput().getData(i));
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
						setIdealValuesMHL(networkCounter, line, i, pair.getIdeal().getData(i));

						//set the resulting output values
						setOutputValuesMHL(networkCounter, line, i, output.getData(i));
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

				setTrainedNetwork((BasicNetwork) train.getMethod());


				//set the unique network key for the hashmap
				String networkKey = "network"+String.valueOf(networkCounter);

				//adds the current network to the list of generated networks
				setAdaptiveNetworkMHL(getTrainedNetwork());
				adaptiveNetworkArrayAndHiddenNeuronsMHL1.put(networkKey, currentSLNetworkHiddenNeurons); // set the first hidden layer
				adaptiveNetworkArrayAndHiddenNeuronsMHL2.put(networkKey, n); // set the second hidden layer

				//if the current quality is smaller than the set one, replace the error value and the network number
				if (quality < getBestMHLNetworkQuality())
				{
					setBestMHLNetworkQuality(quality);
					setSmallestErrorMHL(train.getError());
					setSmallestErrorNetworkNumberMHL(networkCounter);
					setBestEpochsML(epoch);
					setErrorBestNetworkMHL(getError());
					setBestMHLNetwork(getTrainedNetwork());
					setOutputValuesBestMHL(getOutputValuesMHL(networkCounter));
				}

				networkCounter = networkCounter+1;
				errorArrayList.clear();
			}
		}
		//==================================================================multiple hidden layer ANN calculation END==================================================================

		// ... again... why did i do this?
		//setOutputValuesBestMHL(getOutputValuesMHL(networkCounter-1));

		BasicNetwork bestNetwork = new BasicNetwork();
		final FeedForwardPattern bestPattern = new FeedForwardPattern();
		bestNetwork = (BasicNetwork) bestPattern.generate();

		//get the best network and its parameters - needed for GUI output
		if (getBestSHLNetworkQuality() < getBestMHLNetworkQuality())
		{
			bestNetwork = getAdaptiveNetworkSHL(getSmallestErrorNetworkNumberSHL());
			setOutputValuesBestOverall(getOutputValuesBestSHL());
			setBestHiddenLayer1Neurons(adaptiveNetworkArrayAndHiddenNeuronsSHL.get("network"+String.valueOf(getSmallestErrorNetworkNumberSHL())));
			setBestEpochs(getBestEpochsSL());
			setBestNetwork(getBestSHLNetwork());
			setErrorBestNetwork(getErrorBestNetworkSHL());
		}
		else
		{
			bestNetwork = getAdaptiveNetworkMHL(getSmallestErrorNetworkNumberMHL());
			setOutputValuesBestOverall(getOutputValuesBestMHL());
			setBestHiddenLayer1Neurons(adaptiveNetworkArrayAndHiddenNeuronsMHL1.get("network"+String.valueOf(getSmallestErrorNetworkNumberMHL())));
			setBestHiddenLayer2Neurons(adaptiveNetworkArrayAndHiddenNeuronsMHL2.get("network"+String.valueOf(getSmallestErrorNetworkNumberMHL())));
			setBestEpochs(getBestEpochsML());
			setBestNetwork(getBestMHLNetwork());
			setErrorBestNetwork(getErrorBestNetworkMHL());
		}

		//print out best ANN results and details
		if (getBestSHLNetworkQuality() < getBestMHLNetworkQuality())
		{
			System.out.println("SHL Network # "+getSmallestErrorNetworkNumberSHL()+" performed best.");
		}
		else
		{
			System.out.println("MHL Network # "+getSmallestErrorNetworkNumberMHL()+" performed best.");
		}

		System.out.println("Details: ");
		System.out.println(getBestHiddenLayer1Neurons()+" neurons in the first layer.");
		System.out.println(getBestHiddenLayer2Neurons()+" neurons in the second layer.");
		System.out.println(getBestEpochs()+" epochs needed for training.");


		System.out.println("SHL quality results:");
		for (int i = 0; i < getQualitySHLArray().length; i++)
		{
			System.out.println("SHL Network # " + i + " has a quality of " + getQualitySHLArray(i) + " and needed " + getEpochsSL(i) + " epochs.");
		}

		System.out.println("MHL quality results:");
		for (int i = 0; i < getQualityMHLArray().length; i++)
		{
			System.out.println("MHL Network # " + i + " has a quality of " + getQualityMHLArray(i) + " and needed " + getEpochsML(i) + " epochs.");
		}


		if (askForSave==false)
		{
			if (save.trim().equals("y"))
			{
				try {
					File networkSave = new File(savePath);
					SerializeObject.save(networkSave, bestNetwork);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Network saved under " + savePath);
				System.out.println("Operations completed.");
				//System.exit(1);
				//network result = (FeedforwardNetwork)SerializeObject.load("FFANNGA.net");
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

	public void setBestSHLNetwork(BasicNetwork network)
	{
		bestSHLNetwork = network;
	}

	public static BasicNetwork getBestSHLNetwork ()
	{
		return bestSHLNetwork;
	}

	public void setBestMHLNetwork(BasicNetwork network)
	{
		bestMHLNetwork = network;
	}

	public static BasicNetwork getBestMHLNetwork ()
	{
		return bestMHLNetwork;
	}

	public void setBestNetwork(BasicNetwork network)
	{
		bestNetwork = network;
	}

	public static BasicNetwork getBestNetwork ()
	{
		return bestNetwork;
	}

	public void setInputValuesSHL(int network, int row, int col, double value)
	{
		inputValuesArraySHL[network][row][col] = value;
	}

	public static double[][][] getInputValuesSHL()
	{
		return inputValuesArraySHL;
	}

	public static double getInputValuesSHL(int network, int row, int col)
	{
		return inputValuesArraySHL[network][row][col];
	}

	public static double[][] getInputValues(int network)
	{
		return inputValuesArraySHL[network];
	}

	public void setInputValuesMHL(int network, int row, int col, double value)
	{
		inputValuesArrayMHL[network][row][col] = value;
	}

	public static double[][][] getInputValuesMHL()
	{
		return inputValuesArrayMHL;
	}

	public static double getInputValuesMHL(int network, int row, int col)
	{
		return inputValuesArrayMHL[network][row][col];
	}

	public void setOutputValuesSHL(int network, int row, int col, double value)
	{
		outputValuesArraySHL[network][row][col] = value;
	}

	public static double[][][] getOutputValuesSHL()
	{
		return outputValuesArraySHL;
	}

	public static double[][] getOutputValuesSHL(int network)
	{
		return outputValuesArraySHL[network];
	}

	public static double getOutputValuesSHL(int network, int row, int col)
	{
		return outputValuesArraySHL[network][row][col];
	}

	public void setOutputValuesMHL(int network, int row, int col, double value)
	{
		outputValuesArrayMHL[network][row][col] = value;
	}

	public static double[][][] getOutputValuesMHL()
	{
		return outputValuesArrayMHL;
	}

	public static double[][] getOutputValuesMHL(int network)
	{
		return outputValuesArrayMHL[network];
	}

	public static double getOutputValuesMHL(int network, int row, int col)
	{
		return outputValuesArrayMHL[network][row][col];
	}

	public void setIdealValuesSHL(int network, int row, int col, double value)
	{
		idealValuesArraySHL[network][row][col] = value;
	}

	public static double[][][] getIdealValuesSHL()
	{
		return idealValuesArraySHL;
	}

	public static double getIdealValuesSHL(int network, int row, int col)
	{
		return idealValuesArraySHL[network][row][col];
	}

	public static double[][] getIdealValues(int network)
	{
		return idealValuesArraySHL[network];
	}

	public void setIdealValuesMHL(int network, int row, int col, double value)
	{
		idealValuesArrayMHL[network][row][col] = value;
	}

	public static double[][][] getIdealValuesMHL()
	{
		return idealValuesArrayMHL;
	}

	public static double getIdealValuesMHL(int network, int row, int col)
	{
		return idealValuesArrayMHL[network][row][col];
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

	public static void setBestEpochsSL(int epochs)
	{
		epochSL = epochs;
	}

	public static void setBestEpochsML(int epochs)
	{
		epochML = epochs;
	}

	public static void setSmallestErrorSHL(double smallestError)
	{
		smallestErrorNetworkSHL = smallestError;
	}

	public static double getSmallestErrorSHL()
	{
		return smallestErrorNetworkSHL;
	}

	public static void setSmallestErrorMHL(double smallestError)
	{
		smallestErrorNetworkMHL = smallestError;
	}

	public static double getSmallestErrorMHL()
	{
		return smallestErrorNetworkMHL;
	}

	public static void setSmallestErrorNetworkNumberSHL(int networkNumber)
	{
		smallestErrorNetworkNumberSHL = networkNumber;
	}

	public static int getSmallestErrorNetworkNumberSHL()
	{
		return smallestErrorNetworkNumberSHL;
	}

	public static void setSmallestErrorNetworkNumberMHL(int networkNumber)
	{
		smallestErrorNetworkNumberMHL = networkNumber;
	}

	public static int getSmallestErrorNetworkNumberMHL()
	{
		return smallestErrorNetworkNumberMHL;
	}

	public static int getBestEpochsSL()
	{
		return epochSL;
	}

	public static int getBestEpochsML()
	{
		return epochML;
	}

	public void setTrainedNetwork(BasicNetwork network)
	{
		nwt = network;
	}

	public static BasicNetwork getTrainedNetwork ()
	{
		return nwt;
	}

	public static void setAdaptiveNetworkSHL(BasicNetwork n)
	{
		adaptiveNetworkArraySHL.add(n);
	}

	public static void setAdaptiveNetworkMHL(BasicNetwork n)
	{
		adaptiveNetworkArrayMHL.add(n);
	}

	public static BasicNetwork getAdaptiveNetworkSHL(int n)
	{
		return adaptiveNetworkArraySHL.get(n);
	}

	public static ArrayList<BasicNetwork> getAdaptiveNetworkSHL()
	{
		return adaptiveNetworkArraySHL;
	}
	
	public static BasicNetwork getAdaptiveNetworkMHL(int n)
	{
		return adaptiveNetworkArrayMHL.get(n);
	}
	
	public static ArrayList<BasicNetwork> getAdaptiveNetworkMHL()
	{
		return adaptiveNetworkArrayMHL;
	}
	
	public void setOutputValuesBestSHL(double[][] outputValues)
	{
		outputValuesArrayBestSHL = outputValues;
	}

	public double[][] getOutputValuesBestSHL()
	{
		return outputValuesArrayBestSHL;
	}

	public void setOutputValuesBestMHL(double[][] outputValues)
	{
		outputValuesArrayBestMHL = outputValues;
	}

	public double[][] getOutputValuesBestMHL()
	{
		return outputValuesArrayBestMHL;
	}

	public void setOutputValuesBestOverall(double[][] outputValues)
	{
		outputValuesArrayBestOverall = outputValues;
	}

	public static double[][] getOutputValuesBestOverall()
	{
		return outputValuesArrayBestOverall;
	}

	public void setErrorBestNetworkSHL(double[] errorBest)
	{
		errorBestNetworkSHL = errorBest;
	}

	public double[] getErrorBestNetworkSHL()
	{
		return errorBestNetworkSHL;
	}

	public void setErrorBestNetworkMHL(double[] errorBest)
	{
		errorBestNetworkMHL = errorBest;
	}

	public double[] getErrorBestNetworkMHL()
	{
		return errorBestNetworkMHL;
	}

	public void setErrorBestNetwork(double[] errorBest)
	{
		errorBestNetwork = errorBest;
	}

	public static double[] getErrorBestNetwork()
	{
		return errorBestNetwork;
	}

	public static void setErrorsSL(int network, double error)
	{
		errorArrayListSL[network].add(error);
	}
	
	public static double[] getErrorsSL(int network)
	{		
		errorSL = new double[errorArrayListSL[network].size()];
		for (int i=0; i<errorArrayListSL[network].size(); i++)
		{
			errorSL[i] = (double)errorArrayListSL[network].get(i);
		}
		return errorSL;
	}
	
	public static void setErrorsML(int network, double error)
	{
		errorArrayListML[network].add(error);
	}
	
	public static double[] getErrorsML(int network)
	{
		errorML = new double[errorArrayListML[network].size()];
		for (int i=0; i<errorArrayListML[network].size(); i++)
		{
			errorML[i] = (double)errorArrayListML[network].get(i);
		}
		return errorML;
	}
	
	public static void setEpochsSL(int network, int epochs)
	{
		epochArrayListSL.put(network, epochs);
	}
	
	public static int getEpochsSL(int network)
	{
		return epochArrayListSL.get(network);
	}
	
	public static void setEpochsML(int network, int epochs)
	{
		epochArrayListML.put(network, epochs);
	}
	
	public static int getEpochsML(int network)
	{
		return epochArrayListML.get(network);
	}
		
	public void setBestHiddenLayer1Neurons(int neurons)
	{
		bestNetworkHiddenNeurons1 = neurons;
	}

	public void setBestHiddenLayer2Neurons(int neurons)
	{
		bestNetworkHiddenNeurons2 = neurons;
	}

	public static int getBestHiddenLayer1Neurons()
	{
		return bestNetworkHiddenNeurons1;
	}

	public static int getBestHiddenLayer2Neurons()
	{
		return bestNetworkHiddenNeurons2;
	}

	public void setBestEpochs(int epochs)
	{
		epochBest = epochs;
	}

	public static int getBestEpochs()
	{
		return epochBest;
	}

	public void setBestSHLNetworkQuality(double quality)
	{
		bestNetworkQualitySHL = quality;
	}

	public static double getBestSHLNetworkQuality()
	{
		return bestNetworkQualitySHL;
	}

	public void setBestMHLNetworkQuality(double quality)
	{
		bestNetworkQualityMHL = quality;
	}

	public static double getBestMHLNetworkQuality()
	{
		return bestNetworkQualityMHL;
	}

	public void setBestOverallNetworkQuality(double quality)
	{
		bestNetworkQualityOverall = quality;
	}

	public double getBestOverallNetworkQuality()
	{
		return bestNetworkQualityOverall;
	}

	public void defineQualityCalculationArray(int numberInputLines)
	{
		qualityCalculationArray = new double[numberInputLines];
	}

	public void setQualityCalculationArray(int lineNumber, double quality)
	{
		qualityCalculationArray[lineNumber] = quality;
	}

	public double[] getQualityCalculationArray()
	{
		return qualityCalculationArray;
	}

	public double getQualityCalculationArray(int lineNumber)
	{
		return qualityCalculationArray[lineNumber];
	}

	public void defineQualitySHLArray(int numberNetworks)
	{
		qualitySHLArray = new double[numberNetworks];
	}

	public void setQualitySHLArray(int network, double quality)
	{
		qualitySHLArray[network] = quality;
	}

	public static double[] getQualitySHLArray()
	{
		return qualitySHLArray;
	}

	public static double getQualitySHLArray(int networkNumber)
	{
		return qualitySHLArray[networkNumber];
	}

	public void defineQualityMHLArray(int numberNetworks)
	{
		qualityMHLArray = new double[numberNetworks];
	}

	public void setQualityMHLArray(int network, double quality)
	{
		qualityMHLArray[network] = quality;
	}

	public static double[] getQualityMHLArray()
	{
		return qualityMHLArray;
	}

	public static double getQualityMHLArray(int networkNumber)
	{
		return qualityMHLArray[networkNumber];
	}

	public double calculateANNQuality(double[] quality)
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
			accuracy1 = accuracy1 / 100;
		}

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
}