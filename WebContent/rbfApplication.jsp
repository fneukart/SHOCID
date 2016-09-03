<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="shocid.hoann.StartJSP"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Radial Basis Function with RPROP Classification</title>
<style type="text/css">
body,td,th {
	font-family: "Lucida Sans Unicode", "Lucida Grande", sans-serif;
}
/* abc{}; */
/* abc2{height: 0; visiblity: hidden}; */
</style>

<script>
	window.onload = showNormalization();
	window.onload = showOutputFile();
	window.onload = showNumberAgents();
</script>

<script language=javascript>
	function showNormalization() {
		var showNormalization = document.getElementById('normalization').value;
		if (showNormalization == 1) {
			document.getElementById('normType').style.visibility = "visible";
			document.getElementById('normalizationOutputFileHeaderTrue').style.visibility = "visible";
			document.getElementById('normalizationOutputFile').style.visibility = "visible";
			document.getElementById('normalizationType').value = 1;
		} else {
			document.getElementById('normType').style.visibility = "hidden";
			document.getElementById('normalizationOutputFileHeaderTrue').style.visibility = "hidden";
			//document.getElementById('normalizationOutputFileHeaderTrue').style.className = "abc2";
			document.getElementById('normalizationOutputFile').style.visibility = "hidden";
			//document.getElementById('normalizationOutputFile').style.className = "abc2";
		}
	}

	function showOutputFile() {
		var showOutputFile = document.getElementById('normalizationType').value;
		if (showOutputFile == 1) {
			document.getElementById('normalizationOutputFile').style.visibility = "visible";
		} else if (showOutputFile == 0) {
			document.getElementById('normalizationOutputFile').style.visibility = "hidden";
		} else {

		}
	}
	
</script>
</head>

<body>
	<h3>Radial Basis Function with RPROP data processing</h3>
	<p>Shall the task be processed with a single agent or a democratic
		committee of agents?</p>

	<form id="form1" name="form1" method="post"
		action="rbfApplicationMiddler.jsp">

		<table>
			<tr>
				<td><label for="agents"></label> <select name="agents" size="1"
					id="agents" onchange="showNumberAgents()">
						<option value=0>Democratic Committee</option>
						<option value=1>Single Agent</option>
				</select>
				</td>
			</tr>
		</table>

		<p>Shall the file be normalized?</p>
		<span id="form1"> <select name="normalization"
			id="normalization" onchange="showNormalization()">
				<option value=1>yes</option>
				<option value=0>no</option>
		</select> </span>
		
		<div id="normalizationOutputFileHeaderTrue">
			<h3>Data source / target</h3>
		</div>
		
		<div id="normalizationOutputFile">
			<p>Enter the name of the normalization output file (will be stored in the input
				file directory):</p>

			<label for="output"></label> <input type="text" name="output"
				id="output" />
		</div>

		<div id="normType" class="abc">
			<p>Which type of normalization shall be applied?</p>
			<span id="normType"> <select name="normalizationType"
				id="normalizationType" onchange="showOutputFile()">
					<option value=1>CSV to CSV</option>
					<option value=0>CSV to Neural Data Set</option>
			</select> </span>
		</div>

		<p>Select the input file to be mined:</p>
		<label for="input"></label> <input type="file" name="input" id="input"
			size="50" maxlength="100000" accept="text/*" />
			
		<p>Select the agent info file referencing the trained solution:</p>
		<label for="agentInfo"></label> <input type="file" name="agentInfo" id="agentInfo"
			size="50" maxlength="100000" accept="text/*" />

			 <span id="form1"> <input
			name="trainingMethod" type="hidden" id="trainingMethod" value="rad" />
		</span> <span id="form1"> <input name="networkType" type="hidden"
			id="networkType" value="1" /> </span> <input type="submit" name="execute"
			id="execute" value="Execute" />
	</form>

</body>
</html>