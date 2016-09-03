/*
 * Encog(tm) Examples v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/

 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package shocid.boltzmann;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.specific.BiPolarNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.thermal.BoltzmannMachine;

import shocid.ffann.training.FFANNSimulatedAnnealingJSP;
import shocid.utilities.Util;

/**
 * Simple class to recognize some patterns with a Hopfield Neural Network.
 * This is very loosely based on a an example by Karsten Kutza, 
 * written in C on 1996-01-30.
 * http://www.neural-networks-at-your-fingertips.com/hopfield.html
 * 
 * I translated it to Java and adapted it to use Encog for neural
 * network processing.  I mainly kept the patterns from the 
 * original example.
 *
 */
public class BoltzAssociatorJSP {

	static int height = 0; // depending from the number of input neurons - a quadratic or rectangular pattern space shall be determined
	static int width = 0; // depending from the number of input neurons - a quadratic or rectangular pattern space shall be determined
	private static MLDataSet trainingData = null;
	private static MLDataSet trainingDataDBN = null;
	public static int numberInputNeurons = 0;
	public static int numberOutputNeurons = 0;
	public static String[][] inputValuesArrayString = null;
	private static double[][] inputValuesArray = null;
	private static double[][] nextPatternArray = null;
	private static int twoDimPosition = -1;
	public static BasicNetwork deepBeliefNetwork;
	private static int fromLayer = 0;
	private static int depth = 0;
	private static double[][] weights;
	static boolean onlyPositive;
	static boolean saveBool;
	static String saveString;
	static double error;

	public BoltzAssociatorJSP(MLDataSet trainingSet, MLDataSet trainingSetDBN, int numberInputNeurons, int depth, int numberOutputNeurons, boolean onlyPositiveInput, boolean askForSave, String save, double allowedError)
	{
		onlyPositive = onlyPositiveInput;
		saveBool = askForSave;
		saveString = save;
		error = allowedError;

		setTrainingSet(trainingSet);
		setTrainingSetDBN(trainingSetDBN);
		setNumberInputNeurons(numberInputNeurons);
		setDepth(depth);
		setNumberOutputNeurons(numberOutputNeurons);
		createWeightsArray(getDepth(), getNumberInputNeurons()*getNumberInputNeurons());
		createDBN();
		calculateDimensions(2);
		setPatterns(getTrainingSet(), 2);
	}

	public void run()
	{
		BoltzmannMachine boltzmannLogic = new BoltzmannMachine(getWidth() * getHeight());
		boltzmannLogic.setTemperature(100);

		for (int i = 0; i < boltzmannLogic.getOutputCount(); i++)
		{
			for (int j = boltzmannLogic.getOutputCount()-1; j > -1; j--)
			{
				boltzmannLogic.addWeight( i, j, RangeRandomizer.randomize(-1, 1));
			}	
		}

		for(int j=0;j < getInputValues().length;j++)
		{
			boltzmannLogic.addPattern(convertPattern(getInputValues(),j, 2));
		}

		for (int k = 0; k < getDepth(); k++)
		{
			System.out.println(k);
			process(boltzmannLogic,getInputValues(), k);
		}
		Encog.getInstance().shutdown();
	}

	public static BiPolarNeuralData convertPattern(double[][] data, int index, int type)
	{
		int resultIndex = 0;
		BiPolarNeuralData result = new BiPolarNeuralData(getWidth() * getHeight());

		switch(type)
		{
		case 1:
			for(int row=0; row < getHeight(); row++)
			{
				for(int col=0; col < getWidth(); col++)
				{
					double ch = data[index][row];
					result.setData(resultIndex++, ch);
				}
			}
		case 2:
			for(int col=0; col < getWidth(); col++)
			{
				double ch = data[index][col];
				result.setData(resultIndex++, ch);
			}
		}
		return result;
	}

	public static void display(BiPolarNeuralData pattern1,BiPolarNeuralData pattern2)
	{
		int index1 = 0;
		int index2 = 0;

		for(int row = 0; row < getHeight(); row++)
		{
			StringBuilder line = new StringBuilder();

			for(int col = 0;col < getWidth(); col++)
			{
				if(pattern1.getBoolean(index1++))
					line.append('T');
				else
					line.append('F');
			}

			line.append("   ->   ");

			for(int col = 0; col < getWidth(); col++)
			{
				if(pattern2.getBoolean(index2++))
					line.append('T');
				else
					line.append('F');
			}
			System.out.println(line.toString());
		}
	}

	public static void process(BoltzmannMachine boltzmannLogic, double[][] pattern, int depth)
	{
		BiPolarNeuralData pattern1 = null;
		BiPolarNeuralData pattern2 = null;

		if (depth == 0)
		{
			for(int i = 0; i < pattern.length; i++)
			{
				pattern1 = convertPattern(pattern,i, 2);
				boltzmannLogic.setCurrentState(pattern1);
				do
				{
					boltzmannLogic.establishEquilibrium();
					boltzmannLogic.decreaseTemperature(0.99);
				}
				while (boltzmannLogic.getTemperature() > 5);

				pattern2 = (BiPolarNeuralData)boltzmannLogic.getCurrentState();
				setNextPattern(i, boltzmannLogic);
				display(pattern1, pattern2);
				System.out.println("----------------------");
			}
		}
		else
		{
			for(int i = 0; i < getNextPattern().length; i++)
			{
				pattern1 = convertPattern(getNextPattern(),i, 2);
				boltzmannLogic.setCurrentState(pattern1);
				do
				{
					boltzmannLogic.establishEquilibrium();
					boltzmannLogic.decreaseTemperature(0.99);
				}
				while (boltzmannLogic.getTemperature() > 5);

				pattern2 = (BiPolarNeuralData)boltzmannLogic.getCurrentState();
				setNextPattern(i, boltzmannLogic);
				display(pattern1, pattern2);
				System.out.println("----------------------");
			}
		}
		
		if (getDepth()-1 != depth)
		{
			evolveDBN(boltzmannLogic);//increase the FFANN by one layer	
		}
		else
		{
			finalizeDBN(boltzmannLogic); //finalizes the FFANN

			//start DBN training
			FFANNDBNSA ffannsa = new FFANNDBNSA();
			//			System.out.println("Layers: "+getDBN().getLayerCount()+"\n");
			//			System.out.println("Weights before normalization: "+getDBN().dumpWeights());
			//			BasicNetwork test = getDBN();
			//			test.reset();
			//			System.out.println("Weights after normalization: "+test.dumpWeights());
			//			System.out.println("Input layer: "+getDBN().getInputCount());
			//			for (int l = 0; l < getDBN().getLayerCount(); l++)
			//			{
			//				System.out.println("Layer "+l+" features "+getDBN().getLayerTotalNeuronCount(l));
			//			}
			//			System.out.println("Output layer: "+getDBN().getOutputCount());
			ffannsa.run(getDBN(), getNumberInputNeurons(), getNumberOutputNeurons(), getTrainingSetDBN(), onlyPositive, saveBool, saveString, error);
		}
	}

	public static void calculateDimensions(int type)
	{
		//if type == 1, then a quadratic or rectangular input space is created, else a one-dimensional one. 

		switch(type)
		{
		case 1:
			if (getNumberInputNeurons()%5==0 && getNumberInputNeurons()/5!=1)
			{
				setWidth(getNumberInputNeurons()/5);
				setHeight(5);
			}
			else if (getNumberInputNeurons()%4==0 && getNumberInputNeurons()/4!=1)
			{
				setWidth(getNumberInputNeurons()/4);
				setHeight(4);
			}
			else if (getNumberInputNeurons()%3==0 && getNumberInputNeurons()/3!=1)
			{
				setWidth(getNumberInputNeurons()/3);
				setHeight(3);
			}
			else if (getNumberInputNeurons()%2==0 && getNumberInputNeurons()/2!=1)
			{
				setWidth(getNumberInputNeurons()/2);
				setHeight(2);
			}
			else
			{
				setWidth(getNumberInputNeurons());
				setHeight(1);
			}
		case 2:
			setWidth(getNumberInputNeurons());
			setHeight(1);
			break;
		}
	}

	public static void setPatterns(MLDataSet trainingSet, int type)
	{
		//type 1 creates the quadratic or rectangular patterns, type 2 the one-dimensional ones.
		int position;
		switch(type)
		{
		case 1:
			defineInputValuesArray(trainingSet.size(), getHeight());
			position = 0;

			for(MLDataPair pair: trainingSet)
			{
				setTwoDimPosition();
				position = 0;
				for (int j = 0; j < getHeight(); j++)
				{
					for (int k = 0; k < getWidth(); k++)
					{
						setInputValues(getTwoDimPosition(), j, pair.getInput().getData(position));
						position+=1;
					}
				}
			}

		case 2:
			defineInputValuesArray(trainingSet.size(), getWidth()); //in case it is one-dimensional, the height has been set to 1 before
			defineNextPatternArray(trainingSet.size(), getWidth());
			position = -1;

			for(MLDataPair pair: trainingSet)
			{
				position+=1;
				for (int k = 0; k < getWidth(); k++)
				{
					setInputValues(position, k, pair.getInput().getData(k));
				}
			}
			break;
		}
	}

	private static void defineInputValuesArray(int numberDataSets, int height)
	{
		inputValuesArrayString = new String[numberDataSets][height];
		inputValuesArray = new double[numberDataSets][height];
	}

	private static void defineNextPatternArray(int numberDataSets, int height)
	{
		nextPatternArray = new double[numberDataSets][height];
	}

	private static void setNextPattern(int index, BoltzmannMachine boltzmannLogic)
	{
		for (int i = 0; i < boltzmannLogic.getCurrentState().size(); i++)
		{
			nextPatternArray[index][i]= boltzmannLogic.getCurrentState().getData(i);
		}	
	}

	private static double[][] getNextPattern()
	{
		return nextPatternArray;
	}

	public static void setTrainingSet(MLDataSet trainingSet)
	{
		trainingData = trainingSet;
	}

	public static MLDataSet getTrainingSet()
	{
		return trainingData;
	}

	public static void setTrainingSetDBN(MLDataSet trainingSet)
	{
		trainingDataDBN = trainingSet;
	}

	public static MLDataSet getTrainingSetDBN()
	{
		return trainingDataDBN;
	}

	public static void setNumberInputNeurons(int number)
	{
		numberInputNeurons = number;	
	}

	public static int getNumberInputNeurons()
	{
		return numberInputNeurons;
	}

	public static void setNumberOutputNeurons(int number)
	{
		numberOutputNeurons = number;	
	}

	public static int getNumberOutputNeurons()
	{
		return numberOutputNeurons;
	}

	public static void setWidth(int number)
	{
		width = number;	
	}

	public static int getWidth()
	{
		return width;
	}

	public static void setHeight(int number)
	{
		height = number;	
	}

	public static int getHeight()
	{
		return height;
	}

	public static void setInputValues(int row, int col, double value)
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

	private static void setTwoDimPosition()
	{
		twoDimPosition+=1;
	}

	private static int getTwoDimPosition()
	{
		return twoDimPosition;
	}

	private static void createDBN()
	{
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(new ActivationTANH(), true, getNumberInputNeurons()));
		setDBN(network);
	}

	private static void evolveDBN(BoltzmannMachine boltzmannLogic)
	{
		BasicNetwork network = new BasicNetwork();
		network = getDBN();
		network.addLayer(new BasicLayer(new ActivationTANH(), true, getNumberInputNeurons()));
		int currentPosition = 0;
		//set the weight for the neural network
		for (int i = 0; i < boltzmannLogic.getOutputCount(); i++)
		{
			for (int j = boltzmannLogic.getOutputCount()-1; j > -1; j--)
			{
				//network.addWeight(getFromLayer(), i, j, boltzmannLogic.getWeight(i, j));
				setWeightsArray(getFromLayer(), currentPosition, boltzmannLogic.getWeight(i, j));
				currentPosition+=1;
			}
		}
		setFromLayer(); //increase the from layer by 1
		setDBN(network);
	}

	private static void finalizeDBN(BoltzmannMachine boltzmannLogic)
	{
		BasicNetwork network = new BasicNetwork();
		double correctedWeight = 0;
		double normalizationFactor = 0;
		double summedWeights = 0;
		int weightNormalizationType = 3;
		double minWeight = 0;
		double maxWeight = 0;
		double minNorm = -1;
		double maxNorm = 1;
		network = getDBN();
		network.addLayer(new BasicLayer(new ActivationTANH(), true, getNumberInputNeurons()));
		network.addLayer(new BasicLayer(new ActivationTANH(), true, getNumberOutputNeurons()));
		network.getStructure().finalizeStructure();
		setDBN(network);
		int currentPosition;
		System.out.println("dbn properties\n");
		System.out.println("input count: "+network.getInputCount());
		System.out.println("output count: "+network.getOutputCount());
		System.out.println("layer count: "+network.getLayerCount());

		//Sign-depending range mapped normalization
		if (weightNormalizationType == 1)
		{
			for (int l = 0; l < getDepth(); l++)
			{
				currentPosition = 0;
				//add the weights from the first layer to the prenultimate layer
				for (int i = 0; i < boltzmannLogic.getOutputCount(); i++)
				{
					for (int j = boltzmannLogic.getOutputCount()-1; j > -1; j--)
					{
						//System.out.println("layer "+l+", from-neuron "+i+", to-neuron "+j+ ", weight position "+currentPosition);
						if (getWeightsArray(l, currentPosition) > 1)
						{
							correctedWeight = RangeRandomizer.randomize(0, 1);
						}
						else if (getWeightsArray(l, currentPosition) < -1)
						{
							correctedWeight = RangeRandomizer.randomize(-1, 0);
						}
						else if (getWeightsArray(l, currentPosition) == 0)
						{
							correctedWeight = RangeRandomizer.randomize(-1, 1);
						}

						network.addWeight(l, i, j, correctedWeight);
						currentPosition+=1;
					}
				}	
			}
		}
		//Mapping normalization
		else if (weightNormalizationType == 2)
		{
			for (int l = 0; l < getDepth(); l++)
			{
				currentPosition = 0;
				for (int i = 0; i < boltzmannLogic.getOutputCount(); i++)
				{
					for (int j = boltzmannLogic.getOutputCount()-1; j > -1; j--)
					{
						minWeight = Util.getMinArrayValue(getWeightsArray(l));
						maxWeight = Util.getMaxArrayValue(getWeightsArray(l));
						correctedWeight = ((getWeightsArray(l, currentPosition)-minWeight)/(getWeightsArray(l, currentPosition)-maxWeight))*(maxNorm-minNorm);
						network.addWeight(l, i, j, correctedWeight);
						currentPosition+=1;
					}
				}	
			}
		}
		//Multiplicative normalization - this seems to be the real thing.
		else if (weightNormalizationType == 3)
		{
			for (int l = 0; l < getDepth(); l++)
			{
				currentPosition = 0;
				
				for (int w = 0; w < getWeightsArray(l).length; w ++)
				{
					summedWeights = summedWeights + Math.pow(getWeightsArray(l)[w],2);
				}
				
				normalizationFactor = 1/(Math.sqrt(summedWeights));
				
				for (int i = 0; i < boltzmannLogic.getOutputCount(); i++)
				{
					for (int j = boltzmannLogic.getOutputCount()-1; j > -1; j--)
					{
						correctedWeight = getWeightsArray(l, currentPosition)*normalizationFactor;
						network.addWeight(l, i, j, correctedWeight);
						currentPosition+=1;
					}
				}	
			}
		}

		currentPosition = 0;
		//add the weights from the prenultimate to the output layer. the first weights of the boltzmann machine are taken, this is subject to changes 
		for (int i = 0; i < boltzmannLogic.getOutputCount()-1; i++)
		{
			for (int j = getNumberOutputNeurons()-1; j > -1; j--)
			{
				if (boltzmannLogic.getWeight(i, j) > 1)
				{
					correctedWeight = RangeRandomizer.randomize(0, 1);
				}
				else if (boltzmannLogic.getWeight(i, j) < -1)
				{
					correctedWeight = RangeRandomizer.randomize(-1, 0);
				}
				else if (boltzmannLogic.getWeight(i, j) == 0)
				{
					correctedWeight = RangeRandomizer.randomize(-1, 1);
				}
				network.addWeight(getDepth(), i, j, correctedWeight);
			}	
		}	

		System.out.println("Deep Boltzmann Machine successfully created and transformed to Deep Belief Network.");
		setDBN(network);
	}

	private static void createWeightsArray(int depth, int numberInputNeurons)
	{
		weights = new double[depth][numberInputNeurons];
	}

	private static void setWeightsArray(int fromLayer, int weightPos, double weight)
	{
		weights[fromLayer][weightPos] = weight;
	}

	private static double getWeightsArray(int fromLayer, int weightPos)
	{
		return weights[fromLayer][weightPos];
	}

	private static double[] getWeightsArray(int fromLayer)
	{
		return weights[fromLayer];
	}

	private static void setDBN(BasicNetwork network)
	{
		deepBeliefNetwork = network;
	}

	public static BasicNetwork getDBN()
	{
		return deepBeliefNetwork;
	}

	private static void setFromLayer()
	{
		fromLayer+=1;
	}

	private static int getFromLayer()
	{
		return fromLayer;
	}

	private static void setDepth(int d)
	{
		depth = d;
	}

	private static int getDepth()
	{
		return depth;
	}
}