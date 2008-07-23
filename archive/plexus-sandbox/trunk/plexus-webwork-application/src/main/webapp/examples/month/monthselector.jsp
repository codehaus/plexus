<%@ taglib uri="webwork" prefix="webwork" %>
<webwork:action name="'MonthSelector'">

<webwork:bean name="'webwork.util.Counter'" id="monthcounter">
  <webwork:param name="'first'" value="0"/>
  <webwork:param name="'last'" value="11"/>
</webwork:bean>

<TABLE BORDER=1 CELLSPACING=0 CELLPADDING=2 WIDTH=100% HEIGHT=100%>

<TR><TH COLSPAN="6">Month</TH></TR>

<TR>

<webwork:iterator value="@monthcounter" status="'monthstatus'">
  <TD ALIGN=center <webwork:if test=".==month">BGCOLOR="yellow"</webwork:if>>
  <A HREF="<webwork:url><webwork:param name="'month'" value="@monthstatus/index"/></webwork:url>"><webwork:property value="@monthstatus/count"/></A>
  </TD>
  <webwork:if test="@monthstatus/count==6">
    </TR><TR>
  </webwork:if>
</webwork:iterator>
</TR>
</TABLE>

</webwork:action>
