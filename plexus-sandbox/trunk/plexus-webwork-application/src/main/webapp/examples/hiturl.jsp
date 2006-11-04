<%@ taglib uri="webwork" prefix="webwork" %>
<%@ taglib uri="webwork" prefix="ui" %>

<link rel="stylesheet" type="text/css" href="../template/standard/styles.css" title="Style">

<html>
<head>
<title>Hit URL</title>
</head>
<body>

<h1>Hit URL test</h1>

This page allow you to hit a URL a number of times, and gather statistics regarding the time it takes to retrieve it.
This is a good way to determine the performance of a particular page. Note that if you are using a HotSpot server, or similar,
you will see that the it takes a while for the JVM to "warm up", so make a few runs until the timings become stable.

<center>
<form action="<webwork:url value="hiturl.action"/>" method="GET">

<table width="350" border="0" cellpadding="3" cellspacing="0">

     <ui:combobox label="'URL'" size="50" name="'url'" list="history"/>

     <ui:textfield label="'Count'" size="4" name="'count'"/>

     <ui:textfield label="'Thread count'" size="3" name="'threadCount'"/>

	<tr><td colspan="2" align="center">
		<input type=submit value="Hit it!">
	</td></tr>

</table>

</center>

</form>

<webwork:if test="$url">

<HR>
Size: <webwork:property value="size"/><BR>
Total time: <webwork:text name="'timeformat'" value0="total"/>
<BR>

Average time: <webwork:text name="'timeformat'" value0="averageTime"/><BR>
Mean time: <webwork:text name="'timeformat'" value0="meanTime"/><BR>
Fastest time: <webwork:text name="'timeformat'" value0="fastestTime"/><BR>
Slowest time: <webwork:text name="'timeformat'" value0="slowestTime"/><BR>
Nr of errors: <webwork:property value="nrOfErrors"/><BR>
Nr of connect errors: <webwork:property value="nrOfConnectErrors"/><BR>
<P>
All times:<BR>
<OL>
<webwork:iterator value="times">
<LI><webwork:text name="'timeformat'" value0="."/>
</webwork:iterator>
</OL>

</webwork:if>

</body>
</html>
