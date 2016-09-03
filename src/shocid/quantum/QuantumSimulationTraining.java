package shocid.quantum;


public abstract class QuantumSimulationTraining<UNIT_TYPE> {

	/**
	 * The starting temperature.
	 */
	private double startTemperature;
	/**
	 * The ending temperature.
	 */
	private double stopTemperature;
	/**
	 * The number of cycles that will be used.
	 */
	private int cycles;
	/**
	 * The current score.
	 */
	private double score;

	/**
	 * The current temperature.
	 */
	private double temperature;

	/**
	 * Should the score be minimized.
	 */
	private boolean shouldMinimize = true;

	/**
	 * Subclasses should provide a method that evaluates the score for the
	 * current solution. Those solutions with a lower score are better.
	 * 
	 * @return Return the score.
	 */
	public abstract double calculateScore();

	/**
	 * Subclasses must provide access to an array that makes up the solution.
	 * 
	 * @return An array that makes up the solution.
	 */
	public abstract UNIT_TYPE[] getArray();

	/**
	 * Get a copy of the array.
	 * 
	 * @return A copy of the array.
	 */
	public abstract UNIT_TYPE[] getArrayCopy();

	/**
	 * @return the cycles
	 */
	public final int getCycles() {
		return this.cycles;
	}

	/**
	 * @return the globalScore
	 */
	public final double getScore() {
		return this.score;
	}

	/**
	 * @return the startTemperature
	 */
	public final double getStartTemperature() {
		return this.startTemperature;
	}

	/**
	 * @return the stopTemperature
	 */
	public final double getStopTemperature() {
		return this.stopTemperature;
	}

	/**
	 * @return the temperature
	 */
	public final double getTemperature() {
		return this.temperature;
	}

	/**
	 * @return True if the score should be minimized.
	 */
	public final boolean isShouldMinimize() {
		return this.shouldMinimize;
	}

	/**
	 * Called to perform one cycle of the annealing process.
	 */
	public final void iteration() {
		UNIT_TYPE[] bestArray;
//
		setScore(calculateScore());
		bestArray = this.getArrayCopy();
//
//		this.temperature = this.getStartTemperature();
//
//		for (int i = 0; i < this.cycles; i++) {
			double curScore;
//			randomize();
			curScore = calculateScore();
//
//			if (this.shouldMinimize) {
				if (curScore < getScore()) {
					bestArray = this.getArrayCopy();
					setScore(curScore);
				}
//			} else {
				if (curScore > getScore()) {
					bestArray = this.getArrayCopy();
					setScore(curScore);
				}
//			}
//
			this.putArray(bestArray);
//			final double ratio = Math.exp(Math.log(getStopTemperature()
//					/ getStartTemperature())
//					/ (getCycles() - 1));
//			this.temperature *= ratio;
//		}
	}

	/**
	 * Store the array.
	 * 
	 * @param array
	 *            The array to be stored.
	 */
	public abstract void putArray(UNIT_TYPE[] array);

	/**
	 * Randomize the weight matrix.
	 */
	public abstract void randomize();

	/**
	 * @param theCycles
	 *            the cycles to set
	 */
	public final void setCycles(final int theCycles) {
		this.cycles = theCycles;
	}

	/**
	 * Set the score.
	 * 
	 * @param theScore
	 *            The score to set.
	 */
	public final void setScore(final double theScore) {
		this.score = theScore;
	}

	/**
	 * Should the score be minimized.
	 * 
	 * @param theShouldMinimize
	 *            True if the score should be minimized.
	 */
	public final void setShouldMinimize(final boolean theShouldMinimize) {
		this.shouldMinimize = theShouldMinimize;
	}

	/**
	 * @param theStartTemperature
	 *            the startTemperature to set
	 */
	public final void setStartTemperature(final double theStartTemperature) {
		this.startTemperature = theStartTemperature;
	}

	/**
	 * @param theStopTemperature
	 *            the stopTemperature to set
	 */
	public final void setStopTemperature(final double theStopTemperature) {
		this.stopTemperature = theStopTemperature;
	}

	/**
	 * @param theTemperature
	 *            the temperature to set
	 */
	public final void setTemperature(final double theTemperature) {
		this.temperature = theTemperature;
	}

}
