<%@ taglib uri="webwork" prefix="webwork" %>
<%@ taglib uri="webwork" prefix="ui" %>

<link rel="stylesheet" type="text/css" href="../../template/standard/styles.css" title="Style">
<html>
<head>
<title>You are registered</title>
</head>

<body>
<center>

<h1>Registration is valid</h1>
<table>
<tr>
   <td>
      Name:
   </td>
   <td>
      <webwork:property value="firstname"/>&nbsp<webwork:property value="lastname"/><br>
   </td>
</tr>
<tr>
   <td>
      Social security number:
   </td>
   <td>
      <webwork:property value="fnr"/> <br>
   </td>
</tr>
<tr>
   <td>
      Email:
   </td>
   <td>
      <webwork:property value="email"/> <br>
   </td>
</tr>
<tr>
   <td>
      Username:
   </td>
   <td>
      <webwork:property value="username"/> <br>
   </td>
</tr>
<tr>
   <td>
      Password (remove this):
   </td>
   <td>
      <webwork:property value="password"/> <br>
   </td>
</tr>
<tr>
   <td>
      Adress information:
   </td>
   <td>
      <webwork:property value="adress"/><br>
      <webwork:property value="postcode"/>&nbsp<webwork:property value="city"/><br>
   </td>
</tr>
<tr>
   <td>
      <webwork:action name="'GenderMap'" id="genders"/>
   </td>
   <td>
      <ui:radio label="'Gender'" name="'male'" list="@genders/genders"/>
   </td>
</tr>
</table>

<a href="index.jsp">Registration page</a>

</center>
</body>
</html>