<%@ page import="java.util.Map,
                 com.opensymphony.xwork.util.OgnlValueStack"%>
<%@ taglib uri="webwork" prefix="ww" %>
<%@ taglib uri="c" prefix="c" %>

This url is: <ww:url/> <br/>
A package-level i18n key is: <ww:text name="'packageLevelKey'"/>

<hr>

Escaped:     <ww:property value="'this &amp; that'" escape="true" /><br/>
Not escaped: <ww:property value="'this &amp; that'" escape="false" /><br/>

Success!
<hr>

<ww:property value="counter.count"/>

<%--
this sets the variable "counter" in the webwork context
to hold the object returned by the VS find query of "conter"
--%>
<ww:set name="counterBLAH" value="counter" scope="webwork" />

<%--
We can then reference the above object using # (like @ in the
old EL). This time the scope is page and the variable is
"counter", so now we can use JSTL. The last two ww:set
calls could have, of course, been a simple call ;)
--%>
<ww:set name="counter" value="#counterBLAH" scope="page" />
(JSTL also says: <c:out value="${counter.count}"/>)

<%--
aka:
<ww:push value="counter">
    <ww:property value="count"/>
</ww:push>

aka:
<ww:push value="counter">
    <ww:property value="counter.count"/>
</ww:push>

aka:
<ww:push value="counter">
    <ww:property value="[1].counter.count"/>
</ww:push>

aka:
<ww:push value="counter">
    <ww:push value="count">
        <ww:property value="peek()"/>
    </ww:push>
</ww:push>

aka:
<ww:push value="counter">
    <ww:property value="getCount()"/>
</ww:push>

<%
    long x = System.currentTimeMillis();
%>

<ww:textfield label="This is a test" name="counter.count"/>
<%
    long y = System.currentTimeMillis();
    System.out.println(y - x);
%>
--%>

<hr>
Here is an action inside an action. This should make the counter increase twice as fast:<br/>
<ww:action name="'VelocityCounter'" id="vc">
 <ww:param name="'foo'" value="'BAR'"/>
</ww:action>

<ww:push value="#vc">
<pre>
 counter.count (old) == <ww:property value="[1].counter.count" />
 counter.count (new) == <ww:property value="counter.count" />
 foo is also: <ww:property value="'foo'"/>
</pre>
</ww:push>

<hr/>
<p>
    The following is a test of the push tag.  We call &lt;ww:property/&gt; 4 times.  One the second call, we've pushed
    the word 'counter' on the stack.  What we expect to see is an A, B, B, A pattern where A is the toString() of the
    action, and b is the word counter.
</p>
<table border="1">
<tr>
    <td>Stack (before push)</td>
    <td><ww:property/></td>
</tr>
<ww:push value="'counter'">
<tr>
    <td>Stack (inside push)</td>
    <td><ww:property/></td>
</tr>
<tr>
    <td>Top (inside push)</td>
    <td><ww:property value="top"/></td>
</tr>
</ww:push>
<tr>
    <td>Stack (after push)</td>
    <td><ww:property/></td>
</tr>
</table>


<br/>
