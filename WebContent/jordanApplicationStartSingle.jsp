<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="shocid.hoann.StartApplicationJSP"%>
<%@ page import="shocid.hoann.HOANNCommitteeJSP"%>
<%@ page import="shocid.jordan.application.JordanApplicationJSP"%>
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
<title>Jordan Data Processing</title>
<link href="SpryAssets/SpryAccordion.css" rel="stylesheet"
	type="text/css">
</head>
<body>
	<h1 class="heading">Jordan classification results</h1>

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
			String agentInfoFile = request.getParameter("agentInfo");
			System.out.println(inputFilePath);
			String networkType = request.getParameter("networkType");
			String trainingMethod = request.getParameter("trainingMethod");
			boolean adaptive = false;


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

			StartApplicationJSP.execute(committee_or_single, normalize, normalizationType, agentInfoFile,
					outputFilePath, inputFilePath, trainingMethod,
					adaptive);
			
			int numberInputNeurons = Util.getNumberInputNeurons();
         	int numberOutputNeurons = Util.getNumberOutputNeurons();
			MLDataSet trainingSet = JordanApplicationJSP.getTrainingSet();
			BasicNetwork network = JordanApplicationJSP.getNetwork();
			int col = 0;
			double[][] inputValuesArray = JordanApplicationJSP
					.getInputValues();
			double[][] outputValuesArray = JordanApplicationJSP
					.getOutputValues();
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
		<script type="text/javascript">
			var AccordionAll = new Spry.Widget.Accordion("AccordionSingle");
		</script>
	</div>

</body>
</html>