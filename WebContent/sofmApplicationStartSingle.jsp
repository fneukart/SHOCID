<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="shocid.hoann.StartApplicationJSP"%>
<%@ page import="shocid.hoann.HOANNCommitteeJSP"%>
<%@ page import="shocid.som.application.SelfOrganizingFeatureMapApplicationJSP"%>
<%@ page import="shocid.utilities.Util"%>
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

// 			String inputFilePath = "C:\\Users\\Florian Neukart\\workspace\\SHOCID_310\\"
// 					+ request.getParameter("input");
// 			String outputFilePath = "C:\\Users\\Florian Neukart\\workspace\\SHOCID_310\\"
// 					+ request.getParameter("output");
			String inputFilePath = request.getParameter("input");
			String outputFilePath = request.getParameter("output");
			String agentInfoFile = request.getParameter("agentInfo");
			System.out.println(inputFilePath);
			String networkType = request.getParameter("networkType");
			String trainingMethod = request.getParameter("trainingMethod");
			boolean adaptive = false;

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

			StartApplicationJSP.execute(committee_or_single, normalize, normalizationType, agentInfoFile,
					outputFilePath, inputFilePath, trainingMethod,
					adaptive);
			
			int numberInputNeurons = Util.getNumberInputNeurons();
         	int numberOutputNeurons = Util.getNumberOutputNeurons();
			MLDataSet trainingSet = SelfOrganizingFeatureMapApplicationJSP.getTrainingSet();
			BasicNetwork network = SelfOrganizingFeatureMapApplicationJSP.getNetwork();
			int col = 0;
			double[][] inputValuesArray = SelfOrganizingFeatureMapApplicationJSP
					.getInputValues();
			double[][] outputValuesArray = SelfOrganizingFeatureMapApplicationJSP
					.getOutputValues();
	         int[] inputToCluster = SelfOrganizingFeatureMapApplicationJSP
             .getInputToCluster();
     double[][] somInput = SelfOrganizingFeatureMapApplicationJSP.getSomInput();
		%>

	<div id="AccordionSingle" class="Accordion">
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
		<script type="text/javascript">
			var AccordionAll = new Spry.Widget.Accordion("AccordionSingle");
		</script>
	</div>

</body>
</html>