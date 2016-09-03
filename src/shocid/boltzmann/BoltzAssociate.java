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
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.data.specific.BiPolarNeuralData;
import org.encog.neural.thermal.BoltzmannMachine;

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
public class BoltzAssociate {

	final static int HEIGHT = 10;
	final static int WIDTH = 10;

	/**
	 * The neural network will learn these patterns.
	 */
	public static final String[][] PATTERN  = { { 
		"O O O O O ",
		" O O O O O",
		"O O O O O ",
		" O O O O O",
		"O O O O O ",
		" O O O O O",
		"O O O O O ",
		" O O O O O",
		"O O O O O ",
	" O O O O O"  },

	{ "OO  OO  OO",
		"OO  OO  OO",
		"  OO  OO  ",
		"  OO  OO  ",
		"OO  OO  OO",
		"OO  OO  OO",
		"  OO  OO  ",
		"  OO  OO  ",
		"OO  OO  OO",
	"OO  OO  OO"  },

	{ "OOOOO     ",
		"OOOOO     ",
		"OOOOO     ",
		"OOOOO     ",
		"OOOOO     ",
		"     OOOOO",
		"     OOOOO",
		"     OOOOO",
		"     OOOOO",
	"     OOOOO"  },

	{ "O  O  O  O",
		" O  O  O  ",
		"  O  O  O ",
		"O  O  O  O",
		" O  O  O  ",
		"  O  O  O ",
		"O  O  O  O",
		" O  O  O  ",
		"  O  O  O ",
	"O  O  O  O"  },

	{ "OOOOOOOOOO",
		"O        O",
		"O OOOOOO O",
		"O O    O O",
		"O O OO O O",
		"O O OO O O",
		"O O    O O",
		"O OOOOOO O",
		"O        O",
	"OOOOOOOOOO"  } };

	/**
	 * The neural network will be tested on these patterns, to see
	 * which of the last set they are the closest to.
	 */
	public static final String[][] PATTERN2 = { { 
		"          ",
		"          ",
		"          ",
		"          ",
		"          ",
		" O O O O O",
		"O O O O O ",
		" O O O O O",
		"O O O O O ",
	" O O O O O"  },

	{ "OOO O    O",
		" O  OOO OO",
		"  O O OO O",
		" OOO   O  ",
		"OO  O  OOO",
		" O OOO   O",
		"O OO  O  O",
		"   O OOO  ",
		"OO OOO  O ",
	" O  O  OOO"  },

	{ "OOOOO     ",
		"O   O OOO ",
		"O   O OOO ",
		"O   O OOO ",
		"OOOOO     ",
		"     OOOOO",
		" OOO O   O",
		" OOO O   O",
		" OOO O   O",
	"     OOOOO"  },

	{ "O  OOOO  O",
		"OO  OOOO  ",
		"OOO  OOOO ",
		"OOOO  OOOO",
		" OOOO  OOO",
		"  OOOO  OO",
		"O  OOOO  O",
		"OO  OOOO  ",
		"OOO  OOOO ",
	"OOOO  OOOO"  },

	{ "OOOOOOOOOO",
		"O        O",
		"O        O",
		"O        O",
		"O   OO   O",
		"O   OO   O",
		"O        O",
		"O        O",
		"O        O",
	"OOOOOOOOOO"  } };

	public BiPolarNeuralData convertPattern(String[][] data, int index)
	{
		int resultIndex = 0;
		BiPolarNeuralData result = new BiPolarNeuralData(WIDTH*HEIGHT);
		for(int row=0;row<HEIGHT;row++)
		{
			for(int col=0;col<WIDTH;col++)
			{
				char ch = data[index][row].charAt(col);
				result.setData(resultIndex++, ch=='O');
			}
		}
		return result;
	}

	public void display(BiPolarNeuralData pattern1,BiPolarNeuralData pattern2)
	{
		int index1 = 0;
		int index2 = 0;

		for(int row = 0;row<HEIGHT;row++)
		{
			StringBuilder line = new StringBuilder();

			for(int col = 0;col<WIDTH;col++)
			{
				if(pattern1.getBoolean(index1++))
					line.append('O');
				else
					line.append(' ');
			}

			line.append("   ->   ");

			for(int col = 0;col<WIDTH;col++)
			{
				if(pattern2.getBoolean(index2++))
					line.append('O');
				else
					line.append(' ');
			}



			System.out.println(line.toString());
		}
	}

	public void evaluate(BoltzmannMachine boltzmannLogic, String[][] pattern)
	{
		for(int i=0;i<pattern.length;i++)
		{
			BiPolarNeuralData pattern1 = convertPattern(pattern,i);
			boltzmannLogic.setCurrentState(pattern1);
			do
			{
				boltzmannLogic.establishEquilibrium();
				boltzmannLogic.decreaseTemperature(0.99);
			}
			//			while (pattern1 != (BiPolarNeuralData)boltzmannLogic.getCurrentState());
			while (boltzmannLogic.getTemperature()>1);
			BiPolarNeuralData pattern2 = (BiPolarNeuralData)boltzmannLogic.getCurrentState();
			display( pattern1, pattern2);
			System.out.println("----------------------");
		}

	}

	public void run()
	{
		BoltzmannMachine boltzmannLogic = new BoltzmannMachine(WIDTH*HEIGHT);

		boltzmannLogic.setTemperature(100);
		//set the weight for the neural network
		for (int i = 0; i < boltzmannLogic.getOutputCount(); i++)
		{
			for (int j = boltzmannLogic.getOutputCount()-1; j > -1; j--)
			{
				boltzmannLogic.addWeight( i, j, (int) RangeRandomizer.randomize(-1, 1));
			}	
		}
		for(int i=0;i<PATTERN.length;i++)
		{
			boltzmannLogic.addPattern(convertPattern(PATTERN,i));
		}

		evaluate(boltzmannLogic,PATTERN);
		evaluate(boltzmannLogic,PATTERN2);
	}

	public static void main(String[] args)
	{
		BoltzAssociate program = new BoltzAssociate();
		program.run();
		Encog.getInstance().shutdown();
	}

}
