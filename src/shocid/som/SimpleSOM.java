/*
 * Encog(tm) Examples v2.4
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package shocid.som;

import org.encog.engine.network.activation.ActivationLinear;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.som.SOM;
import org.encog.neural.som.training.basic.neighborhood.NeighborhoodSingle;
import org.encog.neural.som.training.basic.BasicTrainSOM;
import org.encog.util.logging.EncogLogging;

/**
 * Implement a simple SOM using Encog.  It learns to recognize two patterns.
 * @author jeff
 *
 */
public class SimpleSOM {
	
	public static double SOM_INPUT[][] = { 
		{ -1.0, -1.0, 1.0, 1.0 }, 
		{ 1.0, 1.0, -1.0, -1.0 } };
	
	public static void main(String args[])
	{
		
		// create the training set
		MLDataSet training = new BasicNeuralDataSet(SOM_INPUT,null);
		
		// Create the neural network.
		SOM network = new SOM(4,2);
		network.reset();
		
		BasicTrainSOM train = new BasicTrainSOM(
				network,
				0.7,
				training,
				new NeighborhoodSingle());
				
		int iteration = 0;
		
		for(iteration = 0;iteration<=10;iteration++)
		{
			train.iteration();
			System.out.println("Iteration: " + iteration + ", Error:" + train.getError());
		}
		
		BasicNeuralData data1 = new BasicNeuralData(SOM_INPUT[0]);
		BasicNeuralData data2 = new BasicNeuralData(SOM_INPUT[1]);
		System.out.println("Pattern 1 winner: " + network.winner(data1));
		System.out.println("Pattern 2 winner: " + network.winner(data2));
	}
}
