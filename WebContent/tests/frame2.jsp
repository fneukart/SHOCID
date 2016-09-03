<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<script src="../SpryAssets/SpryMenuBar.js" type="text/javascript"></script>
<link href="../SpryAssets/SpryMenuBarVertical.css" rel="stylesheet" type="text/css" />
<body bgcolor="pink">
<h3>Task Selection</h3>
<ul id="MenuBar1" class="MenuBarVertical">
  <li><a class="MenuBarItemSubmenu" href="#">Data Mining</a>
    <ul>
      <li><a href="genetic.jsp" title="Genetic Learining" target="frame3">Genetic Learning</a></li>
      <li><a href="#">Back Propagation</a></li>
      <li><a href="#">Manhatten Update</a></li>
      <li><a href="#">Resilient Propagation</a></li>
      <li><a href="#">Levenberg Marquardt</a></li>
      
    </ul>
  </li>
  <li><a href="#">Predictive Analysis</a></li>
  <li><a class="MenuBarItemSubmenu" href="#">Element 3</a>
    <ul>
      <li><a class="MenuBarItemSubmenu" href="#">Element 3.1</a>
        <ul>
          <li><a href="#">Element 3.1.1</a></li>
          <li><a href="#">Element 3.1.2</a></li>
        </ul>
      </li>
      <li><a href="#">Element 3.2</a></li>
      <li><a href="#">Element 3.3</a></li>
    </ul>
  </li>
  <li><a href="#">Element 4</a></li>
</ul>
<script type="text/javascript">
var MenuBar1 = new Spry.Widget.MenuBar("MenuBar1", {imgRight:"../SpryAssets/SpryMenuBarRightHover.gif"});
</script>
</body>
</html>