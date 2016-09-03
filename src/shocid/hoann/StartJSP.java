package shocid.hoann;

import shocid.imputation.ANNImputationJSP;
import shocid.utilities.Util;

public class StartJSP {
	private static String[][] values = null;

	public static void execute(String cos, String numberAgents, String predictiveOrPattern, String norm, String normalizationType, String manualInputOrFlatfile, String manualOutputOrFlatfile, String numberInputNeuronsString, String numberOutputNeuronsString, String inFile, String outFile, String nwType, String trainingMethod, String saveNetwork, boolean adaptive, double allowedError) throws Exception
	{
		if (norm.equals("y"))
		{
			Util.setNormalization(1);
		}
		else
		{
			Util.setNormalization(0);
		}
		
		if (cos.equals("s"))
		{
			if (adaptive == false)
			{
				HOANNJSP.startHOANN(predictiveOrPattern, norm, normalizationType, manualInputOrFlatfile, manualOutputOrFlatfile, numberInputNeuronsString, numberOutputNeuronsString, inFile, outFile, nwType, trainingMethod, saveNetwork, allowedError);
				//System.exit(1);
			}
			else
			{
				AdaptiveHOANNJSP.startHOANN(predictiveOrPattern, norm, normalizationType, manualInputOrFlatfile, manualOutputOrFlatfile, numberInputNeuronsString, numberOutputNeuronsString, inFile, outFile, nwType, trainingMethod, saveNetwork, allowedError);
			}
		}

		else if (cos.equals("c"))
		{
			if (adaptive == false)
			{
				HOANNCommitteeJSP.startHOANNCommittee(norm, numberAgents, normalizationType, manualInputOrFlatfile, manualOutputOrFlatfile, numberInputNeuronsString, numberOutputNeuronsString, inFile, outFile, nwType, trainingMethod, saveNetwork, allowedError);
				//System.exit(1);
			}
			else
			{
				AdaptiveHybridHOANNCommitteeJSP.startHOANNCommittee(norm, numberAgents, normalizationType, manualInputOrFlatfile, manualOutputOrFlatfile, numberInputNeuronsString, numberOutputNeuronsString, inFile, outFile, nwType, trainingMethod, saveNetwork, allowedError);
			}
		}

		else
		{
			System.out.println("Invalid input.");
			System.exit(1);
		}
	}
	
	public static void executeImputation(String inFile, String columnsInfluence, String imputationColumn, double allowedError, String outFile)
	{
		ANNImputationJSP.imputation(inFile, columnsInfluence, imputationColumn, allowedError, outFile);
	}
}