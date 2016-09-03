package shocid.hoann;

import shocid.cortical.CorticalApplicationJSP;
import shocid.cortical.CorticalCApplicationJSP;
import shocid.ffann.application.FFANNBackPropagationApplicationJSP;
import shocid.ffann.application.FFANNGeneticAlgorithmApplicationJSP;
import shocid.ffann.application.FFANNLevenbergMarquardtApplicationJSP;
import shocid.ffann.application.FFANNManhattanUpdateApplicationJSP;
import shocid.ffann.application.FFANNRadialBasisFunctionApplicationJSP;
import shocid.ffann.application.FFANNResilientPropagationApplicationJSP;
import shocid.ffann.application.FFANNSimulatedAnnealingApplicationJSP;
import shocid.ffann.committee.application.*;
import shocid.neatpopulation.application.NEATApplicationJSP;
import shocid.neatpopulation.application.NEATCApplicationJSP;
import shocid.neukart.application.NeukartApplicationJSP;
import shocid.neukart.committee.application.NeukartCApplicationJSP;
import shocid.som.application.SelfOrganizingFeatureMapApplicationJSP;

public class StartApplicationJSP {

	public static void execute(String cos, String norm, String normalizationType, String agentInfoFile, String normOutFile, String sourceNameAndPath, boolean adaptive) throws Exception
	{
		if (cos.equals("s"))
		{
			FFANNBackPropagationApplicationJSP bpa = new FFANNBackPropagationApplicationJSP();
			bpa.run(agentInfoFile, sourceNameAndPath, norm, normalizationType, normOutFile);
		}
		else if (cos.equals("c"))
		{
			BackPropagationCApplicationJSP bpac = new BackPropagationCApplicationJSP();
			bpac.run(agentInfoFile, sourceNameAndPath);
		}
	}

	public static void execute(String cos, String norm, String normalizationType, String agentInfoFile, String normOutFile, String sourceNameAndPath, String trainingMethod, boolean adaptive) throws Exception
	{
		if (cos.equals("s"))
		{
			if (adaptive == false)
			{
				if (trainingMethod.equals("b"))
				{
					FFANNBackPropagationApplicationJSP bpa = new FFANNBackPropagationApplicationJSP();
					bpa.run(agentInfoFile, sourceNameAndPath, norm, normalizationType, normOutFile);
				}

				else if (trainingMethod.equals("g"))
				{
					FFANNGeneticAlgorithmApplicationJSP gaa = new FFANNGeneticAlgorithmApplicationJSP();
					gaa.run(agentInfoFile, sourceNameAndPath, norm, normalizationType, normOutFile);
				}

				else if (trainingMethod.equals("lma"))
				{
					FFANNLevenbergMarquardtApplicationJSP lmaa = new FFANNLevenbergMarquardtApplicationJSP();
					lmaa.run(agentInfoFile, sourceNameAndPath, norm, normalizationType, normOutFile);
				}

				else if (trainingMethod.equals("m"))
				{
					FFANNManhattanUpdateApplicationJSP muaa = new FFANNManhattanUpdateApplicationJSP();
					muaa.run(agentInfoFile, sourceNameAndPath, norm, normalizationType, normOutFile);
				}

				else if (trainingMethod.equals("rad"))
				{
					FFANNRadialBasisFunctionApplicationJSP rbfa = new FFANNRadialBasisFunctionApplicationJSP();
					rbfa.run(agentInfoFile, sourceNameAndPath, norm, normalizationType, normOutFile);
				}

				else if (trainingMethod.equals("r"))
				{
					FFANNResilientPropagationApplicationJSP rpa = new FFANNResilientPropagationApplicationJSP();
					rpa.run(agentInfoFile, sourceNameAndPath, norm, normalizationType, normOutFile);
				}

				else if (trainingMethod.equals("a"))
				{
					FFANNSimulatedAnnealingApplicationJSP saa = new FFANNSimulatedAnnealingApplicationJSP();
					saa.run(agentInfoFile, sourceNameAndPath, norm, normalizationType, normOutFile);
				}

				else if (trainingMethod.equals("neukart"))
				{
					NeukartApplicationJSP na = new NeukartApplicationJSP();
					na.run(agentInfoFile, sourceNameAndPath, norm, normalizationType, normOutFile);
				}

				else if (trainingMethod.equals("neat"))
				{
					NEATApplicationJSP neata = new NEATApplicationJSP();
					neata.run(agentInfoFile, sourceNameAndPath);
				}

				else if (trainingMethod.equals("cortical"))
				{
					CorticalApplicationJSP cortical = new CorticalApplicationJSP();
					cortical.run(agentInfoFile, sourceNameAndPath, norm, normalizationType, normOutFile);
				}

				else if (trainingMethod.equals("neukart"))
				{
					SelfOrganizingFeatureMapApplicationJSP sofm = new SelfOrganizingFeatureMapApplicationJSP();
					sofm.run(agentInfoFile, sourceNameAndPath, norm, normalizationType, normOutFile);
				}

				//System.exit(1);
			}

			else if (adaptive == true)
			{
				if (trainingMethod.equals("b"))
				{
					FFANNBackPropagationApplicationJSP bpa = new FFANNBackPropagationApplicationJSP();
					bpa.run(agentInfoFile, sourceNameAndPath, norm, normalizationType, normOutFile);
				}

				else if (trainingMethod.equals("g"))
				{
					FFANNGeneticAlgorithmApplicationJSP gaa = new FFANNGeneticAlgorithmApplicationJSP();
					gaa.run(agentInfoFile, sourceNameAndPath, norm, normalizationType, normOutFile);
				}

				else if (trainingMethod.equals("lma"))
				{
					FFANNLevenbergMarquardtApplicationJSP lmaa = new FFANNLevenbergMarquardtApplicationJSP();
					lmaa.run(agentInfoFile, sourceNameAndPath, norm, normalizationType, normOutFile);
				}

				else if (trainingMethod.equals("m"))
				{
					FFANNManhattanUpdateApplicationJSP muaa = new FFANNManhattanUpdateApplicationJSP();
					muaa.run(agentInfoFile, sourceNameAndPath, norm, normalizationType, normOutFile);
				}

				else if (trainingMethod.equals("r"))
				{
					FFANNResilientPropagationApplicationJSP rpa = new FFANNResilientPropagationApplicationJSP();
					rpa.run(agentInfoFile, sourceNameAndPath, norm, normalizationType, normOutFile);
				}

				else if (trainingMethod.equals("a"))
				{
					FFANNSimulatedAnnealingApplicationJSP saa = new FFANNSimulatedAnnealingApplicationJSP();
					saa.run(agentInfoFile, sourceNameAndPath, norm, normalizationType, normOutFile);
				}

				else if (trainingMethod.equals("neukart"))
				{
					NeukartApplicationJSP na = new NeukartApplicationJSP();
					na.run(agentInfoFile, sourceNameAndPath, norm, normalizationType, normOutFile);
				}

				//System.exit(1);
			}
		}

		else if (cos.equals("c"))
		{
			if (adaptive == false)
			{
				if (trainingMethod.equals("b"))
				{
					BackPropagationCApplicationJSP bpac = new BackPropagationCApplicationJSP();
					bpac.run(agentInfoFile, sourceNameAndPath);
				}
				else if (trainingMethod.equals("g"))
				{
					GeneticAlgorithmCApplicationJSP gaac = new GeneticAlgorithmCApplicationJSP();
					gaac.run(agentInfoFile, sourceNameAndPath);
				}

				else if (trainingMethod.equals("lma"))
				{
					LevenbergMarquardtCApplicationJSP lmaaa = new LevenbergMarquardtCApplicationJSP();
					lmaaa.run(agentInfoFile, sourceNameAndPath);
				}

				else if (trainingMethod.equals("m"))
				{
					ManhattanUpdateCApplicationJSP muaac = new ManhattanUpdateCApplicationJSP();
					muaac.run(agentInfoFile, sourceNameAndPath);
				}

				else if (trainingMethod.equals("rad"))
				{
					RadialBasisFunctionCApplicationJSP rbfac = new RadialBasisFunctionCApplicationJSP();
					rbfac.run(agentInfoFile, sourceNameAndPath);
				}

				else if (trainingMethod.equals("r"))
				{
					ResilientPropagationCApplicationJSP rpac = new ResilientPropagationCApplicationJSP();
					rpac.run(agentInfoFile, sourceNameAndPath);
				}

				else if (trainingMethod.equals("a"))
				{
					SimulatedAnnealingCApplicationJSP saac = new SimulatedAnnealingCApplicationJSP();
					saac.run(agentInfoFile, sourceNameAndPath);
				}

				else if (trainingMethod.equals("neukart"))
				{
					NeukartCApplicationJSP nac = new NeukartCApplicationJSP();
					nac.run(agentInfoFile, sourceNameAndPath);
				}

				else if (trainingMethod.equals("neat"))
				{
					NEATCApplicationJSP neatac = new NEATCApplicationJSP();
					neatac.run(agentInfoFile, sourceNameAndPath);
				}

				else if (trainingMethod.equals("cortical"))
				{
					CorticalCApplicationJSP corticalc = new CorticalCApplicationJSP();
					corticalc.run(agentInfoFile, sourceNameAndPath);
				}
			}

			else if (adaptive == true)
			{
				if (trainingMethod.equals("b"))
				{
					BackPropagationCApplicationJSP bpac = new BackPropagationCApplicationJSP();
					bpac.run(agentInfoFile, sourceNameAndPath);
				}
				else if (trainingMethod.equals("g"))
				{
					GeneticAlgorithmCApplicationJSP gaac = new GeneticAlgorithmCApplicationJSP();
					gaac.run(agentInfoFile, sourceNameAndPath);
				}

				else if (trainingMethod.equals("lma"))
				{
					LevenbergMarquardtCApplicationJSP lmaaa = new LevenbergMarquardtCApplicationJSP();
					lmaaa.run(agentInfoFile, sourceNameAndPath);
				}

				else if (trainingMethod.equals("m"))
				{
					ManhattanUpdateCApplicationJSP muaac = new ManhattanUpdateCApplicationJSP();
					muaac.run(agentInfoFile, sourceNameAndPath);
				}

				else if (trainingMethod.equals("r"))
				{
					ResilientPropagationCApplicationJSP rpac = new ResilientPropagationCApplicationJSP();
					rpac.run(agentInfoFile, sourceNameAndPath);
				}

				else if (trainingMethod.equals("a"))
				{
					SimulatedAnnealingCApplicationJSP saac = new SimulatedAnnealingCApplicationJSP();
					saac.run(agentInfoFile, sourceNameAndPath);
				}

				else if (trainingMethod.equals("neukart"))
				{
					NeukartCApplicationJSP nac = new NeukartCApplicationJSP();
					nac.run(agentInfoFile, sourceNameAndPath);
				}
			}
		}

		else
		{
			System.out.println("Invalid input.");
			System.exit(1);
		}
	}
}