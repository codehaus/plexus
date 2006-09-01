<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri="plexus-security-system" prefix="pss" %>
<html>
  <head>
    <title>Plexus Example Webapp Login</title>
  </head>

  <body>
    <ww:actionerror/>
    <ww:form action="login">
      <table>
        <tr>
          <td>username</td>
          <td><ww:textfield name="username"/></td>
        </tr>
        <tr>
          <td>password</td>
          <td><ww:password name="password"/></td>
        </tr>
         <tr>
          <td>email</td>
          <td><ww:password name="email"/></td>
        </tr>
      </table>
      <center><ww:submit/></center>
    </ww:form>
  </body>
</html>