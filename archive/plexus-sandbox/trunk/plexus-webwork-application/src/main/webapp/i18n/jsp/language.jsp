<%@ taglib uri="webwork" prefix="webwork" %>
<%@ taglib uri="webwork" prefix="ui" %>

<html>
<head>
<title>
<webwork:text name="'main.title'"/>
</title>
</head>
<body bgcolor="#<webwork:text name="'main.bgcolor'"/>">
<font face="Arial,Times New Roman,Times" size=+3>
<webwork:text name="'main.title'"/>
</font>
<hr>
<p>
Please select a language:
<form action="i18n.Language.action" method="post">

<webwork:action name="'i18n.LanguageList'" id="languages"/>

<webwork:push value="#languages">
<ui:radio label="'Language'" name="'language'" list="languages" listKey="key" listValue="value"/><br>
</webwork:push>

<p>
<input type=submit value=Continue >
</form>

</body>
</html>
