<%@ taglib uri="/webwork" prefix="ww" %>

<html>
  <head>
    <title>Plexus Example Webapp Role</title>
  </head>

  <body>
    <p>
      Roles list page
    </p>
    <ww:actionerror/>

    <ww:iterator id="roles" value="role">
      <ww:url id="roleUrl" action="role">
        <ww:param name="roleId" value="id"/>
      </ww:url>

      <ww:a href="%{roleUrl}">${role.name}</ww:a><br/>
    </ww:iterator>

    <p>
      <ww:url id="newRoleUrl" action="role"/>

      <ww:a href="%{newRoleUrl}">new</ww:a><br/>
    </p>
    

  </body>
</html>