<%@ taglib prefix="ww" uri="webwork" %>
<html>
<head><title>OpenSymphony WebWork2 Indexed Property Example</title></head>
<body>
<div align="center">
<p><h2>Indexed Property Example</h2></p>

<form action="indexedProperties.action" method="POST">
<table>
<tr>
<th colspan="2">Emails - a Map of name -> email </th>
</tr>
<ww:iterator value="emails">
<%-- this needs to be done by hand so we can add the extra single quotes --%>
<tr><td align="right"><ww:property value="key"/> :</td><td><input type="text" size="50" name="emails['<ww:property value="key"/>']" value="<ww:property value="value"/>"/></td></tr>
</ww:iterator>
<th colspan="2">URLs - a List accessed by index</th>
<ww:iterator value="urls" status="urlstatus">
<ww:textfield size="50" label="" name="'urls[' + (#urlstatus.count - 1) + ']'" value="top"/>
</ww:iterator>
<tr><td colspan="2" align="center"><input type="Submit"/></td></tr>
</table>
</form>
</div>
</body>
</html>