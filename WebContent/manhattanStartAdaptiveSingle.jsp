<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="shocid.hoann.StartJSP"%>
<%@ page import="shocid.ffann.training.adaptive.FFANNAdaptiveManhattanUpdateJSP"%>
<%@ page import="shocid.saveFile.ApplyFileDialog"%>
<%@ page import="org.encog.ml.data.MLDataSet"%>
<%@ page import="org.encog.neural.networks.BasicNetwork"%>
<%@ page import="org.encog.util.obj.SerializeObject"%>
<%-- <%@ page import="com.fusioncharts.FusionChartsHelper"%> --%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script language="JavaScript"
	src="C:\\Users\\Florian Neukart\\workspace\\SHOCID_310\\FusionChartsFree\\JSClass\\FusionCharts.js"></script>
<script src="SpryAssets/SpryAccordion.js" type="text/javascript"></script>
<title>Adaptive Mahnattan Update Data Processing</title>
<link href="SpryAssets/SpryAccordion.css" rel="stylesheet"
	type="text/css">
</head>
<body>
	<h1 class="heading">Adaptive manhattan update classification
		results</h1>

	<%
		String committee_or_single = "s";
		String predictive_or_pattern = "prt";
		String normalize = null;
		String normalizationType = null;

		String manualOrFlatInputfile = "f";
		String manualOrFlatOutputfile = "f";

		String saveNetwork = "y";

		String inputFilePath = "C:\\Users\\Florian Neukart\\workspace\\SHOCID_310\\"
				+ request.getParameter("input");
		String outputFilePath = "C:\\Users\\Florian Neukart\\workspace\\SHOCID_310\\"
				+ request.getParameter("output");
		System.out.println(inputFilePath);
		String numberInputNeuronsString = request
				.getParameter("inputNeurons");
		String numberOutputNeuronsString = request
				.getParameter("outputNeurons");
		String networkType = request.getParameter("networkType");
		String trainingMethod = request.getParameter("trainingMethod");
		boolean adaptive = false;
		double allowedError = Double.valueOf(request.getParameter("allowedError")).doubleValue()/100;

		if (request.getParameter("agents").equals("3")) {
			adaptive = true;
		}

		int numberAgents = Integer.valueOf(
				request.getParameter("numberAgentsApplied")).intValue();

		//             if (request.getParameter("agents").equals("1")) {
		//                 committee_or_single = "s";
		//             } else if (request.getParameter("agents").equals("0")) {
		//                 committee_or_single = "c";
		//             } else {
		//                 System.out.println("Invalid input.");
		//                 System.exit(1);
		//             }

		if (request.getParameter("normalization").equals("1")) {
			normalize = "y";
		} else if (request.getParameter("normalization").equals("0")) {
			normalize = "n";
		} else {
			System.out.println("Invalid input.");
			System.exit(1);
		}

		if (request.getParameter("normalizationType").equals("1")) {
			normalizationType = "c2c";
		} else if (request.getParameter("normalizationType").equals("0")) {
			normalizationType = "c2n";
		} else {
			System.out.println("Invalid input.");
			System.exit(1);
		}

		StartJSP.execute(committee_or_single, String.valueOf(numberAgents),
				predictive_or_pattern, normalize, normalizationType,
				manualOrFlatInputfile, manualOrFlatOutputfile,
				numberInputNeuronsString, numberOutputNeuronsString,
				inputFilePath, outputFilePath, networkType, trainingMethod,
				saveNetwork, adaptive, allowedError);
	%>

	<input type=button value="Back" onCLick="history.back()">
	<div id="AccordionSingle" class="Accordion">
		<%
			int numberInputNeurons = FFANNAdaptiveManhattanUpdateJSP
					.getNumberInputNeurons();
		int numberOutputNeurons = Integer.valueOf(numberOutputNeuronsString).intValue();
			MLDataSet trainingSet = FFANNAdaptiveManhattanUpdateJSP
					.getTrainingSet();
			BasicNetwork network = FFANNAdaptiveManhattanUpdateJSP
					.getBestNetwork();
			int col = 0;
			double[][] inputValuesArray = FFANNAdaptiveManhattanUpdateJSP
					.getInputValues(0);
			double[][] outputValuesArray = FFANNAdaptiveManhattanUpdateJSP
					.getOutputValuesBestOverall();
			double[][] idealValuesArray = FFANNAdaptiveManhattanUpdateJSP
					.getIdealValues(0);
			double[] error = FFANNAdaptiveManhattanUpdateJSP
					.getErrorBestNetwork();
			double[] errorSelected;
			int epochs = FFANNAdaptiveManhattanUpdateJSP.getBestEpochs();
		%>
		<div class="AccordionPanel">
			<div class="AccordionPanelTab">Error over iterations - graph of
				manhattan update agent</div>
			<div class="AccordionPanelContent">
				<%
					//Graph
					String[][] arrData = new String[error.length][2];

					//Epochs
					for (int epoch = 0; epoch < error.length; epoch++) {
						arrData[epoch][0] = String.valueOf(epoch + 1).toString();
						arrData[epoch][1] = String.valueOf(error[epoch]).toString();
					}

					//FusionChartsHelper class for colors
					//FusionChartsHelper colorHelper= new FusionChartsHelper();
					//convert this data into XML. 
					String strXML;
					//Initialize graph element
					strXML = "<graph caption='Error over iterations' showValues='0' decimalPrecision='20' showgridbg='1' showNames='0'>";

					//convert data to XML and append
					for (int l = 0; l < arrData.length; l++) {
						//                 strXML = strXML + "<set name='" +arrData[i][0] + "' value='" + arrData[i][1] + "' color='" + colorHelper.getFCColor() + "'/>";
						strXML = strXML + "<set name='" + arrData[l][0] + "' value='"
								+ arrData[l][1] + "' color='AFD8F8'/>";

					}
					//Close graph element
					strXML = strXML + "</graph>";

					//Create the chart
				%>

				<jsp:include
					page="/FusionChartsFree/Code/JSP/Includes/FusionChartsHTMLRenderer.jsp"
					flush="true">
					<jsp:param name="chartSWF"
						value="FusionChartsFree/Charts/FCF_Line.swf" />
					<jsp:param name="strURL" value="" />
					<jsp:param name="strXML" value="<%=strXML %>" />
					<jsp:param name="chartId" value="Error over iterations" />
					<jsp:param name="chartWidth" value="800" />
					<jsp:param name="chartHeight" value="350" />
					<jsp:param name="debugMode" value="false" />
				</jsp:include>
			</div>
		</div>
		<div class="AccordionPanel">
			<div class="AccordionPanelTab">Error over iterations - values</div>
			<div class="AccordionPanelContent">
				<h3><%=epochs%>
					iterations needed for attaining the allowed minimum error.
				</h3>
				<br />
				<table border=5 cellpadding="2" cellspacing="2">
					<tr>
						<th align="left">Epoch
						<th align="left">Error
					</tr>
					<%
						for (int epoch = 0; epoch < error.length; epoch++) {
					%>
					<tr>
						<td><%=epoch + 1%></td>
						<td><%=error[epoch]%></td>
					</tr>
					<%
						}
					%>
				</table>
				<br />

			</div>
		</div>
		<div class="AccordionPanel">
			<div class="AccordionPanelTab">Neuron outputs</div>
			<div class="AccordionPanelContent">
				<table border=5>
					<%
                        for (int i = 0; i < inputValuesArray.length; i++) {
                    %>
					<tr align="left">
						<th>Input data set number: <%=i%></th>
					<tr>
						<th align="left">Input values:</th>
						<%
                            for (int j = 0; j < inputValuesArray[i].length; j++) {
                        %>
						<td><%=inputValuesArray[i][j]%></td>
						<%
                            }
                        %>
					</tr>
					<tr>
						<th align="left">Ideal output values:</th>
						<%
                                for (int o = 0; o < numberOutputNeurons; o++)
                                {
                        %>
						<td><%=idealValuesArray[i][o]%></td>
						<%
                            }
                        %>
					</tr>
					<tr>
						<th align="left">Achieved output values:</th>
						<%
                                for (int j = 0; j < numberOutputNeurons; j++)
                                {
                        %>
						<td><%=outputValuesArray[i][j]%></td>
						<%
                            }
                        %>
					</tr>
					<%
                        }
                    %>
				</table>
			</div>
		</div>
		<div class="AccordionPanel">
			<div class="AccordionPanelTab">Documentation of evolution</div>
			<div class="AccordionPanelContent">
				<%
					int bestNetwork = 0;
					int hn1 = 0;
					int hn2 = 0;
					int bestEpochs;
					//print out best ANN results and details
					if (FFANNAdaptiveManhattanUpdateJSP.getBestSHLNetworkQuality() < FFANNAdaptiveManhattanUpdateJSP
							.getBestMHLNetworkQuality()) {
						bestNetwork = FFANNAdaptiveManhattanUpdateJSP
								.getSmallestErrorNetworkNumberSHL();
				%>
				Single hidden layer multi layer perceptron #
				<%=bestNetwork%>
				has performed best.
				<%
					} else {
						bestNetwork = FFANNAdaptiveManhattanUpdateJSP
								.getSmallestErrorNetworkNumberMHL();
				%>
				Multi hidden layer multi layer perceptron #
				<%=bestNetwork%>
				has performed best.
				<%
					}
				%>
				<br />
				<h5>Details</h5>
				<p>
					<%
						if (FFANNAdaptiveManhattanUpdateJSP.getBestSHLNetworkQuality() < FFANNAdaptiveManhattanUpdateJSP
								.getBestMHLNetworkQuality()) {
							hn1 = FFANNAdaptiveManhattanUpdateJSP
									.getBestHiddenLayer1Neurons();
					%>
					The network has
					<%=hn1%>
					neurons in the first layer.
					<%
						} else {
							hn1 = FFANNAdaptiveManhattanUpdateJSP
									.getBestHiddenLayer1Neurons();
							hn2 = FFANNAdaptiveManhattanUpdateJSP
									.getBestHiddenLayer2Neurons();
					%>
					The network has
					<%=hn1%>
					neurons in the first layer. The network has
					<%=hn2%>
					neurons in the second layer.
					<%
						}
						bestEpochs = FFANNAdaptiveManhattanUpdateJSP.getBestEpochs();
					%>

					<br />

					<%=bestEpochs%>
					epochs were needed for training.

				</p>


				<h5>SHL quality results:</h5>
				<br />
				<table border=0 width=100%>
					<%
						double qualitySHL = 0.0;
						int epochsSHL = 0;
						for (int i = 0; i < FFANNAdaptiveManhattanUpdateJSP
								.getQualitySHLArray().length; i++) {
							qualitySHL = FFANNAdaptiveManhattanUpdateJSP
									.getQualitySHLArray(i);
							epochsSHL = FFANNAdaptiveManhattanUpdateJSP.getEpochsSL(i);
					%>
					<tr>
						<td>Single hidden layer multi layer perceptron # <%=i%> has a
							quality of <%=qualitySHL%> and needed <%=epochsSHL%> epochs for
							training.</td>
					</tr>
					<%
						}
					%>
				</table>

				<h5>MHL quality results:</h5>
				<br />
				<table border=0 width=100%>
					<%
						double qualityMHL = 0.0;
						int epochsMHL = 0;
						for (int i = 0; i < FFANNAdaptiveManhattanUpdateJSP
								.getQualityMHLArray().length; i++) {
							qualityMHL = FFANNAdaptiveManhattanUpdateJSP
									.getQualityMHLArray(i);
							epochsMHL = FFANNAdaptiveManhattanUpdateJSP.getEpochsML(i);
					%>
					<tr>
						<td>Multi hidden layer multi layer perceptron # <%=i%> has a
							quality of <%=qualityMHL%> and needed <%=epochsMHL%> epochs for
							training.</td>
					</tr>
					<%
						}
					%>
				</table>

			</div>
		</div>
		<div class="AccordionPanel">
			<div class="AccordionPanelTab">Error over iterations - graphs
				of agent evolution</div>
			<div class="AccordionPanelContent">
				<form id="form1" name="form1" method="post"
					action="manhattanAdaptiveSingleDetails.jsp">
					<table>
						<tr>
							<td>
								<div id="SHLAgentDetails">
									<label for="SHLAgentDetails">SHL agent evolution
										details:</label><span id="SHLAgentDetails"> <select
										name="showSHLAgentDetails" size="1" id="showSHLAgentDetails">
											<%
												for (int networks = 0; networks < FFANNAdaptiveManhattanUpdateJSP
														.getQualitySHLArray().length; networks++) {
											%>
											<option value=<%=networks%>><%=networks%></option>
											<%
												}
											%>
									</select> </span>
								</div></td>
						</tr>
						<tr>
							<td>
								<div id="MHLAgentDetails">
									<label for="MHLAgentDetails">MHL agent evolution
										details:</label><span id="MHLAgentDetails"> <select
										name="showMHLAgentDetails" size="1" id="showMHLAgentDetails">
											<%
                                                for (int networks = 0; networks < FFANNAdaptiveManhattanUpdateJSP
                                                        .getQualityMHLArray().length; networks++) {
                                            %>
											<option value=<%=networks%>><%=networks%></option>
											<%
                                                }
                                            %>
									</select> </span>
								</div></td>
						</tr>
					</table>
					<input type="submit" name="execute" id="execute" value="Details" />
				</form>
			</div>
		</div>

		<h3>Neural Agent has automatically been saved under
			SHOCID\FFANNS\</h3>

		<script type="text/javascript">
			var AccordionAll = new Spry.Widget.Accordion("AccordionSingle");
		</script>
	</div>

</body>
</html>