<%@ page import="java.sql.*"%>
<html>
<head>
<script type="text/javascript">

///// Code chages 
var imageURL = "redar.jpg";
function changeImage(img_id) {
var imgs=document.getElementsByTagName('img'); 
for(var i=1;i<=imgs.length;i++) {
var imgid="myImage"+i;
if(imgid==img_id){
document.getElementById(img_id).src="greenar.gif";
}
else{
document.getElementById(imgid).src=imageURL;
}
}
}

///////////////
function trim(stringToTrim) {
return stringToTrim.replace(/^\s+|\s+$/g,"");
}

function validate()
{
var emp_value ="";
var count=0;
var imgs=document.getElementsByTagName('img'); 
for(var i=1;i<=imgs.length;i++) {
var imgid="myImage"+i;
var imgurl = document.getElementById(imgid).src;
var imgar = imgurl.split("/");
if(imgar[4]=="greenar.gif")
{
count++;
}
}
if(count==0)
{
alert("Please Select Employee Id");
return false;
}
return true;
} 
function showEmp(){ 
if(validate()){
var imgs=document.getElementsByTagName('img'); 
for(var i=1;i<=imgs.length;i++) {
var imgid="myImage"+i;
var emp_id = "eid"+i;
var imgurl = document.getElementById(imgid).src;
var imgar = imgurl.split("/");
if(imgar[4]=="greenar.gif"){
var emp_value = document.getElementById(emp_id).value;
}
}
xmlHttp=GetXmlHttpObject();
if (xmlHttp==null){
alert ("Browser does not support HTTP Request")
return
}
var url="getuser.jsp";
url=url+"?emp_id="+emp_value;

xmlHttp.onreadystatechange=stateChanged;
xmlHttp.open("GET",url,true);
xmlHttp.send(null);
}


}

function stateChanged() 
{ 
if (xmlHttp.readyState==4 || xmlHttp.readyState=="complete")
{ 
var showdata = xmlHttp.responseText;
var strar = trim(showdata).split(":");
if(strar.length>0)
{
window.opener.location.reload();
window.location.reload(); 
window.close(); 
opener.document.getElementById("emp_id").value=strar[1];
opener.document.getElementById("emp_name").value=strar[0];
window.close();
}
} 
}

function GetXmlHttpObject()
{
var xmlHttp=null;
try
{
// Firefox, Opera 8.0+, Safari
xmlHttp=new XMLHttpRequest();
}
catch (e)
{
//Internet Explorer
try
{
xmlHttp=new ActiveXObject("Msxml2.XMLHTTP");
}
catch (e)
{
xmlHttp=new ActiveXObject("Microsoft.XMLHTTP");
}
}
return xmlHttp;

}
</script>

</head>
<body>
	<form name="employee">
		<br> <br>
		<table border="1" width="300px" align="center" bgcolor="#CDFFFF">
			<tr>
				<td align="center" colspan=3><b>Select Employee Id</b>
				</td>
			</tr>

			<%
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
					String query = "select * from employee_details";

					st = conn.createStatement();
					ResultSet rs = st.executeQuery(query);
			%>

			<%
				int count = 0;
					while (rs.next()) {
						count++;
			%>

			<tr>
				<input type="hidden" value="<%=rs.getString(1)%>" id="eid<%=count%>">
				<td align="right"><img src="redar.jpg" width="25px"
					height="25px" name="myImage" onclick="changeImage(this.id);"
					id="myImage<%=count%>" border="0">
				</td>
				<td><%=rs.getString(1)%></td>
				<td width="50%"><%=rs.getString(2)%></td>
			</tr>

			<%
				}
			%>

			<%
				} catch (Exception e) {
					e.printStackTrace();
				}
			%>

			<tr>
				<td align="center" Colspan=3><input type="button"
					value="Select" onclick="javascript:showEmp();" />
				</td>
			</tr>
		</table>
	</form>
</body>
</html>