<%@ taglib uri="webwork" prefix="webwork" %>
<%@ taglib uri="webwork" prefix="ui" %>

<center>
<form action="<webwork:url/>" method="GET">

<table border="0" cellpadding="3" cellspacing="0">

   <webwork:action name="'rss.sources'" id="sources"/>
   <ui:select label="'Source'" name="'url'" list="@sources/items" listKey="'childText(\'link\')'" listValue="'childText(\'title\')'"/>

   <ui:checkbox label="'Bulleted list'" name="'bullet'" value="$bullet" fieldValue="'true'"/>
   <tr><td colspan="2" align="center">
   <input type=submit value="View channel">
   </td></tr>
</table>

</center>
</form>

<webwork:if test="document">
   <webwork:include value="'rss.jsp'"/>
</webwork:if>
