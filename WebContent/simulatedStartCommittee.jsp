<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="shocid.hoann.StartJSP"%>
<%@ page import="shocid.hoann.HOANNCommitteeJSP"%>
<%@ page import="shocid.ffann.training.FFANNSimulatedAnnealingJSP"%>
<%@ page import="shocid.ffann.committee.training.SimulatedAnnealingCJSP"%>
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
<title>Simulated Annealing Data Processing</title>
<link href="SpryAssets/SpryAccordion.css" rel="stylesheet"
	type="text/css">
</head>
<body>
	<h1 class="heading">Simulated annealing classification results</h1>

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
            double allowedError = Double.valueOf(request.getParameter("allowedError")).doubleValue()/100;
            
			boolean adaptive = false;

			if (request.getParameter("agents").equals("3")) {
				adaptive = true;
			}

            int numberAgents = Integer.valueOf(request.getParameter("numberAgentsApplied")).intValue();

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

            int numberInputNeurons = SimulatedAnnealingCJSP
                    .getNumberInputNeurons();
            int numberOutputNeurons = SimulatedAnnealingCJSP.getNumberOutputNeurons();
            MLDataSet trainingSet = SimulatedAnnealingCJSP
                    .getCommitteeTrainingSet();
            BasicNetwork[] trainedCommittee = HOANNCommitteeJSP
                    .getTrainedCommittee();
            String[] trainedCommitteeNames = HOANNCommitteeJSP.getTrainedCommitteeMemberNames();
            int col = 0;
            double[][] inputValuesArray = SimulatedAnnealingCJSP.getInputValues();
            
            double[][][] outputValuesArrayCommittee = SimulatedAnnealingCJSP.getOverallOutputGUI();
            double[][] averageExpertValuesArray = SimulatedAnnealingCJSP.getAverageExpertValuesGUI();
                        
            HashMap<Integer, Double> agent1EpochErrors = new HashMap<Integer, Double>();
            HashMap<Integer, Double> agent2EpochErrors = new HashMap<Integer, Double>();
            HashMap<Integer, Double> agent3EpochErrors = new HashMap<Integer, Double>();
            HashMap<Integer, Double> agent4EpochErrors = new HashMap<Integer, Double>();
            HashMap<Integer, Double> agent5EpochErrors = new HashMap<Integer, Double>();

            ArrayList<Double> agent1Errors = new ArrayList<Double>();
            ArrayList<Double> agent2Errors = new ArrayList<Double>();
            ArrayList<Double> agent3Errors = new ArrayList<Double>();
            ArrayList<Double> agent4Errors = new ArrayList<Double>();
            ArrayList<Double> agent5Errors = new ArrayList<Double>();
            
            int agent1Epochs = 0;
            int agent2Epochs = 0;
            int agent3Epochs = 0;
            int agent4Epochs = 0;
            int agent5Epochs = 0;
            
            switch(numberAgents)
                {
                case 1:
                    agent1Errors = SimulatedAnnealingCJSP.getAgentErrors(1);
                    
                    agent1Epochs = HOANNCommitteeJSP.getExpertEpochs(1);
                    break;
                case 2:
                    agent1Errors = SimulatedAnnealingCJSP.getAgentErrors(1);
                    agent2Errors = SimulatedAnnealingCJSP.getAgentErrors(2);
                    
                    agent1Epochs = HOANNCommitteeJSP.getExpertEpochs(1);
                    agent2Epochs = HOANNCommitteeJSP.getExpertEpochs(2);
                    break;
                case 3:
                    agent1Errors = SimulatedAnnealingCJSP.getAgentErrors(1);
                    agent2Errors = SimulatedAnnealingCJSP.getAgentErrors(2);
                    agent3Errors = SimulatedAnnealingCJSP.getAgentErrors(3);
                    
                    agent1Epochs = HOANNCommitteeJSP.getExpertEpochs(1);
                    agent2Epochs = HOANNCommitteeJSP.getExpertEpochs(2);
                    agent3Epochs = HOANNCommitteeJSP.getExpertEpochs(3);
                    break;
                case 4:
                    agent1Errors = SimulatedAnnealingCJSP.getAgentErrors(1);
                    agent2Errors = SimulatedAnnealingCJSP.getAgentErrors(2);
                    agent3Errors = SimulatedAnnealingCJSP.getAgentErrors(3);
                    agent4Errors = SimulatedAnnealingCJSP.getAgentErrors(4);
                    
                    agent1Epochs = HOANNCommitteeJSP.getExpertEpochs(1);
                    agent2Epochs = HOANNCommitteeJSP.getExpertEpochs(2);
                    agent3Epochs = HOANNCommitteeJSP.getExpertEpochs(3);
                    agent4Epochs = HOANNCommitteeJSP.getExpertEpochs(4);
                    break;
                case 5:
                    agent1Errors = SimulatedAnnealingCJSP.getAgentErrors(1);
                    agent2Errors = SimulatedAnnealingCJSP.getAgentErrors(2);
                    agent3Errors = SimulatedAnnealingCJSP.getAgentErrors(3);
                    agent4Errors = SimulatedAnnealingCJSP.getAgentErrors(4);
                    agent5Errors = SimulatedAnnealingCJSP.getAgentErrors(5);
                    
                    agent1Epochs = HOANNCommitteeJSP.getExpertEpochs(1);
                    agent2Epochs = HOANNCommitteeJSP.getExpertEpochs(2);
                    agent3Epochs = HOANNCommitteeJSP.getExpertEpochs(3);
                    agent4Epochs = HOANNCommitteeJSP.getExpertEpochs(4);
                    agent5Epochs = HOANNCommitteeJSP.getExpertEpochs(5);
                    break;
                }

            double[][] idealValuesArray = SimulatedAnnealingCJSP
                    .getIdealValues();
            int epochs = SimulatedAnnealingCJSP.getEpochs();
    %>

	<div id="AccordionCommittee" class="Accordion">
		<div class="AccordionPanel">
			<div class="AccordionPanelTab">Error over iterations - graphs
				per simulated annealing agent</div>
			<div class="AccordionPanelContent">
				<table>
					<tr>
						<td>
							<%
                //Agent 1 Graph
                    //Graph
                        String[][] arrDataA1 = new String[agent1Errors.size()][2];

                        //Epochs
                        
                        for (int epoch = 0; epoch < agent1Errors.size(); epoch++) {
                            arrDataA1[epoch][0] = String.valueOf(epoch + 1).toString();
                            arrDataA1[epoch][1] = String.valueOf(agent1Errors.get(epoch)).toString();
                        }

                        //FusionChartsHelper class for colors
                        //FusionChartsHelper colorHelper= new FusionChartsHelper();
                        //convert this data into XML. 
                        String strXMLA1;
                        int a1 = 0;
                        //Initialize graph element
                        strXMLA1 = "<graph caption='Error over iterations - simulated annealing agent 1' showValues='0' decimalPrecision='20' showgridbg='1' showNames='0'>";

                        //convert data to XML and append
                        for (a1 = 0; a1 < arrDataA1.length; a1++) {
                            //                 strXML = strXML + "<set name='" +arrData[i][0] + "' value='" + arrData[i][1] + "' color='" + colorHelper.getFCColor() + "'/>";
                            strXMLA1 = strXMLA1 + "<set name='" + arrDataA1[a1][0]
                                    + "' value='" + arrDataA1[a1][1]
                                    + "' color='AFD8F8'/>";

                        }
                        //Close graph element
                        strXMLA1 = strXMLA1 + "</graph>";

                        //Create the chart
                %> <jsp:include
								page="/FusionChartsFree/Code/JSP/Includes/FusionChartsHTMLRenderer.jsp"
								flush="true">
								<jsp:param name="chartSWF"
									value="FusionChartsFree/Charts/FCF_Line.swf" />
								<jsp:param name="strURL" value="" />
								<jsp:param name="strXML" value="<%=strXMLA1%>" />
								<jsp:param name="chartId" value="Error over iterations" />
								<jsp:param name="chartWidth" value="800" />
								<jsp:param name="chartHeight" value="350" />
								<jsp:param name="debugMode" value="false" />
							</jsp:include></td>

						<td>
							<%
        if(numberAgents >= 2)
        {
            %> <%
             //Agent 2 Graph
                 //Graph
                     String[][] arrDataA2 = new String[agent2Errors.size()][2];

                     //Epochs
                     
                     for (int epoch = 0; epoch < agent2Errors.size(); epoch++) {
                         arrDataA2[epoch][0] = String.valueOf(epoch + 1).toString();
                         arrDataA2[epoch][1] = String.valueOf(agent2Errors.get(epoch)).toString();
                     }

                     //FusionChartsHelper class for colors
                     //FusionChartsHelper colorHelper= new FusionChartsHelper();
                     //convert this data into XML. 
                     String strXMLA2;
                     int a2 = 0;
                     //Initialize graph element
                     strXMLA2 = "<graph caption='Error over iterations - simulated annealing agent 2' showValues='0' decimalPrecision='20' showgridbg='1' showNames='0'>";

                     //convert data to XML and append
                     for (a2 = 0; a2 < arrDataA2.length; a2++) {
                         //                 strXML = strXML + "<set name='" +arrData[i][0] + "' value='" + arrData[i][1] + "' color='" + colorHelper.getFCColor() + "'/>";
                         strXMLA2 = strXMLA2 + "<set name='" + arrDataA2[a2][0]
                                 + "' value='" + arrDataA2[a2][1]
                                 + "' color='AFD8F8'/>";

                     }
                     //Close graph element
                     strXMLA2 = strXMLA2 + "</graph>";

                     //Create the chart
             %> <jsp:include
								page="/FusionChartsFree/Code/JSP/Includes/FusionChartsHTMLRenderer.jsp"
								flush="true">
								<jsp:param name="chartSWF"
									value="FusionChartsFree/Charts/FCF_Line.swf" />
								<jsp:param name="strURL" value="" />
								<jsp:param name="strXML" value="<%=strXMLA2%>" />
								<jsp:param name="chartId" value="Error over iterations" />
								<jsp:param name="chartWidth" value="800" />
								<jsp:param name="chartHeight" value="350" />
								<jsp:param name="debugMode" value="false" />
							</jsp:include> <%
        }
        %>
						</td>

					</tr>
					<tr>

						<td>
							<%
        if(numberAgents >= 3)
        {
            %> <%
             //Agent 3 Graph
                 //Graph
                     String[][] arrDataA3 = new String[agent3Errors.size()][2];

                     //Epochs
                     
                     for (int epoch = 0; epoch < agent3Errors.size(); epoch++) {
                         arrDataA3[epoch][0] = String.valueOf(epoch + 1).toString();
                         arrDataA3[epoch][1] = String.valueOf(agent3Errors.get(epoch)).toString();
                     }

                     //FusionChartsHelper class for colors
                     //FusionChartsHelper colorHelper= new FusionChartsHelper();
                     //convert this data into XML. 
                     String strXMLA3;
                     int a3 = 0;
                     //Initialize graph element
                     strXMLA3 = "<graph caption='Error over iterations - simulated annealing agent 3' showValues='0' decimalPrecision='20' showgridbg='1' showNames='0'>";

                     //convert data to XML and append
                     for (a3 = 0; a3 < arrDataA3.length; a3++) {
                         //                 strXML = strXML + "<set name='" +arrData[i][0] + "' value='" + arrData[i][1] + "' color='" + colorHelper.getFCColor() + "'/>";
                         strXMLA3 = strXMLA3 + "<set name='" + arrDataA3[a3][0]
                                 + "' value='" + arrDataA3[a3][1]
                                 + "' color='AFD8F8'/>";

                     }
                     //Close graph element
                     strXMLA3 = strXMLA3 + "</graph>";

                     //Create the chart
             %> <jsp:include
								page="/FusionChartsFree/Code/JSP/Includes/FusionChartsHTMLRenderer.jsp"
								flush="true">
								<jsp:param name="chartSWF"
									value="FusionChartsFree/Charts/FCF_Line.swf" />
								<jsp:param name="strURL" value="" />
								<jsp:param name="strXML" value="<%=strXMLA3%>" />
								<jsp:param name="chartId" value="Error over iterations" />
								<jsp:param name="chartWidth" value="800" />
								<jsp:param name="chartHeight" value="350" />
								<jsp:param name="debugMode" value="false" />
							</jsp:include> <%
        }
        %>
						</td>

						<td>
							<%
        if(numberAgents >= 4)
        {
            %> <%
             //Agent 4 Graph
                 //Graph
                     String[][] arrDataA4 = new String[agent4Errors.size()][2];

                     //Epochs
                     
                     for (int epoch = 0; epoch < agent4Errors.size(); epoch++) {
                         arrDataA4[epoch][0] = String.valueOf(epoch + 1).toString();
                         arrDataA4[epoch][1] = String.valueOf(agent4Errors.get(epoch)).toString();
                     }

                     //FusionChartsHelper class for colors
                     //FusionChartsHelper colorHelper= new FusionChartsHelper();
                     //convert this data into XML. 
                     String strXMLA4;
                     int a4 = 0;
                     //Initialize graph element
                     strXMLA4 = "<graph caption='Error over iterations - simulated annealing agent 4' showValues='0' decimalPrecision='20' showgridbg='1' showNames='0'>";

                     //convert data to XML and append
                     for (a4 = 0; a4 < arrDataA4.length; a4++) {
                         //                 strXML = strXML + "<set name='" +arrData[i][0] + "' value='" + arrData[i][1] + "' color='" + colorHelper.getFCColor() + "'/>";
                         strXMLA4 = strXMLA4 + "<set name='" + arrDataA4[a4][0]
                                 + "' value='" + arrDataA4[a4][1]
                                 + "' color='AFD8F8'/>";

                     }
                     //Close graph element
                     strXMLA4 = strXMLA4 + "</graph>";

                     //Create the chart
             %> <jsp:include
								page="/FusionChartsFree/Code/JSP/Includes/FusionChartsHTMLRenderer.jsp"
								flush="true">
								<jsp:param name="chartSWF"
									value="FusionChartsFree/Charts/FCF_Line.swf" />
								<jsp:param name="strURL" value="" />
								<jsp:param name="strXML" value="<%=strXMLA4%>" />
								<jsp:param name="chartId" value="Error over iterations" />
								<jsp:param name="chartWidth" value="800" />
								<jsp:param name="chartHeight" value="350" />
								<jsp:param name="debugMode" value="false" />
							</jsp:include> <%
        }
        %>
						</td>

					</tr>

					<tr>

						<td>
							<%
        if(numberAgents == 5)
        {
            %> <%
             //Agent 5 Graph
                 //Graph
                     String[][] arrDataA5 = new String[agent5Errors.size()][2];

                     //Epochs
                     
                     for (int epoch = 0; epoch < agent5Errors.size(); epoch++) {
                         arrDataA5[epoch][0] = String.valueOf(epoch + 1).toString();
                         arrDataA5[epoch][1] = String.valueOf(agent5Errors.get(epoch)).toString();
                     }

                     //FusionChartsHelper class for colors
                     //FusionChartsHelper colorHelper= new FusionChartsHelper();
                     //convert this data into XML. 
                     String strXMLA5;
                     int a5 = 0;
                     //Initialize graph element
                     strXMLA5 = "<graph caption='Error over iterations - simulated annealing agent 5' showValues='0' decimalPrecision='20' showgridbg='1' showNames='0'>";

                     //convert data to XML and append
                     for (a5 = 0; a5 < arrDataA5.length; a5++) {
                         //                 strXML = strXML + "<set name='" +arrData[i][0] + "' value='" + arrData[i][1] + "' color='" + colorHelper.getFCColor() + "'/>";
                         strXMLA5 = strXMLA5 + "<set name='" + arrDataA5[a5][0]
                                 + "' value='" + arrDataA5[a5][1]
                                 + "' color='AFD8F8'/>";

                     }
                     //Close graph element
                     strXMLA5 = strXMLA5 + "</graph>";

                     //Create the chart
             %> <jsp:include
								page="/FusionChartsFree/Code/JSP/Includes/FusionChartsHTMLRenderer.jsp"
								flush="true">
								<jsp:param name="chartSWF"
									value="FusionChartsFree/Charts/FCF_Line.swf" />
								<jsp:param name="strURL" value="" />
								<jsp:param name="strXML" value="<%=strXMLA5%>" />
								<jsp:param name="chartId" value="Error over iterations" />
								<jsp:param name="chartWidth" value="800" />
								<jsp:param name="chartHeight" value="350" />
								<jsp:param name="debugMode" value="false" />
							</jsp:include> <%
        }
        %>
						</td>
					</tr>
				</table>
			</div>
		</div>

		<div class="AccordionPanel">
			<div class="AccordionPanelTab">Error over iterations - values
				per simulated annealing agent</div>
			<div class="AccordionPanelContent">
				<table border=1>
					<tr>
						<th><h3>
								simulated annealing agent 1 needed
								<%=agent1Epochs-1%><br /> iterations for attaining the allowed
								minimum error.
							</h3></th>

						<%if (numberAgents >= 2)
                               {%>
						<th><h3>
								simulated annealing agent 2 needed
								<%=agent2Epochs-1%><br /> iterations for attaining the allowed
								minimum error.
							</h3></th>
						<%} %>

						<%if (numberAgents >= 3)
                               {%>
						<th><h3>
								simulated annealing agent 3 needed
								<%=agent3Epochs-1%><br /> iterations for attaining the allowed
								minimum error.
							</h3></th>
						<%} %>

						<%if (numberAgents >= 4)
                               {%>
						<th><h3>
								simulated annealing agent 4 needed
								<%=agent4Epochs-1%><br /> iterations for attaining the allowed
								minimum error.
							</h3></th>
						<%} %>

						<%if (numberAgents == 5)
                               {%>
						<th><h3>
								simulated annealing agent 5 needed
								<%=agent5Epochs-1%><br /> iterations for attaining the allowed
								minimum error.
							</h3></th>
						<%} %>
					</tr>
					<tr>
						<td><br />
							<table border=5 cellpadding="2" cellspacing="2">
								<tr>
									<th align="left">Epoch
									<th align="left">Error
								</tr>
								<%
                        for (int epoch = 0; epoch < agent1Errors.size(); epoch++) {
                    %>
								<tr>
									<td><%=epoch + 1%></td>
									<td><%=agent1Errors.get(epoch)%></td>
								</tr>
								<%
                        }
                    %>
							</table> <br />
						</td>
						<%
        if(numberAgents >= 2)
        {
            %>
						<td><br />
							<table border=5 cellpadding="2" cellspacing="2">
								<tr>
									<th align="left">Epoch
									<th align="left">Error
								</tr>
								<%
                        for (int epoch = 0; epoch < agent2Errors.size(); epoch++) {
                    %>
								<tr>
									<td><%=epoch + 1%></td>
									<td><%=agent2Errors.get(epoch)%></td>
								</tr>
								<%
                        }
                    %>
							</table> <br />
						</td>
						<%} %>
						<%
        if(numberAgents >= 3)
        {
            %>
						<td><br />
							<table border=5 cellpadding="2" cellspacing="2">
								<tr>
									<th align="left">Epoch
									<th align="left">Error
								</tr>
								<%
                        for (int epoch = 0; epoch < agent3Errors.size(); epoch++) {
                    %>
								<tr>
									<td><%=epoch + 1%></td>
									<td><%=agent3Errors.get(epoch)%></td>
								</tr>
								<%
                        }
                    %>
							</table> <br />
						</td>
						<%} %>
						<%
        if(numberAgents >= 4)
        {
            %>
						<td><br />
							<table border=5 cellpadding="2" cellspacing="2">
								<tr>
									<th align="left">Epoch
									<th align="left">Error
								</tr>
								<%
                        for (int epoch = 0; epoch < agent4Errors.size(); epoch++) {
                    %>
								<tr>
									<td><%=epoch + 1%></td>
									<td><%=agent4Errors.get(epoch)%></td>
								</tr>
								<%
                        }
                    %>
							</table> <br />
						</td>
						<%} %>

						<%
        if(numberAgents == 5)
        {
            %>
						<td><br />
							<table border=5 cellpadding="2" cellspacing="2">
								<tr>
									<th align="left">Epoch
									<th align="left">Error
								</tr>
								<%
                        for (int epoch = 0; epoch < agent5Errors.size(); epoch++) {
                    %>
								<tr>
									<td><%=epoch + 1%></td>
									<td><%=agent5Errors.get(epoch)%></td>
								</tr>
								<%
                        }
                    %>
							</table> <br />
						</td>
						<%} %>
					</tr>
				</table>
			</div>
		</div>
		<div class="AccordionPanel">
			<div class="AccordionPanelTab">Neuron outputs per genetic agent</div>
			<div class="AccordionPanelContent">
				<h3>Neural simulated annealing committee agent 1:</h3>
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
				<h3>Neural simulated annealing committee agent 2:</h3>
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
				<h3>Neural simulated annealing committee agent 3:</h3>
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
				<h3>Neural simulated annealing committee agent 4:</h3>
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
				<h3>Neural simulated annealing committee agent 5:</h3>
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
		<h3>Neural simulated annealing committee has automatically
							been saved under SHOCID\committees</h3>

		<script type="text/javascript">
        var AccordionCommittee = new Spry.Widget.Accordion("AccordionCommittee");
    </script>
	
					</div>

</body>
</html>