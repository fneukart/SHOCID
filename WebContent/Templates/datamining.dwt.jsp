<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<!-- TemplateBeginEditable name="doctitle" -->
<title>Unbenanntes Dokument</title>
<!-- TemplateEndEditable -->
<!-- TemplateBeginEditable name="head" -->
<!-- TemplateEndEditable -->
<style type="text/css">
<!--
body {
	font: 100%/1.4 Verdana, Arial, Helvetica, sans-serif;
	background: #42413C;
	margin: 0;
	padding: 0;
	color: #000;
}

/* ~~ Element-/Tag-Selektoren ~~ */
ul, ol, dl { /* Aufgrund von Abweichungen zwischen verschiedenen Browsern empfiehlt es sich, die Auffüllung und den Rand in Listen auf 0 einzustellen. Zu Konsistenzzwecken können Sie die gewünschten Werte entweder hier oder in den enthaltenen Listenelementen (LI, DT, DD) eingeben. Beachten Sie, dass die hier eingegebenen Werte hierarchisch auf die .nav-Liste angewendet werden, sofern Sie keinen spezifischeren Selektor festlegen. */
	padding: 0;
	margin: 0;
}
h1, h2, h3, h4, h5, h6, p {
	margin-top: 0;	 /* Durch Verschieben des oberen Rands wird das Problem behoben, dass Ränder aus dem zugehörigen div-Tag geraten können. Der übrig gebliebene untere Rand hält ihn getrennt von allen folgenden Elementen. */
	padding-right: 15px;
	padding-left: 15px; /* Durch Hinzufügen der Auffüllung zu den Seiten der Elemente innerhalb der div-Tags anstelle der div-Tags selbst entfallen jegliche Box-Modell-Berechnungen. Alternativ kann auch ein verschachteltes div-Tag mit seitlicher Auffüllung verwendet werden. */
}
a img { /* Dieser Selektor entfernt den standardmäßigen blauen Rahmen, der in einigen Browsern um ein Bild angezeigt wird, wenn es von einem Hyperlink umschlossen ist. */
	border: none;
}

/* ~~ Die Reihenfolge der Stildefinitionen für die Hyperlinks der Site, einschließlich der Gruppe der Selektoren zum Erzeugen des Hover-Effekts, muss erhalten bleiben. ~~ */
a:link {
	color: #42413C;
	text-decoration: underline; /* Sofern Ihre Hyperlinks nicht besonders hervorgehoben werden sollen, empfiehlt es sich, zur schnellen visuellen Erkennung Unterstreichungen zu verwenden. */
}
a:visited {
	color: #6E6C64;
	text-decoration: underline;
}
a:hover, a:active, a:focus { /* Durch diese Gruppe von Selektoren wird bei Verwendung der Tastatur der gleiche Hover-Effekt wie beim Verwenden der Maus erzielt. */
	text-decoration: none;
}

/* ~~ Dieser Container mit fester Breite umschließt die anderen div-Tags. ~~ */
.container {
	width: 960px;
	background: #FFF;
	margin: 0 auto; /* Der mit der Breite gekoppelte automatische Wert an den Seiten zentriert das Layout. */
}

/* ~~ Für die Kopfzeile wird keine Breite angegeben. Sie erstreckt sich über die gesamte Breite des Layouts. Sie enthält einen Bild-Platzhalter, der durch Ihr eigenes, mit Hyperlink versehenes Logo ersetzt werden sollte. ~~ */
.header {
	background: #ADB96E;
}

/* ~~ Dies sind die Spalten für das Layout. ~~ 

1) Eine Auffüllung wird nur oben und/oder unten in den div-Tags positioniert. Die Elemente innerhalb dieser div-Tags verfügen über eine seitliche Auffüllung. Dadurch müssen Sie keine Box-Modell-Berechnungen durchführen. Zu beachten: Wenn Sie dem div-Tag eine seitliche Auffüllung oder einen Rahmen hinzufügen, werden diese zu der festgelegten Breite addiert und ergeben die *gesamte* Breite. Sie können auch die Auffüllung für das Element im div-Tag entfernen und ein zweites div-Tag ohne Breite und mit der gewünschten Auffüllung im ersten div-Tag einfügen. Sie können auch die Auffüllung für das Element im div-Tag entfernen und ein zweites div-Tag ohne Breite und mit der gewünschten Auffüllung im ersten div-Tag einfügen.

2) Für die Spalten wurde kein Rand angegeben, da es sich um fließende Spalten handelt. Wenn Sie unbedingt einen Rand hinzufügen möchten, platzieren Sie ihn nicht auf der Seite in Flussrichtung (z. B. ein rechter Rand in einem div-Tag, das so festgelegt ist, dass das Element nach rechts fließt). In vielen Fällen kann stattdessen eine Auffüllung verwendet werden. Bei div-Tags, bei denen diese Regel gebrochen werden muss, sollten Sie der Regel des div-Tags eine display:inline-Deklaration hinzufügen, um das Problem zu umgehen, dass in einigen Versionen von Internet Explorer der Rand doppelt angezeigt wird.

3) Da Klassen in einem Dokument mehrmals verwendet werden können (und zudem auf ein Element mehrere Klassen angewendet werden können), wurden den Spalten Klassennamen statt IDs zugewiesen. Beispielsweise können zwei div-Tags für eine Seitenleiste bei Bedarf gestapelt werden. Diese div-Tags können problemlos in IDs geändert werden, solange Sie sie nur einmal pro Dokument verwenden.

4) Wenn Sie die Navigation auf der rechten statt auf der linken Seite platzieren möchten, lassen Sie diese Spalten einfach in die andere Richtung fließen (alle nach rechts anstatt alle nach links). Die Spalten werden dann in umgekehrter Reihenfolge dargestellt. Die div-Tags in der HTML-Quelle müssen nicht verschoben werden.

*/
.sidebar1 {
	float: left;
	width: 200px;
	background: #EADCAE;
	padding-bottom: 10px;
}
.content {
	padding: 10px 0;
	width: 760px;
	float: left;
}

/* ~~ Dieser gruppierte Selektor gibt die Listen im .content-Bereich an. ~~ */
.content ul, .content ol { 
	padding: 0 15px 15px 40px; /* Diese Auffüllung setzt die rechte Auffüllung in der obigen Regel für Überschriften und Absätze fort. Die Auffüllung wurde unten für den Abstand zwischen anderen Elementen in den Listen und links für den Einzug platziert. Sie können die Werte nach Bedarf ändern. */
}

/* ~~ Stile für die Navigationslisten (können entfernt werden, wenn Sie ein vordefiniertes Ausklappmenü wie Spry verwenden) ~~ */
ul.nav {
	list-style: none; /* Hiermit wird die Listenmarkierung entfernt. */
	border-top: 1px solid #666; /* Hiermit wird der obere Rand für die Hyperlinks erstellt. Alle anderen werden mit einem unteren Rand im LI-Element platziert. */
	margin-bottom: 15px; /* Hiermit wird der Abstand zwischen den Navigationselementen und den Inhalten unten erstellt. */
}
ul.nav li {
	border-bottom: 1px solid #666; /* Hiermit wird die Trennung der Schaltflächen erstellt. */
}
ul.nav a, ul.nav a:visited { /* Durch Gruppieren dieser Selektoren wird sichergestellt, dass die Hyperlinks auch nach dem Aufrufen die Form einer Schaltfläche beibehalten. */
	padding: 5px 5px 5px 15px;
	display: block; /* Hiermit werden die Blockeigenschaften für den Hyperlink angegeben, sodass das gesamte umschließende LI-Element aufgefüllt wird. Hiermit wird angegeben, dass der gesamte Bereich auf einen Mausklick reagiert. */
	width: 180px;  /*Mit dieser Breite reagiert die gesamte Schaltfläche in IE6 auf Mausklicks. Kann entfernt werden, wenn IE6 nicht unterstützt werden muss. Berechnen Sie die exakte Breite durch Subtrahieren der Auffüllung für diesen Hyperlink von der Breite des Containers für die Randleiste. */
	text-decoration: none;
	background: #C6D580;
}
ul.nav a:hover, ul.nav a:active, ul.nav a:focus { /* Hiermit wird der Hintergrund und die Textfarbe bei der Navigation mit der Maus und der Tastatur geändert. */
	background: #ADB96E;
	color: #FFF;
}

/* ~~ Fußzeile ~~ */
.footer {
	padding: 10px 0;
	background: #CCC49F;
	position: relative;/* Hiermit erhält IE6 die Eigenschaft hasLayout, damit die clear-Anweisung korrekt ausgeführt wird. */
	clear: both; /* Diese clear-Eigenschaft bewirkt, dass .container das Ende der fließenden Spalten erkennt und ihren Inhalt aufnimmt. */
}

/* ~~ Verschiedene float/clear-Klassen ~~ */
.fltrt {  /* Mit dieser Klasse können Sie ein Element auf der Seite nach rechts fließen lassen. Das fließende Element muss vor dem Element stehen, neben dem es auf der Seite erscheinen soll. */
	float: right;
	margin-left: 8px;
}
.fltlft { /* Mit dieser Klasse können Sie ein Element auf der Seite nach links fließen lassen. Das fließende Element muss vor dem Element stehen, neben dem es auf der Seite erscheinen soll. */
	float: left;
	margin-right: 8px;
}
.clearfloat { /* Diese Klasse kann in einem <br />-Tag oder leeren div-Tag als letztes Element nach dem letzten fließenden div-Tag (im #container) platziert werden, wenn #footer entfernt oder aus dem #container herausgenommen wird. */
	clear:both;
	height:0;
	font-size: 1px;
	line-height: 0px;
}
-->
</style>
<script src="../SpryAssets/SpryMenuBar.js" type="text/javascript"></script>
<link href="../SpryAssets/SpryMenuBarHorizontal.css" rel="stylesheet" type="text/css" />
</head>

<body>

<div class="container">
  <div class="header"><a href="#"><img src="file:///C|/Users/Florian Neukart/workspace/SHOCID/Pictures/Logo.gif" alt="Hier Logo einfügen" name="Insert_logo" width="129" height="106" id="Insert_logo" style="background: #8090AB; display:block;" /></a> 
    <!-- end .header --></div>
  <div class="sidebar1">
    <ul class="nav">
      <li><a href="#">Hyperlink 1</a></li>
      <li><a href="#">Hyperlink 2</a></li>
      <li><a href="#">Hyperlink 3</a></li>
      <li><a href="#">Hyperlink 4</a></li>
    </ul>
    <p> Die oben stehenden Hyperlinks stellen eine allgemeine Navigationsstruktur dar, bei der eine ungeordnete Liste mit CSS formatiert wird. Verwenden Sie diese Navigationsstruktur als Ausgangspunkt und ändern Sie die Eigenschaften, um Ihre Website nach Ihren Vorstellungen zu gestalten. Wenn Sie Ausklappmenüs verwenden möchten, erstellen Sie ein eigenes Menü auf Grundlage eines Spry-Menüs, eines Menü-Widgets aus Adobe Exchange oder einer Vielzahl anderer JavaScript- oder CSS-Lösungen.</p>
    <p>Wenn sich die Navigationsstruktur am oberen Rand befinden soll, verschieben Sie ul.nav an den oberen Rand der Seite und erstellen Sie die Formatierung neu.</p>
    <!-- end .sidebar1 --></div>
  <ul id="MenuBar1" class="MenuBarHorizontal">
    <li><a class="MenuBarItemSubmenu" href="#">ANN</a>
      <ul>
        <li><a href="#">Pattern Recognition</a></li>
        <li><a href="#">Pattern Recognition (recurrent) </a></li>
        <li><a href="#">Element 1.3</a></li>
      </ul>
    </li>
    <li><a href="#">SOFM</a></li>
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
  <div class="content">
    <h1>&nbsp;</h1>
  <!-- end .content --></div>
  <div class="footer">
    <p>Diese .footer-Regel enthält die Deklaration position:relative. So erhält Internet Explorer 6 die Eigenschaft hasLayout für .footer, damit die clear-Anweisung korrekt ausgeführt wird. Kann entfernt werden, wenn IE6 nicht unterstützt werden muss. </p>
    <!-- end .footer --></div>
  <!-- end .container --></div>
<script type="text/javascript">
var MenuBar1 = new Spry.Widget.MenuBar("MenuBar1", {imgDown:"../SpryAssets/SpryMenuBarDownHover.gif", imgRight:"../SpryAssets/SpryMenuBarRightHover.gif"});
</script>
</body>
</html>
