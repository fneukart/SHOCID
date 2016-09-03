<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Jsp Frameset</title>
</head>
<frameset rows="10%,*">
<frame src="frame1.jsp" name="frame1"scrolling="no">
<frameset cols="20%,*">
<frame src="frame2.jsp" name="frame2">
<frame src="frame3.jsp" name="frame3">
</frameset></frameset
><noframes></noframes>
</html>