<%@ page language="java"%>
<%@ page import="java.lang.*"%>
<%@ page import="java.sql.*"%>

<HTML>
<head>

</head>
<BODY BGCOLOR="LIGHTYELLOW">
	<form NAME="form" METHOD="GET" ACTION="process.jsp">
		<br> <br>

		<H3>
			<P ALIGN="CENTER">
				<FONT SIZE=6> EMPLOYEE DETAILS </FONT>
			</P>
		</H3>
		<BR> <BR>
		<TABLE CELLSPACING=5 CELLPADDING=5 BGCOLOR="LIGHTBLUE" COLSPAN=2
			ROWSPAN=2 ALIGN="CENTER">
			<TR>
				<TD><FONT SIZE=5> Enter Employee ID 
				</TD>
				<TD><INPUT TYPE="TEXT" NAME="id" id="emp_id">
				</TD>
				<TD><INPUT TYPE="button" NAME="s1" VALUE="View Record"
					onClick="window.open('windowopen.jsp','mywindow','width=500, height=350,toolbar=no,resizable=yes,menubar=yes')">
					</FONT>
				</TD>
			</TR>
			<TR>
				<TD><FONT SIZE=5> Enter Employee Name 
				</TD>
				<TD><INPUT TYPE="TEXT" NAME="name" id="emp_name"> </FONT>
				</TD>
			</TR>


			</TR>
			</FONT>
			<%
				if (session.getAttribute("empcode") != null
						&& session.getAttribute("empname") != null) {
			%>
			<script language="javascript">
document.getElementById('id').value=
<%=session.getAttribute("empcode").toString()%>
document.getElementById('name').value='<%=session.getAttribute("empname").toString()%>'
</script>
			<%
				session.removeAttribute("empcode");
					session.removeAttribute("empname");
				}
			%>
</form>
</BODY>
</HTML>
