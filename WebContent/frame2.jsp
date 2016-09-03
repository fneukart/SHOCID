<%@ page contentType="text/html; charset=utf-8" language="java"
	import="java.sql.*" errorPage=""%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<style type="text/css">
body,td,th {
	font-family: "Lucida Sans Unicode", "Lucida Grande", sans-serif;
}
</style>
<script src="SpryAssets/SpryMenuBar.js" type="text/javascript"></script>
<link href="SpryAssets/SpryMenuBarHorizontal.css" rel="stylesheet"
	type="text/css" />
<body bgcolor="#CCCCCC">
	<h3>Task definition</h3>
	<ul id="TaskSelection" class="MenuBarHorizontal">
		<li><a href="#" class="MenuBarItemSubmenu">Classification</a>
			<ul>
				<li><a href="#" class="MenuBarItemSubmenu">Training</a>
					<ul>
					    <li><a href="quantum.jsp" title="Quantum network"
                            target="frame3">Quantum network</a>
                        </li>
						<li><a href="belief.jsp" title="Deep belief network"
                            target="frame3">Deep belief network</a>
                        </li>
						<li><a href="backprop.jsp" title="Back Propagation Learning"
							target="frame3">Quick & dirty (Back Propagation Learning)</a>
						</li>
						<li><a href="manhattan.jsp"
							title="Manhattan Update Rule Learning" target="frame3">Quick
								(Mahnattan Update Rule Learning)</a>
						</li>
						<li><a href="resprop.jsp"
							title="Resilient Propagation Learning" target="frame3">Efficient
								(Resilient Propagation Learning)</a>
						</li>
						<li><a href="simulated.jsp"
							title="Simulated Annealing Learning" target="frame3">Small
								evolution (Simulated Annealing Learning)</a>
						</li>
						<li><a href="genetic.jsp" title="Genetic Learning"
							target="frame3">Evolution (Genetic Learning)</a>
						</li>
						<li><a href="neat.jsp"
							title="Neuroevolution of Augmenting Topologies" target="frame3">Complex
								evolution (NeuroEvolution of Augmenting Topologies)</a>
						</li>
						<li><a href="neukartClassification.jsp"
							title="Neukart Learning" target="frame3">Optimal evolution
								(Neukart Learning)</a>
						</li>
						<li><a href="rbf.jsp" title="Radial Base Function Learning"
							target="frame3">Similarity evolution (Radial Base Function)</a>
						</li>
						<li><a href="corticalClassification.jsp"
							title="Cortical Genetic Learning" target="frame3">Cortical
								processing</a>
						</li>
						<li><a href="lma.jsp" title="Levenberg Marquardt Learning"
							target="frame3">Experimental (Levenberg Marquardt Learning)</a>
						</li>
						<li><a href="transgenetic.jsp" title="Transgenetic Evolution"
							target="frame3">Transgenetic Evolution</a>
						</li>
						<li><a href="transgeneticAIS.jsp" title="Transgenetic AIS Evolution"
                            target="frame3">Transgenetic AIS Evolution</a>
                        </li>
					</ul></li>
				<li><a href="#" class="MenuBarItemSubmenu">Application</a>
					<ul>
						<li><a href="backpropApplication.jsp"
							title="Back Propagation Application" target="frame3">Quick &
								dirty (Back Propagation Application)</a>
						</li>
						<li><a href="manhattanApplication.jsp"
							title="Manhattan Update Rule Learning" target="frame3">Quick
								(Mahnattan Update Rule Learning)</a>
						</li>
						<li><a href="respropApplication.jsp"
							title="Resilient Propagation Learning" target="frame3">Efficient
								(Resilient Propagation Learning)</a>
						</li>
						<li><a href="simulatedApplication.jsp"
							title="Simulated Annealing Learning" target="frame3">Small
								evolution (Simulated Annealing Learning)</a>
						</li>
						<li><a href="geneticApplication.jsp" title="Genetic Learning"
							target="frame3">Evolution (Genetic Learning)</a>
						</li>
						<li><a href="neatApplication.jsp"
							title="Neuroevolution of Augmenting Topologies" target="frame3">Complex
								evolution (NeuroEvolution of Augmenting Topologies)</a>
						</li>
						<li><a href="neukartApplication.jsp" title="Neukart Learning"
							target="frame3">Optimal evolution (Neukart Learning)</a>
						</li>
						<li><a href="rbfApplication.jsp"
							title="Radial Base Function Learning" target="frame3">Similarity
								evolution (Radial Base Function)</a>
						</li>
						<li><a href="corticalApplication.jsp"
							title="Cortical Learning" target="frame3">Cortical processing
								(Cortical ANN)</a>
						</li>
						<li><a href="lmaApplication.jsp"
							title="Levenberg Marquardt Learning" target="frame3">Experimental
								(Levenberg Marquardt Learning)</a>
						</li>
					</ul></li>
			</ul></li>

		<li><a href="#" class="MenuBarItemSubmenu">Predictive
				Analysis</a>
			<ul>
				<li><a href="#" class="MenuBarItemSubmenu">Training</a>
					<ul>
						<li><a href="neukartPrediction.jsp"
							title="Neukart Prediction" target="frame3">Neukart Prediction</a>
						</li>
						<li><a href="corticalPrediction.jsp"
							title="Cortical Prediction" target="frame3">Cortical
								Prediction</a></li>
						<li><a href="elman.jsp" title="Elman Prediction"
							target="frame3">Elman Prediction</a>
						</li>
						<li><a href="jordan.jsp" title="Jordan Prediction"
							target="frame3">Jordan Prediction</a>
						</li>

					</ul></li>
				<li><a href="#" class="MenuBarItemSubmenu">Application</a>
					<ul>
						<li><a href="neukartApplication.jsp"
							title="Neukart Prediction" target="frame3">Neukart Prediction</a>
						</li>
						<li><a href="corticalApplication.jsp"
							title="Cortical Learning" target="frame3">Cortical prediction
								(Cortical ANN)</a>
						</li>
						<li><a href="elmanApplication.jsp" title="Elman Prediction"
							target="frame3">Elman Prediction</a>
						</li>
						<li><a href="jordanApplication.jsp" title="Jordan Prediction"
							target="frame3">Jordan Prediction</a>
						</li>
					</ul></li>
			</ul></li>

		<li><a href="#" class="MenuBarItemSubmenu">Clustering</a>
			<ul>
				<li><a href="#" class="MenuBarItemSubmenu">Training</a>
					<ul>
						<li><a href="sofm.jsp" title="SOFM Clustering"
							target="frame3">SOFM Clustering</a>
						</li>
					</ul></li>
				<li><a href="#" class="MenuBarItemSubmenu">Application</a>
					<ul>
						<li><a href="sofmApplication.jsp" title="SOFM Clustering"
							target="frame3">SOFM Clustering</a>
						</li>
					</ul></li>
			</ul></li>


		<li><a href="#" class="MenuBarItemSubmenu">Value Imputation</a>
			<ul>
				<li><a href="#" class="MenuBarItemSubmenu">Training</a>
					<ul>
						<li><a href="imputation.jsp" title="Imputation"
							target="frame3">Genetic Value Imputation</a>
						</li>
					</ul></li>
				<li><a href="#" class="MenuBarItemSubmenu">Application</a>
					<ul>
						<li><a href="#">Genetic Value Imputation</a>
						</li>
					</ul></li>
			</ul></li>

	</ul>
	<p>&nbsp;</p>
	<script type="text/javascript">
		var MenuBar1 = new Spry.Widget.MenuBar("TaskSelection", {
			imgDown : "SpryAssets/SpryMenuBarDownHover.gif",
			imgRight : "SpryAssets/SpryMenuBarRightHover.gif"
		});
	</script>
</body>
</html>