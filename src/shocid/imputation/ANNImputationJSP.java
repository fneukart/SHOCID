package shocid.imputation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.TrainingSetUtil;

public class ANNImputationJSP extends Thread {

	public static double imputationValue;
	public static String incompleteFilePath;
	public static String wholeFilePath;
	public static ArrayList<String> imputedDatasets=new ArrayList<String>();

	public static void imputation(String inPath, String columnsInfluence, String imputationColumn, double allowedError, String outFile)
	{
		ANNPreparationJSP preparation = new ANNPreparationJSP(inPath, columnsInfluence, imputationColumn, allowedError, outFile);

		Thread ANNPreparationJSPThread = new Thread(preparation);
//		BasicNetwork imputationNetwork = null;

		try {
			ANNPreparationJSPThread.start();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (ANNPreparationJSPThread.isAlive())
		{
			// do nothing	
		}

//		//open the saved imputation network
//		imputationNetwork = ANNPreparationJSP.openNetwork(ANNPreparationJSP.getImputationNetwork());
//		//imputationNetwork = ANNPreparationJSP.openNetwork("C:\\Users\\Florian Neukart\\workspace\\encog-java-core-2.5.3\\imputation");
//
//		System.out.println(ANNPreparationJSP.getTargetBaseFile());
//		impute(ANNPreparationJSP.getNumberInputNeurons(),1,ANNPreparationJSP.getTargetTrainingFile(),true, ANNPreparationJSP.getTargetBaseFile(), imputationNetwork);
	}
	
	public static void preparation2()
	{
		BasicNetwork imputationNetwork = null;
		//open the saved imputation network
		imputationNetwork = ANNPreparationJSP.openNetwork(ANNPreparationJSP.getImputationNetwork());
		//imputationNetwork = ANNPreparationJSP.openNetwork("C:\\Users\\Florian Neukart\\workspace\\encog-java-core-2.5.3\\imputation");

		System.out.println(ANNPreparationJSP.getTargetBaseFile());
		impute(ANNPreparationJSP.getNumberInputNeurons(),1,ANNPreparationJSP.getTargetTrainingFile(),true, ANNPreparationJSP.getTargetBaseFile(), imputationNetwork);
	}

	public static void impute(int numberInputNeurons, int numberOutputNeurons, String trainingFile, boolean networkOnlyPositiveInput, String baseFilePath, BasicNetwork imputationNetwork)
	{
		float floatNumberInputNeurons;
		float floatNumberOutputNeurons;
		float floatNumberHiddenNeurons;

		floatNumberInputNeurons = Float.valueOf(numberInputNeurons).floatValue();
		floatNumberOutputNeurons = Float.valueOf(numberOutputNeurons).floatValue();
		floatNumberHiddenNeurons = ((floatNumberInputNeurons / 3) * 2) + floatNumberOutputNeurons;
		Math.round(floatNumberHiddenNeurons);

		//BasicNetwork network = new BasicNetwork();
		MLDataSet baseFileTrainingSet;

		//load the training data out of the base file
		baseFileTrainingSet = TrainingSetUtil.loadCSVTOMemory(CSVFormat.ENGLISH, baseFilePath, false, numberInputNeurons, numberOutputNeurons);

		int col = 0;

		for(MLDataPair pair: baseFileTrainingSet)
		{
			final MLData output = imputationNetwork.compute(pair.getInput());
			System.out.println("Input Line " + col);

			//necessary, as more than one input neurons are likely
			for (int i = 0; i < numberInputNeurons; i++)
			{
				System.out.println(pair.getInput().getData(i));
			}

			//necessary, as more than one output neurons are likely. the ideal output is the same for each expert, as this must not vary.
			for (int i = 0; i < numberOutputNeurons; i++)
			{
				// value of the column to impute
				System.out.println("Imputation Neuron Value:" + "\n" + "actual=" + output.getData(i) /*+ ", ideal=" + pair.getIdeal().getData(i)*/);

				//set the imputation value for the completion of the incomplete file
				setImputationValue(output.getData(i));

				//the incomplete file output
				String targetCompleteIncomplete = "C:\\Users\\Florian Neukart\\workspace\\SHOCID\\imputation\\completeIncompleteFile.csv";
				String targetCompleteWhole = "C:\\Users\\Florian Neukart\\workspace\\SHOCID\\imputation\\completeWholeFile.csv";

				setIncompletePath("C:\\Users\\Florian Neukart\\workspace\\SHOCID\\imputation\\norm_impute_incomplete.csv");
				FileReader frCi = null;
				try {
					frCi = new FileReader(getIncompletePath());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				BufferedReader brCi = new BufferedReader(frCi);
				completeIncomplete(brCi, targetCompleteIncomplete, getImputationValue());
				try {
					brCi.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					frCi.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				setWholePath("C:\\Users\\Florian Neukart\\workspace\\SHOCID\\imputation\\norm_impute_prepared.csv");
				FileReader frCw = null;
				try {
					frCw = new FileReader(getWholePath());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				BufferedReader brCw = new BufferedReader(frCw);
				completeWhole(brCw, targetCompleteWhole, getImputationValue());
				try {
					brCw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					frCi.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			col = col+1;
		}
	}

	public static void completeIncomplete(BufferedReader br, String fileNameTarget, double ImputationValue)
	{
		try {

			StringTokenizer st = null;

			int lineNumber = 0;
			int tokenNumber = 0;

			FileWriter writer = new FileWriter(fileNameTarget);

			String currentToken = null;

			System.out.println("Imputed Datasets: \n");

			String fileName = getIncompletePath();

			ArrayList <String>storeValues = new ArrayList<String>();

			while((fileName = br.readLine()) != null)
			{

				lineNumber++;
				storeValues.add(fileName);
				//break comma separated line using ","
				st = new StringTokenizer(fileName.replace("NaN", String.valueOf(ImputationValue)));

				while(st.hasMoreTokens())
				{
					if (tokenNumber > 0)
					{
						writer.append(",");	
					}

					currentToken = st.nextToken();
					System.out.println("Line # " + lineNumber + 
							", Token # " + tokenNumber 
							+ ", Token : "+ currentToken);
					
					//for the GUI
					imputedDatasets.add("Line # " + lineNumber + 
							", Token # " + tokenNumber 
							+ ", Token : "+ currentToken);

					tokenNumber++;
					writer.append(currentToken);
				}

				//reset token number
				tokenNumber = 0;
				writer.append("\n");
			}

			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void completeWhole(BufferedReader br, String fileNameTarget, double ImputationValue)
	{
		try {

			StringTokenizer st = null;

			int lineNumber = 0;
			int tokenNumber = 0;

			FileWriter writer = new FileWriter(fileNameTarget);

			String currentToken = null;

			System.out.println("Imputed Datasets: \n");

			String fileName = getWholePath();

			ArrayList <String>storeValues = new ArrayList<String>();

			while((fileName = br.readLine()) != null)
			{

				lineNumber++;
				storeValues.add(fileName);
				//break comma separated line using ","
				st = new StringTokenizer(fileName.replace("NaN", String.valueOf(ImputationValue)));

				while(st.hasMoreTokens())
				{
					if (tokenNumber > 0)
					{
						writer.append(",");	
					}

					currentToken = st.nextToken();
					System.out.println("Line # " + lineNumber + 
							", Token # " + tokenNumber 
							+ ", Token : "+ currentToken);

					tokenNumber++;
					writer.append(currentToken);
				}

				//reset token number
				tokenNumber = 0;
				writer.append("\n");
			}

			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void setImputationValue(double iValue)
	{
		imputationValue = iValue;
	}

	private static double getImputationValue()
	{
		return imputationValue;
	}

	public static void setIncompletePath(String path)
	{
		incompleteFilePath = path;
	}

	private static String getIncompletePath()
	{
		return incompleteFilePath;
	}

	public static void setWholePath(String path)
	{
		wholeFilePath = path;
	}

	private static String getWholePath()
	{
		return wholeFilePath;
	}
	
	public static ArrayList<String> getImputedDatasets()
	{
		return imputedDatasets;
	}
}

//imputation\\norm_impute.csv