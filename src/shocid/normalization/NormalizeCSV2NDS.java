/*
 * Encog(tm) Core v2.6 Unit Test - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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
package shocid.normalization;

import java.io.File;

import junit.framework.TestCase;

import org.encog.NullStatusReportable;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.data.NeuralDataSet;
import org.encog.util.normalize.DataNormalization;
import org.encog.util.normalize.input.InputField;
import org.encog.util.normalize.input.InputFieldCSV;
import org.encog.util.normalize.output.OutputFieldRangeMapped;
import org.encog.util.normalize.output.multiplicative.MultiplicativeGroup;
import org.encog.util.normalize.output.multiplicative.OutputFieldMultiplicative;
import org.encog.util.normalize.target.NormalizationStorageCSV;
import org.encog.util.normalize.target.NormalizationStorageNeuralDataSet;

public class NormalizeCSV2NDS extends TestCase {
	
	public NormalizeCSV2NDS()
	{

	}

	public MLDataSet normalizeMultiplicative(File input, int numberInputNeurons, int numberOutputNeurons)
	{	
		InputField[] inputFieldArray = new InputField[numberInputNeurons + numberOutputNeurons];
		
		MultiplicativeGroup group = new MultiplicativeGroup();
		
		DataNormalization norm = new DataNormalization();
		norm.setReport(new NullStatusReportable());
		norm.setTarget(new NormalizationStorageNeuralDataSet(numberInputNeurons, numberOutputNeurons));
		
		for (int i = 0; i < (numberInputNeurons + numberOutputNeurons); i++)
		{
			norm.addInputField(inputFieldArray[i] = new InputFieldCSV(true,input,i));
		}
		
		for (int i = 0; i < numberInputNeurons; i++)
		{
			norm.addOutputField(new OutputFieldMultiplicative(group,inputFieldArray[i]));
		}
		
		for (int i = numberInputNeurons; i < (numberInputNeurons + numberOutputNeurons); i++)
		{
			//norm.addOutputField(new OutputFieldDirect(inputFieldArray[i]));
			//direct output is useless, as the output fields have to be normalized too
			norm.addOutputField(new OutputFieldMultiplicative(group,inputFieldArray[i]));
		}
		
		norm.process();
		MLDataSet normalizedSet = norm.getTarget().getDataset();
		
		return normalizedSet;
	}
	
	public MLDataSet normalizeRangeMapped(File input, int numberInputNeurons, int numberOutputNeurons/*, double low, double high*/)
	{		
		InputField[] inputFieldArray = new InputField[numberInputNeurons + numberOutputNeurons];
		
		DataNormalization norm = new DataNormalization();
		norm.setReport(new NullStatusReportable());
		norm.setTarget(new NormalizationStorageNeuralDataSet(numberInputNeurons, numberOutputNeurons));
		
		double low = -1.0;
		double high = 1.0;
		
		for (int i = 0; i < (numberInputNeurons + numberOutputNeurons); i++)
		{
			norm.addInputField(inputFieldArray[i] = new InputFieldCSV(true,input,i));
		}
		
		for (int i = 0; i < numberInputNeurons; i++)
		{
			norm.addOutputField(new OutputFieldRangeMapped(inputFieldArray[i],low,high));
		}
		
		for (int i = numberInputNeurons; i < (numberInputNeurons + numberOutputNeurons); i++)
		{
			//norm.addOutputField(new OutputFieldDirect(inputFieldArray[i]));
			//again, direct output is useless
			norm.addOutputField(new OutputFieldRangeMapped(inputFieldArray[i],low,high));
		}
		
		norm.process();
		MLDataSet normalizedSet = norm.getTarget().getDataset();
	
		return normalizedSet;
	}
}
