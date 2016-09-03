<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="shocid.hoann.StartJSP"%>
<%@ page import="shocid.hoann.HOANNCommitteeJSP"%>
<%@ page import="shocid.ffann.training.FFANNGeneticAlgorithmJSP"%>
<%@ page import="shocid.ffann.committee.training.GeneticAlgorithmCJSP"%>
<%@ page import="shocid.saveFile.ApplyFileDialog"%>
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
<title>Cortical Data Processing</title>
<link href="SpryAssets/SpryAccordion.css" rel="stylesheet"
	type="text/css">
</head>
<body>

    <%
        String committee_or_single = null;
        if (request.getParameter("agents").equals("1")) {
            committee_or_single = "s";
        } else if (request.getParameter("agents").equals("0")) {
            committee_or_single = "c";
        } else if (request.getParameter("agents").equals("2")) {
            committee_or_single = "ec";
        } else if (request.getParameter("agents").equals("3")) {
            committee_or_single = "es";
        } else {
            System.out.println("Invalid input.");
            System.exit(1);
        }
        if (committee_or_single.equals("s")) {
    %>
    <jsp:forward page="corticalStartSingle.jsp" />
    <%
        } else if (committee_or_single.equals("es")) {
    %>
    <jsp:forward page="neukartStartAdaptiveSingle.jsp" />
    <%
        } else if (committee_or_single.equals("c")) {
    %>
    <jsp:forward page="corticalStartCommittee.jsp" />
    <%
        } else if (committee_or_single.equals("ec")) {
    %>
    <jsp:forward page="neukartStartAdaptiveHybridCommittee.jsp" />
    <%
        }
    %>

</body>
</html>