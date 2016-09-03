package shocid.transgenetic.hostgeneticinformation;

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
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.mathutil.randomize.Randomizer;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.genetic.genome.Genome;
import org.encog.ml.genetic.mutate.MutatePerturb;
import org.encog.ml.genetic.population.Population;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.util.obj.SerializeObject;
import org.encog.neural.networks.training.genetic.NeuralGeneticAlgorithm;
import org.encog.neural.pattern.FeedForwardPattern;

import shocid.transgenetic.transgeneticvectors.Plasmid;
import shocid.transgenetic.transgeneticvectors.Transposon;
import shocid.utilities.Util;

public class FFANNAISTransGeneticAlgorithmJSPStd {

	Properties properties = new Properties();

	String propertiesFilePath = "C:\\Users\\Florian Neukart\\workspace\\SHOCID\\programobject.properties";

	static int epoch = 0;
	static int tgEpoch = 0;
	String brsavgyn = null;

	String savePath =null;
	String logPath = null;

	private static int numberInputNeurons = 0;
	private static MLDataSet trainingData = null;
	private static BasicNetwork nw = null;
	private static BasicNetwork tgnw = null;
	private static BasicNetwork[] nwta = null;
	private static BasicNetwork nwt = new BasicNetwork();
	private static double[][] inputValuesArray = null;
	private static double[][] outputValuesArray = null;
	private static double[][] idealValuesArray = null;
	private static ArrayList<Double> errorArrayList = new ArrayList<Double>();
	private static ArrayList<Double> tgErrorArrayList = new ArrayList<Double>();
	private static double[] error = null;
	private static double[] tgError = null;
	BasicNetwork[] geneticPool = null;//the networks which will be used as genetic pool for the transgenetic vectors
	//public FFANNBackPropagation(BasicNetwork network, int numberInputNeurons, int numberHiddenNeurons, int numberOutputNeurons, MLDataSet trainingSet)
	public FFANNAISTransGeneticAlgorithmJSPStd()
	{
		//run(network, numberInputNeurons, numberHiddenNeurons, numberOutputNeurons, trainingSet);
	}

	public void run(int numberInputNeurons, int numberHiddenNeurons, int numberOutputNeurons, MLDataSet trainingSet, boolean onlyPositiveInput, boolean askForSave, String save, double allowedError, int numberGeneticPoolMembers)
	{
		/*methods needed for the GUI
		 *=======================================================
		 */
		setNumberInputNeurons(numberInputNeurons);
		setTrainingSet(trainingSet);
		String inputValues = null;
		/*
		 *======================================================= 
		 */

		geneticPool = new BasicNetwork[numberGeneticPoolMembers];
		nwta = new BasicNetwork[numberGeneticPoolMembers];

		for (int position = 0; position < numberGeneticPoolMembers; position++)
		{
			geneticPool[position] = new BasicNetwork();
			final FeedForwardPattern pattern = new FeedForwardPattern();
			geneticPool[position] = (BasicNetwork) pattern.generate();

			if (onlyPositiveInput)
			{
				geneticPool[position].addLayer(new BasicLayer(new ActivationSigmoid(),true,numberInputNeurons));
				geneticPool[position].addLayer(new BasicLayer(new ActivationSigmoid(),true,numberHiddenNeurons));
				geneticPool[position].addLayer(new BasicLayer(new ActivationSigmoid(),true,numberOutputNeurons));	
			}
			else
			{
				geneticPool[position].addLayer(new BasicLayer(new ActivationTANH(),true,numberInputNeurons));
				geneticPool[position].addLayer(new BasicLayer(new ActivationTANH(),true,numberHiddenNeurons));
				geneticPool[position].addLayer(new BasicLayer(new ActivationTANH(),true,numberOutputNeurons));
			}

			
			geneticPool[position].getStructure().finalizeStructure();
			geneticPool[position].reset();

			CalculateScore score = new TrainingSetScore(trainingSet);

			Randomizer randomizer = new RangeRandomizer(-1,1);
			int populationSize = 5000;
			double mutationPercent = 0.1;
			double percentToMate = 0.25;

			//Util.setErrorCalculationMode(ErrorCalculationMode.ARCTAN);
			final NeuralGeneticAlgorithm train = new NeuralGeneticAlgorithm(geneticPool[position], randomizer, score, populationSize, mutationPercent, percentToMate);

			epoch = 1;

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
				//setError(Double.valueOf(train.getError()));
				epoch++;
				//set the number of epochs needed for training
				//setEpochs(epoch);

				if (train.getError() == generationError)
				{
					generationErrorCounter++;
				}
				else
				{
					generationErrorCounter = 0;
				}
			} //while ((generationErrorCounter < 100) && (train.getError() > allowedError));
			while ((generationErrorCounter < 100) && (train.getError() > allowedError*10));

			train.finishTraining();



			setGeneticPool(position, (BasicNetwork) train.getMethod());

			// test the neural network
			System.out.println("Neural Network Results:");
			//		for(MLDataPair pair: trainingSet ) {
			//			final MLData output = geneticPool[position].compute(pair.getInput());
			//			System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1)
			//					+ ", actual=" + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
			//		}


			setNetwork(geneticPool[position]);

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

			//			FileWriter outFile = null;

			//			try {
			//				outFile = new FileWriter(logFile);
			//			} catch (IOException e) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			}

			for(MLDataPair pair: trainingSet)
			{
				final MLData output = geneticPool[position].compute(pair.getInput());
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

					//set the ideal values
					setIdealValues(line, i, pair.getIdeal().getData(i));

					//set the resulting output values
					setOutputValues(line, i, output.getData(i));
				}
				line = line+1;
			}

			boolean dontSave = true;

			if (dontSave==false)
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
				}
				else
				{
					System.out.println("Operations completed - network not saved.");
					//System.exit(1);
				}
			}
		}
		transgeneticRun(numberInputNeurons, numberHiddenNeurons, numberOutputNeurons, trainingSet, onlyPositiveInput, askForSave, save, allowedError);
	}


	/*===============================================================================================================*/


	public void transgeneticRun(int numberInputNeurons, int numberHiddenNeurons, int numberOutputNeurons, MLDataSet trainingSet, boolean onlyPositiveInput, boolean askForSave, String save, double allowedError)
	{
		//allowedError = 0.0001;
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
		savePath = properties.getProperty("application.saveGeneticAgentPath")+"FFANNTGA_"+Util.getSetDateTime()+"_.net";
		logPath = properties.getProperty("application.logPath")+"LOG_ffanntga_"+Util.getSetDateTime()+".txt";

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
		Util.setSaveDirectory(properties.getProperty("application.saveGeneticAgentPath"));
		try {
			Util.writeSingleAgentInfo("FFANNTGA_"+Util.getSetDateTime()+"_.txt", "FFANNTGA_"+Util.getSetDateTime()+"_.net",numberInputNeurons, numberOutputNeurons);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		BasicNetwork network = new BasicNetwork();
		final FeedForwardPattern pattern = new FeedForwardPattern();
		network = (BasicNetwork) pattern.generate();

		if (onlyPositiveInput)
		{
			network.addLayer(new BasicLayer(new ActivationSigmoid(),true,numberInputNeurons));
			network.addLayer(new BasicLayer(new ActivationSigmoid(),true,numberHiddenNeurons));
			network.addLayer(new BasicLayer(new ActivationSigmoid(),true,numberOutputNeurons));	
		}
		else
		{
			network.addLayer(new BasicLayer(new ActivationTANH(),true,numberInputNeurons));
			network.addLayer(new BasicLayer(new ActivationTANH(),true,numberHiddenNeurons));
			network.addLayer(new BasicLayer(new ActivationTANH(),true,numberOutputNeurons));
		}

		
		network.getStructure().finalizeStructure();
		network.reset();

		CalculateScore score = new TrainingSetScore(trainingSet);

		Randomizer randomizer = new RangeRandomizer(-1,1);
		int populationSize = 5000;
		double mutationPercent = 0.1;
		double percentToMate = 0.25;


		//Util.setErrorCalculationMode(ErrorCalculationMode.ARCTAN);
		final NeuralGeneticAlgorithm train = new NeuralGeneticAlgorithm(network, randomizer, score, populationSize, mutationPercent, percentToMate);

		//		final double startTemp = 10;
		//		final double stopTemp = 2; 
		//		final int cycles = 150;
		//	
		//		final NeuralSimulatedAnnealing train = new NeuralSimulatedAnnealing(network, score, startTemp, stopTemp, cycles);

		tgEpoch = 1;

		int generationErrorCounter = 1;
		double generationError = 0.0;

		Random random = new Random();
		Genome currentGenome = null;
		Genome currentBestGenome = null;
		Genome currentWorstGenome = null;
		BasicNetwork currentBestNetwork = null;
		BasicNetwork currentWorstNetwork = null;
		double multiplicand = 0.0;
		int cloneFactor = 0;
		MutatePerturb mutationLow = new MutatePerturb(2.0);
		MutatePerturb mutationHigh = new MutatePerturb(6.0);
		int shrinkedPopulationSize = 0;
		double worstLastIteration = 0.0;

		do {
			if (tgEpoch > 1)
			{
				generationError = train.getError();
			}

			/*===transgenetic vectors start*/


			/*==the plasmid vector====*/

			Plasmid plasmid = new Plasmid(getGeneticPool(), (BasicNetwork) train.getMethod(), train);
			plasmid.weightPlasmid("GA");

			/*=======================*/

			/*==the transposon vector====*/

			Transposon transposon = new Transposon(getGeneticPool(), (BasicNetwork) train.getMethod(), train);
			transposon.transposon("GA");

			/*=======================*/

			/*========Clonal Selection and Hypermutation Start============*/


			/*quality of the current worst genome for danger theory
			altough it is being deleted below, the quality value of this individual serves as threshold for
			danger theory - strange, isn't it?
			but it's like that - I decided it so.
			determine the quality of the worst ANN*/
			double currentWorst = 0;
			double currentBest = 100.0;
			double replace = 0.0;
			int currentWorstPosition = 0;
			int currentBestPosition = 0;
			//don't do it in the first generation, because in the first generation the currentWorstScore and currentBestScore are the same
			if (tgEpoch > 1)
			{
				//reset currentBest after each generation
				currentBest = 100.0;
				for (int w = 0; w < train.getGenetic().getPopulation().size(); w++)
				{
					replace = train.getGenetic().getPopulation().get(w).getScore();
					//the best network
					if (replace < currentBest)
					{
						currentBestNetwork = (BasicNetwork) train.getGenetic().getPopulation().get(w).getOrganism();
						currentBest = replace;
						currentBestPosition = w;
						currentBestGenome = train.getGenetic().getPopulation().get(w);
					}
				}				

				multiplicand = Util.getANNQuality((BasicNetwork)train.getGenetic().getPopulation().get(currentBestPosition).getOrganism(), trainingSet);
				cloneFactor = (int) (Math.round(((populationSize / 50) * multiplicand)));

				currentGenome = currentBestGenome;

				for (int c = 0; c < cloneFactor; c++)
				{
					//clone, insert and mutate the superchromosomes
					for (int i = 0; i < currentGenome.getChromosomes().size(); i++)
					{
						if (c%2==0)
						{
							//perform low mutation for clones with even numbers
							//train.getGenetic().getMutate().performMutation(currentBestGenomeChromosomes.get(i));
							mutationLow.performMutation(currentGenome.getChromosomes().get(i));	
						}
						else
						{
							//perform hypermutation for clones with odd numbers
							mutationHigh.performMutation(currentGenome.getChromosomes().get(i));
						}
					}
					//add the cloned and mutated genome to the population
					train.getGenetic().getPopulation().add(currentGenome);
				}

				//determine the worst genome anew after each deletion
				for (int c = 0; c < cloneFactor; c++)
				{
					currentWorst = 0.0;
					for (int w = 0; w < train.getGenetic().getPopulation().size(); w++)
					{
						replace = train.getGenetic().getPopulation().get(w).getScore();
						//the worst network
						if (replace > currentWorst)
						{
							currentWorstNetwork = (BasicNetwork) train.getGenetic().getPopulation().get(w).getOrganism();
							currentWorst = replace;
							currentWorstPosition = w;
							//currentWorstGenome = train.getGenetic().getPopulation().get(w);
						}
					}
					//delete the worst genome from the population
					train.getGenetic().getPopulation().remove(train.getGenetic().getPopulation().get(currentWorstPosition));
				}
				System.out.println("Genome cloned successfully.");

				/*========Clonal Selection and Hypermutation End============*/


				worstLastIteration = 0.0;
				//get current worst individual for danger theory - as iteration happens before, it is the worst of the last iteration
				for (int w = 0; w < train.getGenetic().getPopulation().size(); w++)
				{
					replace = train.getGenetic().getPopulation().get(w).getScore();
					//the worst network
					if (replace > worstLastIteration)
					{
						worstLastIteration = replace;
					}
				}
			}
			//the iteration must be done here, as the danger theory targets the worst individual of the last iteration.
			train.iteration();

			if (tgEpoch > 1)
			{
				/*========Danger theory, virus attack and hyperrecombination Start============*/

				/*the first deletion of weak individuals can only happen after the first iteration, as the worst genome of the last iteration
				serves as threshold*/
				int eliminationCounter = 0;
				List<Integer> eliminationCandidates = new ArrayList<Integer>();

				for (int ps = 0; ps < train.getGenetic().getPopulation().size(); ps++)
				{
					if (train.getGenetic().getPopulation().get(ps).getScore() >= (worstLastIteration/110)*100)
					{
						//train.getGenetic().getPopulation().remove(train.getGenetic().getPopulation().getSpecific(ps));
						eliminationCandidates.add(ps);
						eliminationCounter++;
					}
				}

				if (eliminationCounter < train.getGenetic().getPopulation().size() - 2)
				{
					Population replacementPopulation = train.getGenetic().getPopulation();
					shrinkedPopulationSize = train.getGenetic().getPopulation().size() - eliminationCounter;
					//train.dangerTheoryIteration(shrinkedPopulationSize, eliminationCounter);
					train.dangerTheoryIteration(eliminationCounter/2, eliminationCounter, worstLastIteration);
					//System.out.println("Generation: " + tgEpoch);
					System.out.println("Population size shrinked from "+train.getGenetic().getPopulation().size()+" to "+shrinkedPopulationSize);

					//					int removerHelper = 0;
					//
					//					for (int ec = 0; ec < Math.round(eliminationCounter/1); ec++)
					//					{
					//						replacementPopulation.remove(replacementPopulation.get((eliminationCandidates.get(ec))-removerHelper));
					//						removerHelper += 1;
					//					}
					//					System.out.println("replacementpopulationsize: " + replacementPopulation.size());
					//
					//
					//					int fatherInt = 0;
					//					int motherInt = 0;
					//					for (int up = 0; up < Math.round(eliminationCounter/1); up++)
					//					{
					//						fatherInt = random.nextInt(shrinkedPopulationSize);
					//						motherInt = random.nextInt(shrinkedPopulationSize);
					//						final Genome mother = replacementPopulation.getGenomes().get(motherInt);
					//						final Genome father = replacementPopulation.get(fatherInt);
					//						replacementPopulation.add(father.dangerTheoryMate(father, mother));
					//						shrinkedPopulationSize = shrinkedPopulationSize + 1;
					//						System.out.println("replacementpopulationsize: " + replacementPopulation.size());
					//					}
					//					train.getGenetic().setPopulation(replacementPopulation);
				}
			}

			/*========Danger theory, virus attack and hyperrecombination End============*/

			System.out
			.println("Epoch #" + tgEpoch + " Error:" + train.getError());

			//add the error of the epoch to the ErrorArrayList
			setTGError(Double.valueOf(train.getError()));
			tgEpoch++;
			//set the number of epochs needed for training
			setTGEpochs(tgEpoch);

			if (train.getError() == generationError)
			{
				generationErrorCounter++;
			}
			else
			{
				generationErrorCounter = 0;
			}
		} while ((generationErrorCounter < 25000) && (train.getError() > allowedError));


		train.finishTraining();

		network = (BasicNetwork) train.getMethod();

		// test the neural network
		System.out.println("Neural Network Results:");
		//		for(MLDataPair pair: trainingSet ) {
		//			final MLData output = network.compute(pair.getInput());
		//			System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1)
		//					+ ", actual=" + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
		//		}


		setTGNetwork(network);

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

		setTrainedNetwork((BasicNetwork) train.getMethod());

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
			}
			else
			{
				System.out.println("Operations completed - network not saved.");
				//System.exit(1);
			}
		}
	}


	/*===============================================================================================================*/



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

	public void setTGNetwork(BasicNetwork network)
	{
		tgnw = network;
	}

	public static BasicNetwork getNetwork ()
	{
		return nw;
	}

	public static BasicNetwork getTGNetwork ()
	{
		return tgnw;
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

	public static void setTGError(double error)
	{
		tgErrorArrayList.add(error);
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

	public static double[] getTGError()
	{		
		tgError = new double[tgErrorArrayList.size()];
		for (int i=0; i<tgErrorArrayList.size(); i++)
		{
			tgError[i] = (double)tgErrorArrayList.get(i);
		}
		return tgError;
	}

	public static void setEpochs(int epochs)
	{
		epoch = epochs;
	}

	public static void setTGEpochs(int epochs)
	{
		tgEpoch = epochs;
	}

	public static int getEpochs()
	{
		return epoch;
	}

	public static int getTGEpochs()
	{
		return tgEpoch;
	}

	public void setGeneticPool(int position, BasicNetwork network)
	{
		nwta[position] = network;
	}

	public static BasicNetwork getGeneticPool (int position)
	{
		return nwta[position];
	}

	public static BasicNetwork[] getGeneticPool ()
	{
		return nwta;
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