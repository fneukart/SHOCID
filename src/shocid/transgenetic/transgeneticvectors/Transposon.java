package shocid.transgenetic.transgeneticvectors;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.anneal.NeuralSimulatedAnnealing;
import org.encog.neural.networks.training.genetic.NeuralGeneticAlgorithm;

import java.util.Random;

/**
 * A simple mutation based on random numbers.
 */
public class Transposon  {
	private static BasicNetwork currentPool = null;
	private static BasicNetwork evolvingStructure = null;
	private static BasicNetwork successor = null;
	private static double[] poolWeights;
	private static double[] evolvingStructureWeights;
	static NeuralSimulatedAnnealing aln = null;
	static NeuralGeneticAlgorithm alg = null;
	String transposonType;

	public Transposon(BasicNetwork[] geneticPool, BasicNetwork evolvingStructure, NeuralSimulatedAnnealing train)
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
		setSAAlgorithm(train);
	}

	public Transposon(BasicNetwork[] geneticPool, BasicNetwork evolvingStructure, NeuralGeneticAlgorithm train) {
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
		setGAlgorithm(train);
	}

	public void transposon(String algorithmType)
	{
		Random random = new Random();
		int transposonSequenceDeterminator = random.nextInt(getCurrentPoolWeights().length-1);//at least one weight must be selected
		int transposonSequenceStart = 0;
		int transposonSequenceEnd = 0;
		int range = 0;
		int randomStart = 0;
		double cache;
		double halfNumberWeightsDouble = Math.round(getCurrentPoolWeights().length / 2);
		int halfNumberWeights = (int) halfNumberWeightsDouble;

		if (transposonSequenceDeterminator < halfNumberWeights)
		{
			transposonSequenceStart = transposonSequenceDeterminator;
			transposonSequenceEnd = halfNumberWeights;
			if ((halfNumberWeights - 1) == transposonSequenceDeterminator)
			{
				transposonSequenceDeterminator = transposonSequenceDeterminator - 1;
			}
			range = halfNumberWeights - transposonSequenceDeterminator;
		}

		else if(transposonSequenceDeterminator > halfNumberWeights)
		{
			transposonSequenceStart = halfNumberWeights;
			transposonSequenceEnd = transposonSequenceDeterminator;
			if ((halfNumberWeights + 1) == transposonSequenceDeterminator)
			{
				halfNumberWeights = halfNumberWeights - 1;
			}
			range = transposonSequenceDeterminator - halfNumberWeights;
		}

		if (range < 0)
		{
			range = range * (-1);
		}
		
		if (range == 0)
		{
			range = range + 1;
		}

		randomStart = random.nextInt(range);

		if (randomStart < 0)
		{
			randomStart = randomStart * (-1);
		}


		//jump and swap
		if (range == 2)
		{
			cache = getCurrentPoolWeights()[transposonSequenceStart];
			setCurrentPoolWeights(getCurrentPoolWeights()[transposonSequenceEnd],transposonSequenceStart);
			setCurrentPoolWeights(cache,transposonSequenceEnd);
		}

		//erase and jump
		else if (range > 2)
		{
			if (randomStart == range)
			{
				randomStart = randomStart - 1;
			}
			//the switch start must at least start at 0 of the transposon sequence
			randomStart = transposonSequenceStart + randomStart;
			cache = getCurrentPoolWeights()[randomStart];
			setCurrentPoolWeights(getCurrentPoolWeights()[randomStart+1],randomStart);
			setCurrentPoolWeights(cache,randomStart+1);
		}
		createNewMaterial(getCurrentPoolWeights(), range, algorithmType);
	}

	private void createNewMaterial(double[] newWeights, int type, String algorithmType)
	{
		BasicNetwork successor = new BasicNetwork();
		successor = (BasicNetwork) getEvolvingStructure().clone();
		double currentError = 0.0;

		if (algorithmType.equals("SA"))
		{
			currentError = getSAAlgorithm().getError();
		}
		else if (algorithmType.equals("GA"))
		{
			currentError = getGAlgorithm().getError();
		}

		successor.setNewWeights(newWeights);
		setTransposonType(type);

		//also actualizes the structure and calculates the new error
		double newError = 0.0;
		if (algorithmType.equals("SA"))
		{
			getSAAlgorithm().setNetwork(successor);
			newError = getSAAlgorithm().getError();
		}
		else if (algorithmType.equals("GA"))
		{
			getGAlgorithm().setNetwork(successor);
			newError = getGAlgorithm().getError();
		}

		if (newError > currentError)
		{
			if (algorithmType.equals("SA"))
			{
				getSAAlgorithm().setNetwork(getEvolvingStructure());
			}
			else if (algorithmType.equals("GA"))
			{
				getGAlgorithm().setNetwork(getEvolvingStructure());
			}


			System.out.println(getTransposonType()+"transposon vector tried, mutation evolutionary not reasonable.");
		}
		else
		{
			System.out.println(getTransposonType()+"transposon vector tried, mutation evolutionary reasonable.");
		}
		setSuccessor(successor);
	}


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

	public void setCurrentPoolWeights(double weight, int position)
	{
		poolWeights[position] = weight;
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

	private void setSAAlgorithm(NeuralSimulatedAnnealing algorithm)
	{
		aln = algorithm;
	}

	public NeuralSimulatedAnnealing getSAAlgorithm()
	{
		return aln;
	}

	private void setGAlgorithm(NeuralGeneticAlgorithm algorithm)
	{
		alg = algorithm;
	}

	public NeuralGeneticAlgorithm getGAlgorithm()
	{
		return alg;
	}

	private void setTransposonType(int type)
	{
		if(type ==2)
		{
			transposonType="Jump and swap ";
		}
		else
		{
			transposonType="Erase and jump ";
		}
	}

	private String getTransposonType()
	{
		return transposonType;
	}
}
