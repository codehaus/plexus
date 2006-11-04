<%@ taglib uri="webwork" prefix="webwork" %>
<webwork:action name="'MonthList'">

<TABLE BORDER=1 CELLSPACING=0 CELLPADDING=2 WIDTH=100% HEIGHT=100%>
<TR>
<TH>&nbsp;</TH>
<TH COLSPAN=7>Day</TH>
</TR>

<TR>
<TH>Week</TH>

<webwork:bean name="'webwork.util.Counter'" id="day">
  <webwork:param name="'first'" value="firstDay"/>
  <webwork:param name="'interval'" value="'86400000'"/>

<webwork:bean name="'webwork.util.DateFormatter'">
  <webwork:param name="'parseFormat'">d</webwork:param>
  <webwork:param name="'format'">E</webwork:param>

<webwork:param name="'time'" value="@day/next"/>
<TH><webwork:property value="formattedDate"/></TH>

<webwork:param name="'time'" value="@day/next"/>
<TH><webwork:property value="formattedDate"/></TH>

<webwork:param name="'time'" value="@day/next"/>
<TH><webwork:property value="formattedDate"/></TH>

<webwork:param name="'time'" value="@day/next"/>
<TH><webwork:property value="formattedDate"/></TH>

<webwork:param name="'time'" value="@day/next"/>
<TH><webwork:property value="formattedDate"/></TH>

<webwork:param name="'time'" value="@day/next"/>
<TH><webwork:property value="formattedDate"/></TH>

<webwork:param name="'time'" value="@day/next"/>
<TH><webwork:property value="formattedDate"/></TH>
</webwork:bean>
</webwork:bean>
</TR>

<webwork:bean name="'webwork.util.Counter'" id="week">
  <webwork:param name="'first'" value="1"/>
  <webwork:param name="'current'" value="firstWeek"/>
  <webwork:param name="'last'" value="52"/>
  <webwork:param name="'wrap'" value="true"/>
</webwork:bean>

<webwork:iterator value="weeks">
<TR>
<TH><webwork:property value="@week/next"/></TH>
<webwork:iterator>
<webwork:if test=".=='0'">
  <TD>&nbsp;</TD>
</webwork:if>
<webwork:else>
  <TD <webwork:if test="$day==.">BGCOLOR="yellow"</webwork:if>
      <webwork:elseIf test="$month==thisMonth && today==.">BGCOLOR=#EEAAAA</webwork:elseIf>>
  <A HREF="<webwork:url><webwork:param name="'day'" value="."/></webwork:url>"><webwork:property/></A>&nbsp;
  </TD>
</webwork:else>
</webwork:iterator>
</TR>
</webwork:iterator>
</TABLE>

</webwork:action>
