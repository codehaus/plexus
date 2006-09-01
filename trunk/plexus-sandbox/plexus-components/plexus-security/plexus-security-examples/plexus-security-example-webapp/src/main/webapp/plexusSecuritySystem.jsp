<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri="plexus-security-system" prefix="pss" %>
<html>
    <head>
        <title>Plexus Security System</title>
    </head>
    <body>
      <p>
       Info
      </p>
         <table>
           <tr>
             <td>Authentication</td>
             <td>${pageScope.authentication}</td>
           </tr>
           <tr>
             <td>Authorization</td>
             <td>${pageScope.authorization}</td>
           </tr>
           <tr>
             <td>User Management</td>
             <td>${pageScope.userManagement}</td>
           </tr>
         </table>
    </body>
</html>