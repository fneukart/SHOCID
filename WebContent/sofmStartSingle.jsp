<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="shocid.hoann.StartJSP"%>
<%@ page import="shocid.hoann.HOANNCommitteeJSP"%>
<%@ page import="shocid.som.SelfOrganizingFeatureMapJSP"%>
<%@ page import="shocid.ffann.committee.training.GeneticAlgorithmCJSP"%>
<%@ page import="shocid.saveFile.ApplyFileDialog"%>
<%@ page import="org.encog.ml.data.MLDataSet"%>
<%@ page import="org.encog.neural.som.SOM"%>
<%@ page import="org.encog.util.obj.SerializeObject"%>
<%-- <%@ page import="com.fusioncharts.FusionChartsHelper"%> --%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script language="JavaScript"
	src="C:\\Users\\Florian Neukart\\workspace\\SHOCID_310\\FusionChartsFree\\JSClass\\FusionCharts.js"></script>
<script src="SpryAssets/SpryAccordion.js" type="text/javascript"></script>
<title>SOFM Data Processing</title>
<link href="SpryAssets/SpryAccordion.css" rel="stylesheet"
	type="text/css">
</head>
<body>
	<h1 class="heading">SOFM classification results</h1>

	<%
		String committee_or_single = "s";
		String predictive_or_pattern = "prt";
		String normalize = null;
		String normalizationType = null;

		String manualOrFlatInputfile = "f";
		String manualOrFlatOutputfile = "f";

		String saveNetwork = "y";

// 		String inputFilePath = "C:\\Users\\Florian Neukart\\workspace\\SHOCID_310\\"
// 				+ request.getParameter("input");
// 		String outputFilePath = "C:\\Users\\Florian Neukart\\workspace\\SHOCID_310\\"
// 				+ request.getParameter("output");
		String inputFilePath = request.getParameter("input");
		String outputFilePath = request.getParameter("output");
		System.out.println(inputFilePath);
		String numberInputNeuronsString = request
				.getParameter("inputNeurons");
		String numberOutputNeuronsString = request
				.getParameter("outputNeurons");
		String networkType = request.getParameter("networkType");
		String trainingMethod = request.getParameter("trainingMethod");
		double allowedError = Double.valueOf(request.getParameter("allowedError")).doubleValue()/100;

		boolean adaptive = false;


		int numberAgents = 1;

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


	<div id="AccordionSingle" class="Accordion">
		<%
			int numberInputNeurons = SelfOrganizingFeatureMapJSP
					.getNumberInputNeurons();
			int numberOutputNeurons = Integer
					.valueOf(numberOutputNeuronsString).intValue();
			MLDataSet trainingSet = SelfOrganizingFeatureMapJSP
					.getTrainingSet();
			SOM network = SelfOrganizingFeatureMapJSP.getNetwork();
			int col = 0;
			double[][] inputValuesArray = SelfOrganizingFeatureMapJSP
					.getInputValues();
			double[][] outputValuesArray = SelfOrganizingFeatureMapJSP
					.getOutputValues();
			double[] error = SelfOrganizingFeatureMapJSP.getError();
			int epochs = SelfOrganizingFeatureMapJSP.getEpochs();
			int[] inputToCluster = SelfOrganizingFeatureMapJSP
					.getInputToCluster();
			double[][] somInput = SelfOrganizingFeatureMapJSP.getSomInput();
		%>
		<div class="AccordionPanel">
			<div class="AccordionPanelTab">Error over generations - graph
				of SOFM agent</div>
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
					int l = 0;
					//Initialize graph element
					strXML = "<graph caption='Error over generations' showValues='0' decimalPrecision='20' showgridbg='1' showNames='0'>";

					//convert data to XML and append
					for (l = 0; l < arrData.length; l++) {
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
					<jsp:param name="chartId" value="Error over generations" />
					<jsp:param name="chartWidth" value="800" />
					<jsp:param name="chartHeight" value="350" />
					<jsp:param name="debugMode" value="false" />
				</jsp:include>
			</div>
		</div>
		<div class="AccordionPanel">
			<div class="AccordionPanelTab">Error over generations - values</div>
			<div class="AccordionPanelContent">
				<h3><%=epochs%>
					generations needed for attaining the allowed minimum error.
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
						<th align="left">Output Cluster:</th>
						<td><%=inputToCluster[i]%></td>
					</tr>
					<%
						}
					%>
				</table>
			</div>
        </div>
			<div class="AccordionPanel">
				<div class="AccordionPanelTab">Cluster assignments</div>
				<div class="AccordionPanelContent">
					<table border=0>
						<%
							for (int j = 0; j < numberOutputNeurons; j++) {
								int assignments = 0;
								for (int i = 0; i < inputValuesArray.length; i++) {
									if (inputToCluster[i] == j) {
										assignments++;
									}
								}
						%>
						<tr align="left">
							<td>Cluster <%=j%> has <%=assignments%> assignments</td>
						</tr>
						<%
							}
						%>
					</table>
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