<%@ taglib uri="webwork" prefix="webwork" %>
<%@ taglib uri="webwork" prefix="ui" %>

<link rel="stylesheet" type="text/css" href="../../template/standard/styles.css" title="Style">

<html>
<head>
   <title>Login Screen</title>
</head>
<body>

<font face="Arial">

<form method="POST" action="events.login.action">
   <table border="0" width="400">
      <tr>
         <td width="100%" align="right" colspan="2"><div align="left">
            <p><strong>James Bond's <br><em>Super-Secret Login Screen</em></strong></td>
      </tr>

<webwork:iterator value="errorMessages">
      <tr>
         <th width="100%" colspan="2"><webwork:property/></th>
      </tr>
</webwork:iterator>

   <ui:textfield label="'Name'" name="'name'"/>
   <ui:password label="'Password'" name="'password'" size="10" maxlength="15"/>
<tr>
<td width="22%"></td>
<td width="78%">
<input type="submit" value="Login">
</td>
</tr>
</table>
</form>
</font>

</body>
</html>
