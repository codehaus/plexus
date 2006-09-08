<%@ taglib uri="/webwork" prefix="ww" %>
<html>
<head>
<title>Welcome New User</title>
</head>
<body>
    <ww:actionerror/>
    <ww:form action="login">
      <table>
        <tr>
          <td><label for="username">User Name:</label></td>
          <td><ww:textfield id="username" name="username"/></td>
        </tr>
        <tr>
          <td><label for="fullname">Full Name:</label></td>
          <td><ww:textfield id="fullname" name="fullName"></ww:textfield>
        </tr>
        <tr>
          <td><label for="email">Email Address:</label></td>
          <td><ww:password id="email" name="email"/></td>
        </tr>
        <tr>
          <td><label for="password">Password:</label></td>
          <td><ww:password id="password" name="password"/></td>
        </tr>
        <tr>
          <td><label for="passwordConfirm">Password (confirm):</label></td>
          <td><ww:password id="passwordConfirm" name="passwordConfirm"/></td>
        </tr>
      </table>
      <center><ww:submit/></center>
    </ww:form>
</body>
</html>