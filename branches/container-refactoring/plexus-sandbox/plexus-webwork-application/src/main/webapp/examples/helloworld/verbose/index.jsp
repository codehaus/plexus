<%@ taglib uri="webwork" prefix="webwork" %>

<link rel="stylesheet" type="text/css" href="../../../template/standard/styles.css" title="Style">

<HTML>
<BODY>

<CENTER>
<TABLE BORDER="1" BGCOLOR=lightyellow WIDTH=100% HEIGHT=100%>
<TR><TH COLSPAN=2>WebWork Test Application</TH></TR>
<TR><TD WIDTH=200 VALIGN=top>
<webwork:include value="'helloworld.loginstatus.action'" />
<webwork:include value="'../selectview.jsp'" />
</TD>
<TD ALIGN=center VALIGN=center BGCOLOR=white WIDTH=100%>

<webwork:include value="'cardpane.action'">
   <webwork:param name="'paneName'" value="'helloworld'"/>
   <webwork:param name="'defaultName'" value="'phrase'"/>
</webwork:include>

</TD></TR>
<TR><TH COLSPAN=2>
HelloWorld example
</TD></TR>
</TABLE>

</BODY>
</HTML>





