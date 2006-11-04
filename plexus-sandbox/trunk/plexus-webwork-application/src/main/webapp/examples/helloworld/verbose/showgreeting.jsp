<%@ taglib uri="webwork" prefix="webwork" %>

<TABLE BORDER="1" BGCOLOR=lightyellow>
<HEADER>Here is a greeting for you</HEADER>

<TR><TD><webwork:property value="greeting"/></TD></TR>
<TR><TD><A HREF="<webwork:url><webwork:param name="'helloworld'" value="'phrase'"/></webwork:url>">Try again</A></TD></TR>
</TABLE>
