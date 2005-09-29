<%@ taglib uri="webwork" prefix="webwork" %>

<link rel="stylesheet" type="text/css" href="../../template/standard/styles.css" title="Style">

<html>
<head>
   <title>Welcome!</title>
</head>
<body>

<font face="Arial">
Welcome <webwork:property value="name"/>! <webwork:if test="autoLogin==true">(You were automatically logged in)</webwork:if>
<P>
You have been here <webwork:action name="'events.logincounter'"><webwork:property value="count"/></webwork:action> times before.
<P>
Either <a href="../events.logout.action">log out</a> or <a href="../">return</a> to main test page.
</font>

</body>
</html>
