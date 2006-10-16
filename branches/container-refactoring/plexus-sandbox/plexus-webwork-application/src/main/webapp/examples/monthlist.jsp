<%@ taglib uri="webwork" prefix="webwork" %>
<%@ page buffer="17kb" %>

<HTML>
<BODY>

<H1>This year</H1>
<webwork:bean name="'webwork.util.Timer'" id="timer"/>

<webwork:bean name="'webwork.util.Counter'">
  <webwork:param name="'first'" value="0"/>
  <webwork:param name="'last'" value="11"/>

<webwork:iterator id="monthnr">
  <webwork:action name="'monthlist'" id="month">
    <webwork:param name="'month'" value="#attr.monthnr"/>
  </webwork:action>
  <webwork:push value="#month">

<TABLE BORDER=1 CELLSPACING=0 CELLPADDING=2>
<TR>
<TH>&nbsp;</TH>
<TH COLSPAN=7>
<webwork:text name="'monthName'" value0="#attr.monthnr"/>
</TH>
</TR>

<TR>
<TH>Week</TH>

<webwork:bean name="'webwork.util.Counter'">
  <webwork:param name="'last'" value="7"/>

  <webwork:iterator>
  <TH><webwork:text name="'dayName'" value0="top"/></TH>
  </webwork:iterator>
</webwork:bean>
</TR>

<webwork:bean name="'webwork.util.Counter'" id="week">
  <webwork:param name="'first'" value="1"/>
  <webwork:param name="'current'" value="#attr.month.firstWeek"/>
  <webwork:param name="'last'" value="52"/>
  <webwork:param name="'wrap'" value="true"/>
</webwork:bean>

<webwork:iterator value="#attr.month.weeks">
<TR>
<TH><webwork:property value="#attr.week.next"/></TH>
<webwork:iterator>
<TD <webwork:if test="#attr.monthnr == #attr.month.thisMonth && top == #attr.month.today">BGCOLOR=#EEAAAA</webwork:if>> <webwork:text name="'day'" value1="top"/>  &nbsp;</TD>
</webwork:iterator>
</TR>
</webwork:iterator>
</TABLE>
<HR SIZE=1>

  </webwork:push>
</webwork:iterator>

</webwork:bean>

Time:<webwork:property value="#attr.timer.time"/>ms

</BODY>
</HTML>

