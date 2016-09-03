<%@ page import="java.sql.*"%>
<%
	if (request.getParameter("emp_id") != null) {
		String emp_id = request.getParameter("emp_id").toString();
		String data = "";
		Connection conn = null;
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "user_register";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "root";
		String password = "root";
		int sumcount = 0;
		Statement st;
		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url + dbName, userName,
					password);
			String query = "select * from employee_details where  eid='"
					+ emp_id + "'";
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				data = rs.getString(2) + " " + rs.getString(3) + ":"
						+ emp_id;
			}
			out.println(data.trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
	} else {
		response.sendRedirect("winopenradio.jsp");
	}
%>