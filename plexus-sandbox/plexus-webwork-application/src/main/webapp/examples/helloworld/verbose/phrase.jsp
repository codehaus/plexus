<%@ taglib uri="webwork" prefix="webwork" %>
<%@ taglib uri="webwork" prefix="ui" %>

<FORM ACTION="<webwork:url><webwork:param name="$paneName" value="'greeting'"/></webwork:url>" METHOD="POST">
<TABLE BORDER="1" BGCOLOR=lightyellow>
<HEADER>Enter greeting phrase and your name (HINT: try "Hello" and "World")</HEADER>

	<ui:textfield label="'Greeting'" name="'phrase'"/>

   <webwork:action name="'helloworld.loginstatus'" id="status"/>
      <webwork:if test="@status/name=='Guest'">
      	<ui:textfield label="'Name'" name="'name'"/>
      </webwork:if>
      <webwork:else>
         <input type="hidden" name="name" value="<webwork:property value="@status/name"/>">
         <tr><td><i>Name:</i></td><td><webwork:property value="@status/name"/></td></tr>
      </webwork:else>

<TR><TH COLSPAN=2><INPUT TYPE=submit VALUE=" Show "></TH></TR>
</TABLE>
</FORM>
