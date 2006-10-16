<%@ taglib prefix="ww" uri="webwork" %>

<p>Example of the Iterator example:</p>

<table border="0" cellspacing="0" cellpadding="1">
  <tr>
    <th>Days of the week</th>
  </tr>

  <ww:iterator value="days">
    <tr>
      <td><ww:property/></td>
    </tr>
  </ww:iterator>
</table>

<p>Example of the Iterator example using the status attribute and a value from the ActionContext</p>

<ww:bean name="'com.opensymphony.webwork.example.IteratorExample'" id="it">
  <ww:param name="'day'" value="'foo'"/>
  <ww:param name="'day'" value="'bar'"/>
</ww:bean>

 <table border="0" cellspacing="0" cellpadding="1">
  <tr>
    <th>Days of the week</th>
  </tr>

  <ww:iterator value="#it.days" status="rowstatus">
    <tr>
      <ww:if test="#rowstatus.odd == true">
        <td style="background: grey"><ww:property/></td>
      </ww:if>
      <ww:else>
        <td><ww:property/></td>
      </ww:else>
    </tr>
  </ww:iterator>
</table>