<%@ taglib uri="webwork" prefix="webwork" %>
<%@ taglib uri="webwork" prefix="ui" %>
<HTML>

<HEAD>
<STYLE type="text/css">
TD { font-family: Verdana,Arial; font-size: 8pt }
TH { font-family: Verdana,Arial; font-size: 8pt; font-weight:bold; font-style: italic }
A { text-decoration: none}
A:HOVER { text-decoration: underline}
</STYLE>
</HEAD>

<BODY>

<webwork:bean name="'webwork.util.Timer'" id="timer"/>

<TABLE BORDER=1>
<TR>

<TD ROWSPAN=2>
Test
</TD>

<TD>
<webwork:include page="monthselector.jsp"/>

</TD>
</TR>

<TR>
<TD>

<webwork:include page="monthview.jsp"/>

</TD>
</TR>
</TABLE>

Time:<webwork:property value="@timer/time"/>ms

</BODY>
</HTML>

