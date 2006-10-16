<%@ taglib uri="webwork" prefix="webwork" %>
<%@ taglib uri="webwork" prefix="ui" %>

<webwork:include page="header.jsp" />

<H1>Login</H1>

<webwork:iterator value="errorMessages">
<webwork:property/><BR>
</webwork:iterator>

<FORM ACTION="helloworld.login.action" METHOD="POST">
<ui:textfield label="getText('name')" name="'name'"/>

<ui:password label="getText('password')" name="'password'" size="10" maxlength="15"/>

<INPUT TYPE=submit VALUE=" Login " >
</FORM>

<webwork:text name="'hint'"/>

<webwork:include page="footer.jsp" />
