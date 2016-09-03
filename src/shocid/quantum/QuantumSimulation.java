package shocid.quantum;

import org.encog.ml.TrainingImplementationType;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.util.logging.EncogLogging;


public class QuantumSimulation extends BasicTraining {

	/**
	 * The cutoff for random data.
	 */
	public static final double CUT = 0.5;

	/**
	 * The neural network that is to be trained.
	 */
	private BasicNetwork network;

	/**
	 * This class actually performs the training.
	 */
	private final QuantumSimulationHelper quantum;

	/**
	 * Used to calculate the score.
	 */
	private final CalculateScore calculateScore;

	/**
	 * Construct a simulated annleaing trainer for a feedforward neural network.
	 * 
	 * @param network
	 *            The neural network to be trained.
	 * @param calculateScore
	 *            Used to calculate the score for a neural network.
	 * @param startTemp
	 *            The starting temperature.
	 * @param stopTemp
	 *            The ending temperature.
	 * @param cycles
	 *            The number of cycles in a training iteration.
	 */
	public QuantumSimulation(final BasicNetwork network,
			final CalculateScore calculateScore) {
		super(TrainingImplementationType.Iterative);
		this.network = network;
		this.calculateScore = calculateScore;
		this.quantum = new QuantumSimulationHelper(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean canContinue() {
		return false;
	}

	/**
	 * Get the network as an array of doubles.
	 * 
	 * @return The network as an array of doubles.
	 */
	public final double[] getArray() {
		return NetworkCODEC
		.networkToArray(QuantumSimulation.this.network);
	}

	/**
	 * @return A copy of the quantum array.
	 */
	public final double[] getArrayCopy() {
		return getArray();
	}

	/**
	 * @return The object used to calculate the score.
	 */
	public final CalculateScore getCalculateScore() {
		return this.calculateScore;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final BasicNetwork getMethod() {
		return this.network;
	}

	/**
	 * Perform one quantum iteration.
	 */
	@Override
	public final void iteration() {
		EncogLogging.log(EncogLogging.LEVEL_INFO,
		"Performing quantum iteration.");
		preIteration();
		this.quantum.iteration();
		setError(this.quantum.calculateScore());
		postIteration();
	}

	@Override
	public TrainingContinuation pause() {
		return null;
	}

	/**
	 * Convert an array of doubles to the current best network.
	 * 
	 * @param array
	 *            An array.
	 */
	public final void putArray(final double[] array) {
		NetworkCODEC.arrayToNetwork(array,
				QuantumSimulation.this.network);
	}

	/**
	 * Randomize the weights and bias values. This function does most of the
	 * work of the class. Each call to this class will randomize the data
	 * according to the current temperature. The higher the temperature the more
	 * randomness.
	 */
	public final void randomize() {
		final double[] array = NetworkCODEC
		.networkToArray(QuantumSimulation.this.network);

		for (int i = 0; i < array.length; i++) {
			double add = QuantumSimulation.CUT - Math.random();
			add /= this.quantum.getStartTemperature();
			add *= this.quantum.getTemperature();
			array[i] = array[i] + add;
		}

		NetworkCODEC.arrayToNetwork(array,
				QuantumSimulation.this.network);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resume(final TrainingContinuation state) {

	}

	//added by FNNT
	public void setNetwork(BasicNetwork nw)
	{
		/**
		 * Set the network to manipulate by external access.
		 *
		 * @return The new network.
		 */
		this.network = nw;
		setError(this.quantum.calculateScore());
		postIteration();
	}

}
