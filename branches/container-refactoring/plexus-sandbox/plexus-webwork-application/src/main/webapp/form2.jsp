<%@ taglib prefix="ww" uri="webwork" %>
<html>
<head><title>OpenSymphony WebWork2 Form Example</title></head>
<body>

<ww:if test="errorMessages != null">
<p>
<font color="red">
<b>ERRORS:</b><br>
<ul>
<ww:iterator value="errorMessages">
<li><ww:property/></li>
</ww:iterator>
</ul>
</font>
</p>
</ww:if>

<ol>
<li>Enter some data below, then execute the action.</li>
<li>It will wait 2 seconds and then show you a success page.</li>
<li>Then click back and change the value for foo and submit.</li>
<li>Notice that the original value of foo is used and there is no 2 second delay.
    That is because the action isn't being executed again.</li>
</ol>

<ww:form name="'myForm'" action="'formTest2.action'" method="'POST'">
    <ww:token name="'myToken'"/><br>
    <ww:textfield label="'Foo'" name="'foo'"/><br>
    <ww:submit value="'Test With Token'" />
</ww:form>

</body>
</html>