<html>
<head>
<title>JDOM tests</title>
</head>
<body>

<%@ taglib uri="webwork" prefix="webwork" %>
<%@ taglib uri="webwork" prefix="iterator" %>

<H1>Testing XML display using JDOM</H1>

<webwork:bean name="'webwork.util.Timer'" id="timer"/>

  <webwork:property value="doc/rootElement/name"/><br>

  Names:<br>
  <webwork:iterator value="doc/rootElement/children('author')">
    <webwork:property value="child('firstname')/text"/> <webwork:property value="child('surname')/text"/> (Karma=<webwork:property value="attributeValue('karma')"/>)<br>
  </webwork:iterator>

Time:<webwork:property value="@timer/time"/>ms<br>

Total time:<webwork:property value="@timer/total"/>ms<br>

</body>
</html>
