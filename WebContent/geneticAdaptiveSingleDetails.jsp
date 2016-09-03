<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="shocid.hoann.StartJSP"%>
<%@ page import="shocid.ffann.training.adaptive.FFANNAdaptiveGeneticAlgorithmJSP"%>
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
<title>Adaptive Genetic Algorithm Data Processing</title>
<link href="SpryAssets/SpryAccordion.css" rel="stylesheet"
	type="text/css">
</head>
<body>
	<h1 class="heading">Adaptive genetic algorithm classification
		results</h1>
	<input type=button value="Back" onCLick="history.back()">
	<div id="AccordionSingle" class="Accordion">
		<%
			int numberInputNeurons = FFANNAdaptiveGeneticAlgorithmJSP
					.getNumberInputNeurons();
			MLDataSet trainingSet = FFANNAdaptiveGeneticAlgorithmJSP
					.getTrainingSet();
			BasicNetwork network = FFANNAdaptiveGeneticAlgorithmJSP
					.getBestNetwork();
			int col = 0;
			double[][] inputValuesArray = FFANNAdaptiveGeneticAlgorithmJSP
					.getInputValues(0);
			double[][] outputValuesArray = FFANNAdaptiveGeneticAlgorithmJSP
					.getOutputValuesBestOverall();
			double[][] idealValuesArray = FFANNAdaptiveGeneticAlgorithmJSP
					.getIdealValues(0);
			double[] error = FFANNAdaptiveGeneticAlgorithmJSP
					.getErrorBestNetwork();
			double[] errorSelected;
			int epochs = FFANNAdaptiveGeneticAlgorithmJSP.getBestEpochs();
			int SHLAgentNumber = Integer.valueOf(
					request.getParameter("showSHLAgentDetails")).intValue();
			int MHLAgentNumber = Integer.valueOf(
					request.getParameter("showMHLAgentDetails")).intValue();
			int position = 0;
			int jump = 0;
			String[][] arrData = null;

			String strXML;
		%>
		<div class="AccordionPanel">
			<div class="AccordionPanelTab">Error over iterations - graph of
				SHL genetic algorithm agent</div>
			<div class="AccordionPanelContent">


				<h2>Single hidden layer agent evolution</h2>
				<br />
				<%
					strXML = new String();

					if (FFANNAdaptiveGeneticAlgorithmJSP.getErrorsSL(SHLAgentNumber).length > 500) {
						errorSelected = new double[500];
						jump = Math.round(FFANNAdaptiveGeneticAlgorithmJSP
								.getErrorsSL(SHLAgentNumber).length / 500);

						for (int i = 0; i < 500; i++) {
							errorSelected[i] = FFANNAdaptiveGeneticAlgorithmJSP
									.getErrorsSL(SHLAgentNumber)[position];
							position = position + jump;
						}
					} else {
						errorSelected = FFANNAdaptiveGeneticAlgorithmJSP
								.getErrorsSL(SHLAgentNumber);
					}

					//Graph
					arrData = new String[errorSelected.length][2];

					//Epochs
					for (int epoch = 0; epoch < errorSelected.length; epoch++) {
						arrData[epoch][0] = String.valueOf(epoch + 1).toString();
						arrData[epoch][1] = String.valueOf(errorSelected[epoch])
								.toString();
					}

					//FusionChartsHelper class for colors
					//FusionChartsHelper colorHelper= new FusionChartsHelper();
					//convert this data into XML. 
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
				<table>
					<tr>
						<th>Single hidden layer agent <%=SHLAgentNumber%></th>
					</tr>
					<tr>
						<td><jsp:include
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
							</jsp:include></td>
					</tr>
				</table>
			</div>

			<div class="AccordionPanelTab">Error over iterations - graph of
				MHL genetic algorithm agent</div>
			<div class="AccordionPanelContent">


				<h2>Multi hidden layer agent evolution</h2>
				<br />
				<%
					strXML = new String();

					if (FFANNAdaptiveGeneticAlgorithmJSP.getErrorsML(MHLAgentNumber).length > 500) {
						errorSelected = new double[500];
						jump = Math.round(FFANNAdaptiveGeneticAlgorithmJSP
								.getErrorsML(MHLAgentNumber).length / 500);

						for (int i = 0; i < 500; i++) {
							errorSelected[i] = FFANNAdaptiveGeneticAlgorithmJSP
									.getErrorsML(MHLAgentNumber)[position];
							position = position + jump;
						}
					} else {
						errorSelected = FFANNAdaptiveGeneticAlgorithmJSP
								.getErrorsML(MHLAgentNumber);
					}

					//Graph
					arrData = new String[errorSelected.length][2];

					//Epochs
					for (int epoch = 0; epoch < errorSelected.length; epoch++) {
						arrData[epoch][0] = String.valueOf(epoch + 1).toString();
						arrData[epoch][1] = String.valueOf(errorSelected[epoch])
								.toString();
					}

					//FusionChartsHelper class for colors
					//FusionChartsHelper colorHelper= new FusionChartsHelper();
					//convert this data into XML. 
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
				<table>
					<tr>
						<th>Multi hidden layer agent <%=MHLAgentNumber%></th>
					</tr>
					<tr>
						<td><jsp:include
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
							</jsp:include></td>
					</tr>
				</table>
			</div>
		</div>

		<script type="text/javascript">
			var AccordionAll = new Spry.Widget.Accordion("AccordionSingle");
		</script>
	</div>

</body>
</html>