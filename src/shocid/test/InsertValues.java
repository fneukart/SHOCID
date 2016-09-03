package shocid.test;

import java.sql.*;

public class InsertValues
{
	public static void main(String[] args)
	{
		System.out.println("Inserting values in Mysql database table!");
		Connection con = null;
		String url = "jdbc:mysql://localhost:3306/";
		String db = "shocid";
		String driver = "com.mysql.jdbc.Driver";
		try{
			Class.forName(driver);
			con = DriverManager.getConnection(url+db,"shocid","shocid");
//			con =
//			       DriverManager.getConnection("jdbc:mysql://localhost:3306/shocid?" +
//			                                   "user=root&password=@uakedrei3");
			try{
				Statement st = con.createStatement();
				//int val = st.executeUpdate("INSERT employee VALUES("+13+","+"'Aman'"+")");
				int val = st.executeUpdate("INSERT into test (testfield) VALUES('value3')");
				System.out.println("1 row affected");
			}
			catch (SQLException s){
				System.out.println("SQL statement is not executed!");
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
} 
