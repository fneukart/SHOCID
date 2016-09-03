<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="shocid.hoann.StartJSP"%>
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
<title>Deep Belief Network Processing</title>
<link href="SpryAssets/SpryAccordion.css" rel="stylesheet"
	type="text/css">
</head>
<body>

	<%
		String committee_or_single = null;
		committee_or_single = "s";
	%>
	<jsp:forward page="beliefStartSingle.jsp" />

</body>
</html>