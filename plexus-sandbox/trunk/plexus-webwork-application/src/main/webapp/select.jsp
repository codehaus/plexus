<%@ taglib prefix="ww" uri="webwork" %>
<html>
<head><title>Select example</title></head>

<body>
<p>Example of the Select tag:</p>
<p>Selected Value: <ww:property value="selected"/></p>
<form action="select.action">
<ww:select label="'Select'" name="'selected'" list="selectMap" listKey="key" listValue="value"></ww:select>
<input type="Submit" value="Submit"/>
</form>
</body>
</html>