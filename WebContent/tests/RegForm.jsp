<?xml version="1.0"?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="1.2">
	<!-- RegForm.jsp
     Copyright (c) 2002 by Dr. Herong Yang
-->
	<jsp:directive.page contentType="text/html" />
	<jsp:declaration><![CDATA[private String getItem(String queryString, String key) {
		String value = null;
		if (queryString != null) {
			int i = queryString.indexOf(key);
			if (i > -1) {
				i = i + key.length();
				int j = queryString.indexOf("&", i);
				if (j > -1) {
					value = queryString.substring(i, j);
				} else {
					value = queryString.substring(i);
				}
				if (value.startsWith("=")) {
					value = value.replaceFirst("=", "");
				}
			}
		}
		return value;
	}]]></jsp:declaration>
	<jsp:scriptlet><![CDATA[String lastUser = (String) application.getAttribute("name");
			if (lastUser == null) {
				lastUser = "Nobody";
				application.setAttribute("name", lastUser);
			}
			String queryString = request.getQueryString();
			String submit = getItem(queryString, "submit");
			if (submit != null && submit.equals("Submit")) {
				// Collecting the input data
				session.setAttribute("name", getItem(queryString, "name"));
				session.setAttribute("pass", getItem(queryString, "pass"));
				application.setAttribute("name", getItem(queryString, "name"));
				response.sendRedirect("RegDone.jsp?color="
						+ getItem(queryString, "color"));
			} else {
				// Presenting the registration form
				out.print("<html><body>");
				out.print("<b>Registration Form</b>:<br/>");
				out.print("<form action=RegForm.jsp method=get>");
				out.print("Login Name:");
				out.print("<input type=text size=16 name=name><br/>");
				out.print("Password:");
				out.print("<input type=text size=16 name=pass><br/>");
				out.print("Favor Color:");
				out.print("<input type=text size=16 name=color><br/>");
				out.print("<input type=submit name=submit value=Submit></br>");
				out.print("</form>");
				out.print("Your session ID is " + session.getId() + "<br/>");
				out.print("Last user on the server: " + lastUser + "<br/>");
				out.print("</body></html>");
			}]]></jsp:scriptlet>
</jsp:root>
