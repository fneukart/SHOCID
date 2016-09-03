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

package shocid.prediction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.StringTokenizer;

import org.encog.NullStatusReportable;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.temporal.TemporalDataDescription;
import org.encog.ml.data.temporal.TemporalMLDataSet;
import org.encog.ml.data.temporal.TemporalPoint;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.normalize.DataNormalization;
import org.encog.util.normalize.input.InputField;
import org.encog.util.normalize.input.InputFieldArray1D;
import org.encog.util.normalize.output.OutputFieldRangeMapped;
import org.encog.util.normalize.target.NormalizationStorageArray1D;
import org.encog.util.obj.SerializeObject;

public class ANNPrediction {

	//holds the values out of the data file
	public static double[] values = null;


	//public final static int STARTING_YEAR = 1700;

	//defines the size of the prediction window - done in the main method
	public static int windowSize;

	//defines, where the training should start - at the end of the first window.
	//public static int trainingStart = windowSize;
	public static int trainingStart; //will be set in the main method

	//defines at which dataset in the present data the training should end
	//public static int trainingEnd = 259;
	public static int trainingEnd;

	//defines at which data set in the present data the prediction (evaluation) on given data should start
	//public static int evaluationStart = 260;
	public static int evaluationStart;

	//defines when the prediction (evaluation) should end - here the variable is just initialized, it is set with the setEvaluationEnd()-method.
	public static int evaluationEnd = 0;
	static String fileName;

	static String inputFile;

	//specifies the overall number of tokens in the file
	static int tokenNumberFile = 0;
	
	static String savePath = "prediction\\predictiveAnalysis.net";


	public ANNPrediction()
	{

	}

	public static void startPrediction()
	{

		String brinf = null;
		String brwinf = null;

		//EncogLogging.stopConsoleLogging();

		//  prompt to specify the input file
		System.out.print("Enter the path and name of input file: ");

		// open up standard input
		BufferedReader brin = new BufferedReader(new InputStreamReader(System.in));

		try
		{
			brinf = brin.readLine();
		}
		catch (IOException ioe)
		{
			System.out.println("IO error trying to read the inputfile path.");
			System.exit(1);
		}

		setInputFile(brinf); //prediction\\prediction.csv

		//  prompt to specify the past window size
		System.out.print("Enter the past window size: ");

		// open up standard input
		BufferedReader brwin = new BufferedReader(new InputStreamReader(System.in));

		try
		{
			brwinf = brwin.readLine();
		}
		catch (IOException ioe)
		{
			System.out.println("IO error trying to read the past window size.");
			System.exit(1);
		}

		setPastWindowSize(Integer.valueOf(brwinf));
		setTrainingStart(getPastWindowSize());

		fileToArray(getInputFile());
		ANNPrediction prediction = new ANNPrediction();
		prediction.run();
	}

	public static void fileToArray(String filePath)
	{
		File predictionFile = new File(filePath);

		BufferedReader br = null;
		FileReader fr = null;

		try {
			fr = new FileReader(predictionFile);
			br = new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//just to count all the tokens in the file
		try {
			while((fileName = br.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(fileName,",");

				//while st.hasMoreTokens() does not work, if the last token is at the end of the line
				int count = st.countTokens();
				for (int i = 0; i < count; i++)
				{
					setTokenNumberFile();	
				}
			}

			//tokenNumberFile must be set with +1, as counting starts at 0
			values = new double[getTokenNumberFile()+1];
			setEvaluationEnd(values.length-1);

			//set the data set where the training should stop and the evaluation should start
			setTrainingEnd(trainUntil(values.length-1));
			setEvaluationStart(getTrainingEnd()+1);

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//create the readers again as they are empty after the first try/catch
		try {
			fr = new FileReader(predictionFile);
			br = new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//reset the token number
		tokenNumberFile = 0;

		try {
			while((fileName = br.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(fileName,",");

				//while st.hasMoreTokens() does not work, if the last token is at the end of the line
				int count = st.countTokens();
				for (int i = 0; i < count; i++)
				{
					setPredictionValues(getTokenNumberFile(), Double.valueOf(st.nextToken()).doubleValue());
					setTokenNumberFile();
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static int trainUntil(int numberValues)
	{
		//the method automatically defines the number of training sets with 80% of the overall data amount - 20% is then used for evaluation purposes
		float untilFloat = (numberValues/100)*80;
		int untilInt = Math.round(untilFloat);
		return untilInt;
	}

	/**
	 * This really should be lowered, I am setting it to a level here that will
	 * train in under a minute.
	 */
	public final static double MAX_ERROR = 0.01;

	private double[] normalizedValues;
	private double[] closedLoopValues;

	public void normalizeValues(double lo,double hi)
	{			
		InputField in;

		// create arrays to hold the normalized values
		normalizedValues = new double[values.length];
		closedLoopValues = new double[values.length];

		// normalize the values
		DataNormalization norm = new DataNormalization();
		norm.setReport(new NullStatusReportable());
		norm.addInputField(in = new InputFieldArray1D(true,values));
		norm.addOutputField(new OutputFieldRangeMapped(in, lo, hi));
		norm.setTarget(new NormalizationStorageArray1D(normalizedValues));
		norm.process();
		System.arraycopy(normalizedValues, 0, closedLoopValues, 0, normalizedValues.length);

	}

	public MLDataSet generateTraining()
	{
		TemporalMLDataSet result = new TemporalMLDataSet(windowSize,1);

		TemporalDataDescription desc = new TemporalDataDescription(
				TemporalDataDescription.Type.RAW,true,true);
		result.addDescription(desc);

		for(int year = trainingStart;year<trainingEnd;year++)
		{
			TemporalPoint point = new TemporalPoint(1);
			point.setSequence(year);
			point.setData(0, this.normalizedValues[year]);
			result.getPoints().add(point);
		}

		result.generate();

		return result;
	}

	public BasicNetwork createNetwork()
	{
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(windowSize));
		network.addLayer(new BasicLayer(10));
		network.addLayer(new BasicLayer(1));
		network.getStructure().finalizeStructure();
		network.reset();
		return network;
	}

	public void train(BasicNetwork network,MLDataSet training)
	{
		final Train train = new ResilientPropagation(network, training);

		int epoch = 1;

		do {
			train.iteration();
			System.out
			.println("Epoch #" + epoch + " Error:" + train.getError());
			epoch++;
		} while(train.getError() > MAX_ERROR);
	}

	public void predict(BasicNetwork network)
	{
		String brsavpyn = null;
		NumberFormat f = NumberFormat.getNumberInstance();
		f.setMaximumFractionDigits(4);
		f.setMinimumFractionDigits(4);

		System.out.println("\nEvaluation Start: \n");
		System.out.println("Step\tSet\tActual\tPredict\tClosed Loop Predict");

		//evaluation step number
		int step = 1;

		for(int evaluationBorder=evaluationStart;evaluationBorder<evaluationEnd;evaluationBorder++)
		{	
			// calculate based on actual data
			BasicNeuralData input = new BasicNeuralData(windowSize);
			for(int i=0;i<input.size();i++)
			{
				input.setData(i,this.normalizedValues[(evaluationBorder-windowSize)+i]);
			}
			MLData output = network.compute(input);
			double prediction = output.getData(0);
			this.closedLoopValues[evaluationBorder] = prediction;

			// calculate "closed loop", based on predicted data
			for(int i=0;i<input.size();i++)
			{
				input.setData(i,this.closedLoopValues[(evaluationBorder-windowSize)+i]);
			}
			output = network.compute(input);
			double closedLoopPrediction = output.getData(0);

			// print out the evaluation step, the evaluation data set and the predictions
			System.out.println(step
					+"\t"+(evaluationBorder)
					+"\t"+f.format(this.normalizedValues[evaluationBorder])
					+"\t"+f.format(prediction)
					+"\t"+f.format(closedLoopPrediction)
			);
			step++;
		}

		//  prompt for saving the network
		System.out.print("Save the neural network (y/n)? ");

		//  open up standard input
		BufferedReader brsav = new BufferedReader(new InputStreamReader(System.in));

		//  read the input neurons from the command-line; need to use try/catch with the readLine() method
		try {
			brsavpyn = brsav.readLine();
		}
		catch (IOException ioe) {
			System.out.println("IO error trying to read the answer.");
			System.exit(1);
		}
		if (brsavpyn.trim().equals("y"))
		{
			try {
				//SerializeObject.save("\\inputFilePath\\predictiveAnalysis.net", train.getNetwork());
				File networkSave = new File(savePath);
				SerializeObject.save(networkSave, network);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Network saved under " + savePath);
			System.out.println("Operations completed.");
		}
	}

	public void run()
	{
		normalizeValues(0.1,0.9);
		BasicNetwork network = createNetwork();
		MLDataSet training = generateTraining();
		train(network,training);
		predict(network);

	}

	public static void setPredictionValues(int col, double value)
	{
		values[col] = value;
	}

	public double getPredictionValues(int col)
	{
		return values[col];
	}

	public static void setEvaluationStart(int start)
	{
		evaluationStart = start;
	}

	public int getEvaluationStart()
	{
		return evaluationStart;
	}

	public static void setEvaluationEnd(int end)
	{
		evaluationEnd = end;
	}

	public int getEvaluationEnd()
	{
		return evaluationEnd;
	}

	public static void setTokenNumberFile()
	{
		tokenNumberFile = tokenNumberFile + 1;
	}

	public static int getTokenNumberFile()
	{
		return tokenNumberFile;
	}

	public static void setInputFile(String filePath)
	{
		inputFile = filePath;
	}

	public static String getInputFile()
	{
		return inputFile;
	}

	public static void setPastWindowSize(int size)
	{
		windowSize = size;
	}

	public static int getPastWindowSize()
	{
		return windowSize;
	}

	public static void setTrainingStart(int start)
	{
		trainingStart = start;
	}

	public static int getTrainingStart()
	{
		return trainingStart;
	}

	public static void setTrainingEnd(int end)
	{
		trainingEnd = end;
	}

	public static int getTrainingEnd()
	{
		return trainingEnd;
	}
}
//prediction\\prediction.csv
