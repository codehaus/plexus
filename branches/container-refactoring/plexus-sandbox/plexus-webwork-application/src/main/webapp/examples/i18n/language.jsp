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

<webwork:action name="'i18n.LanguageList'">
   <ui:radio label="'Language'" name="'language'" list="languages"/>
</webwork:action>

<p>
<input type=submit value=Continue >
</form>

</body>
</html>
