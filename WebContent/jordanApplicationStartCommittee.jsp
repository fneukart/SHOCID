<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="shocid.hoann.StartApplicationJSP"%>
<%@ page
	import="shocid.jordan.application.JordanCApplicationJSP"%>
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
            String committee_or_single = "c";
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
    		String agentInfoFile = request.getParameter("agentInfo");

    		if (request.getParameter("agents").equals("3")) {
    			adaptive = true;
    		}

            

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
            
            int numberAgents = JordanCApplicationJSP.getNumberCommitteeMembers();

            int numberInputNeurons = JordanCApplicationJSP
                    .getNumberInputNeurons();
            int numberOutputNeurons = JordanCApplicationJSP.getNumberOutputNeurons();
            MLDataSet trainingSet = JordanCApplicationJSP
                    .getCommitteeTrainingSet();
            int col = 0;
            double[][] inputValuesArray = JordanCApplicationJSP.getInputValues();
            
            double[][][] outputValuesArrayCommittee = JordanCApplicationJSP.getOverallOutputGUI();
            double[][] averageExpertValuesArray = JordanCApplicationJSP.getAverageExpertValuesGUI();
    %>

	<div id="AccordionCommittee" class="Accordion">
		<div class="AccordionPanel">
			<div class="AccordionPanelTab">Neuron outputs per Jordan agent</div>
			<div class="AccordionPanelContent">
				<h3>Neural Jordan propagation committee agent 1:</h3>
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
                                for (int o = 0; o < numberOutputNeurons; o++)
                                {
                        %>
						<td><%=outputValuesArrayCommittee[o][0][i]%></td>
						<%
                            }
                        %>
					</tr>
					<%
                        }
                    %>
				</table>
				<br />
				<%
        if(numberAgents >= 2)
        {
            %>
				<h3>Neural Jordan committee agent 2:</h3>
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
                                for (int o = 0; o < numberOutputNeurons; o++)
                                {
                        %>
						<td><%=outputValuesArrayCommittee[o][1][i]%></td>
						<%
                            }
                        %>
					</tr>
					<%
                        }
                    %>
				</table>
				<%} %>

				<br />
				<%
        if(numberAgents >= 3)
        {
            %>
				<h3>Neural Jordan committee agent 3:</h3>
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
                                for (int o = 0; o < numberOutputNeurons; o++)
                                {
                        %>
						<td><%=outputValuesArrayCommittee[o][2][i]%></td>
						<%
                            }
                        %>
					</tr>
					<%
                        }
                    %>
				</table>
				<%} %>

				<br />
				<%
        if(numberAgents >= 4)
        {
            %>
				<h3>Neural Jordan committee agent 4:</h3>
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
                                for (int o = 0; o < numberOutputNeurons; o++)
                                {
                        %>
						<td><%=outputValuesArrayCommittee[o][3][i]%></td>
						<%
                            }
                        %>
					</tr>
					<%
                        }
                    %>
				</table>
				<%} %>

				<br />
				<%
        if(numberAgents == 5)
        {
            %>
				<h3>Neural Jordan committee agent 5:</h3>
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
                                for (int o = 0; o < numberOutputNeurons; o++)
                                {
                        %>
						<td><%=outputValuesArrayCommittee[o][4][i]%></td>
						<%
                            }
                        %>
					</tr>
					<%
                        }
                    %>
				</table>
				<%} %>
			</div>
		</div>
		<div class="AccordionPanel">
			<div class="AccordionPanelTab">Committee output values</div>
			<div class="AccordionPanelContent">
				<h3>Committee output values:</h3>
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
						<th align="left">Committee output values:</th>
						<%
                                for (int o = 0; o < numberOutputNeurons; o++)
                                {
                        %>
						<td><%=averageExpertValuesArray[i][o]%></td>
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
        var AccordionCommittee = new Spry.Widget.Accordion("AccordionCommittee");
    </script>
	</div>

</body>
</html>