<%@ taglib uri="webwork" prefix="webwork" %>
<%@ taglib uri="webwork" prefix="ui" %>

<webwork:include page="header.jsp" />

<webwork:iterator value="errorMessages">
<webwork:property/><BR>
</webwork:iterator>

<FORM ACTION="<webwork:url value="helloworld.login.action"/>" METHOD="POST">
<TABLE BORDER="1" BGCOLOR=lightyellow>
<TR><TH COLSPAN=2><webwork:text name="'login'"/></TH></TR>

   <ui:textfield label="getText('name')" name="'name'"/>

   <ui:password label="getText('password')" name="'password'" size="10" maxlength="15"/>

<TR><TH COLSPAN=2><INPUT TYPE=submit VALUE="<webwork:text name="'login'"/>" ></TH></TR>
</TABLE>
</FORM>

<webwork:text name="'hint'"/>

<webwork:include value="'footer.jsp'" />
