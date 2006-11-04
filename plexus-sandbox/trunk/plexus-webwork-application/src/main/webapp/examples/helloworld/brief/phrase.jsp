<%@ taglib uri="webwork" prefix="webwork" %>

<webwork:include page="header.jsp" />

<H1>HelloWorld example</H1>

<webwork:if test="hasErrorMessages == true">
Could not create greeting:<BR>
<webwork:iterator value="errorMessages">
<webwork:property/><BR>
</webwork:iterator>
</webwork:if>

<FORM ACTION="helloworld.helloworld.action" METHOD="POST">
Greeting:<INPUT TYPE=text NAME="phrase" value="<webwork:property value="phrase"/>">
Name:<INPUT TYPE=text NAME="name" value="<webwork:property value="name"/>">
<INPUT TYPE=submit VALUE=" Show " >
</FORM>

<webwork:include page="footer.jsp" />
