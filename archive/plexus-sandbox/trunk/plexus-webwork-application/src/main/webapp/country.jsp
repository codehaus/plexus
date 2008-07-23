<%@ taglib uri="webwork" prefix="ww" %>
<%@ taglib uri="benchmark" prefix="benchmark" %>

<%-- this is to make our special controlfooter not close the row yet --%>
<ww:set name="extraCol" value="'true'" scope="webwork" />

<ww:form action="">

<tr>
    <th colspan="2" align="center">Tag</th>
    <th>Time</th>
</tr>

<benchmark:duration output="true" >
<ww:select label="'multiple select test'" name="'select1'" value="countries[0][1]" list="countries" listKey="top[1]" listValue="top[0]" multiple="'true'" size="'5'"/>
</benchmark:duration> ms</td></tr>

</ww:form>
