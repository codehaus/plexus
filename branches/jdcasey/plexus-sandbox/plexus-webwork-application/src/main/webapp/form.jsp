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
Status: <ww:property value="status"/><br><p>
<b>Test Form with valid token</b><br>
<ww:form name="'myForm'" action="'formTest.action'" method="POST">
<ww:token name="'myToken'"/><br>
<table>
<ww:textfield label="'Foo'" name="'foo'" value="foo"/><br>
</table>
<input type="submit" value="Test With Token"/>
</ww:form>
<b>Test Form without valid token</b><br>
<ww:form name="'myForm'" action="'formTest.action'" method="POST">
<table>
<%--
    @todo something weird happens on resin where if this textfield has the same label as the previous textfield,
    the the label for this textfield comes up as empty.
--%>
<ww:textfield label="'No Foo'" name="'foo'" value="foo"/><br>
</table>
<input type="submit" value="Test Without Token"/>
</ww:form>
</body>