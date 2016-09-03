package shocid.transgenetic.transgeneticvectors;

import org.encog.ml.genetic.genome.Genome;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.anneal.NeuralSimulatedAnnealing;
import org.encog.neural.networks.training.genetic.NeuralGeneticAlgorithm;
import java.util.Random;

/**
 * A simple mutation based on random numbers.
 */
public class Plasmid  {
	private static BasicNetwork currentPool = null;
	private static BasicNetwork evolvingStructure = null;
	private static BasicNetwork successor = null;
	private static double[] poolWeights;
	private static double[] evolvingStructureWeights;
	static NeuralSimulatedAnnealing aln = null;
	static NeuralGeneticAlgorithm alg = null;

	public Plasmid(BasicNetwork[] geneticPool, BasicNetwork evolvingStructure, NeuralSimulatedAnnealing train)
	{
		//determine which of the trained ANNs serves as genetic pool
		Random random = new Random();
		int currentPoolPosition = random.nextInt(geneticPool.length);

		//set the weights of the selected genetic pool ANN and the pool itself
		setCurrentPool(geneticPool[currentPoolPosition]);
		setCurrentPoolWeights(getCurrentPool().dumpWeightsArray());

		//set the weights of ANN to change by the plasmid and the ANN itself		
		setEvolvingStructure(evolvingStructure);
		setEvolvingStructureWeights(evolvingStructure.dumpWeightsArray());

		//set the NeuralGeneticAlgorithm
		setAlgorithm(train);
	}

	public Plasmid(BasicNetwork[] geneticPool, BasicNetwork evolvingStructure, NeuralGeneticAlgorithm train) {
		//determine which of the trained ANNs serves as genetic pool
		Random random = new Random();
		int currentPoolPosition = random.nextInt(geneticPool.length);

		//set the weights of the selected genetic pool ANN and the pool itself
		setCurrentPool(geneticPool[currentPoolPosition]);
		setCurrentPoolWeights(getCurrentPool().dumpWeightsArray());

		//set the weights of ANN to change by the plasmid and the ANN itself		
		setEvolvingStructure(evolvingStructure);
		setEvolvingStructureWeights(evolvingStructure.dumpWeightsArray());

		//set the NeuralGeneticAlgorithm
		setAlgorithm(train);
	}

	public void weightPlasmid(String algorithmType)
	{
		Random random = new Random();
		int plasmidSequenceStart = random.nextInt(getCurrentPoolWeights().length-1);//at least one weight must be selected
		int counter = plasmidSequenceStart;

		double[] plasmidSequence = new double[getCurrentPoolWeights().length-plasmidSequenceStart];
		double[] newWeights = new double[getCurrentPoolWeights().length];

		for (int s = 0; s < plasmidSequence.length; s++)
		{
			plasmidSequence[s] = getCurrentPoolWeights()[counter];
			counter = counter + 1;
		}

		for (int i = 0; i < plasmidSequenceStart; i++)
		{
			newWeights[i] = getEvolvingStructureWeights()[i];
			//newWeights[i] = getCurrentPoolWeights()[i];
		}

		counter = 0;

		for (int j = plasmidSequenceStart; j < getCurrentPoolWeights().length; j++)
		{
			newWeights[j] = plasmidSequence[counter];
			counter = counter + 1;
		}

		createNewMaterial(newWeights, algorithmType);

	}
	
	private void createNewMaterial(double[] newWeights, String algorithmType)
	{
		BasicNetwork successor = new BasicNetwork();
		successor = (BasicNetwork) getEvolvingStructure().clone();
		successor.setNewWeights(newWeights);
		double currentError = 0.0;
		double newError = 0.0;
		if (algorithmType.equals("SA"))
		{
			currentError = getSAAlgorithm().getError();
			getSAAlgorithm().setNetwork(successor);
			newError = getSAAlgorithm().getError();
		}
		else if (algorithmType.equals("GA"))
		{
			currentError = getGAlgorithm().getError();
			getGAlgorithm().setNetwork(successor);
			newError = getGAlgorithm().getError();
		}

		if (newError >= currentError)
		{
			if (algorithmType.equals("SA"))
			{
				getSAAlgorithm().setNetwork(getEvolvingStructure());
			}
			else if (algorithmType.equals("GA"))
			{
				getGAlgorithm().setNetwork(getEvolvingStructure());
			}

			System.out.println("Plasmid vector tried, mutation evolutionary not reasonable.");
		}
		else
		{
			System.out.println("Plasmid vector tried, mutation evolutionary reasonable.");
		}
		setSuccessor(successor);
	}

	/*=======================FOR THE GA ONLY========================*/
	
	public Genome geneticWeightPlasmid(Genome currentGenome)
	{
		BasicNetwork currentNetwork = (BasicNetwork) currentGenome.getOrganism();
		//BasicNetwork newOrganism;
		Genome newOrganism = currentGenome;
		Random random = new Random();
		int plasmidSequenceStart = random.nextInt(getCurrentPoolWeights().length-1);//at least one weight must be selected
		int counter = plasmidSequenceStart;

		double[] plasmidSequence = new double[getCurrentPoolWeights().length-plasmidSequenceStart];
		double[] newWeights = new double[getCurrentPoolWeights().length];

		for (int s = 0; s < plasmidSequence.length; s++)
		{
			plasmidSequence[s] = getCurrentPoolWeights()[counter];
			counter = counter + 1;
		}

		for (int i = 0; i < plasmidSequenceStart; i++)
		{
			newWeights[i] = currentNetwork.dumpWeightsArray()[i];
			//newWeights[i] = getCurrentPoolWeights()[i];
		}

		counter = 0;

		for (int j = plasmidSequenceStart; j < getCurrentPoolWeights().length; j++)
		{
			newWeights[j] = plasmidSequence[counter];
			counter = counter + 1;
		}
		System.out.println("new org before: " + newOrganism.getScore());
		newOrganism.setOrganism(createNewOrganism(newWeights));
		System.out.println("new org after: " + newOrganism.getScore());
		return newOrganism;
	}

	private BasicNetwork createNewOrganism(double[] newWeights)
	{
		BasicNetwork successor = new BasicNetwork();
		successor = (BasicNetwork) getEvolvingStructure().clone();
		successor.setNewWeights(newWeights);
		double currentError = 0.0;
		double newError = 0.0;


		currentError = getGAlgorithm().getError();
		getGAlgorithm().setNetwork(successor);
		newError = getGAlgorithm().getError();

		getGAlgorithm().setNetwork(getEvolvingStructure());

		setSuccessor(successor);
		return successor;
	}
	/*=======================FOR THE GA ONLY========================*/

	public void setCurrentPool(BasicNetwork network)
	{
		currentPool = new BasicNetwork();
		currentPool = network;
	}

	public static BasicNetwork getCurrentPool()
	{
		return currentPool;
	}

	public void setEvolvingStructure(BasicNetwork network)
	{
		evolvingStructure = network;
	}

	public static BasicNetwork getEvolvingStructure()
	{
		return evolvingStructure;
	}

	public void setSuccessor(BasicNetwork network)
	{
		successor = network;
	}

	public static BasicNetwork getSuccessor()
	{
		return successor;
	}

	public void setCurrentPoolWeights(double[] weights)
	{
		poolWeights = new double[weights.length];
		poolWeights = weights;
	}

	public static double[] getCurrentPoolWeights()
	{
		return poolWeights;
	}

	public void setEvolvingStructureWeights(double[] weights)
	{
		evolvingStructureWeights = new double[weights.length];
		evolvingStructureWeights = weights;
	}

	public static double[] getEvolvingStructureWeights()
	{
		return evolvingStructureWeights;
	}

	public void setAlgorithm(NeuralSimulatedAnnealing algorithm)
	{
		aln = algorithm;
	}

	private void setAlgorithm(NeuralGeneticAlgorithm algorithm) {
		alg = algorithm;

	}

	public NeuralSimulatedAnnealing getSAAlgorithm()
	{
		return aln;
	}

	public NeuralGeneticAlgorithm getGAlgorithm()
	{
		return alg;
	}
}
