<%@ taglib uri="/webwork" prefix="ww" %>

<html>
  <head>
    <title>Plexus Example Webapp Permission</title>
  </head>

  <body>
    <p>
      permissions list page
    </p>
    <ww:actionerror/>

    <ww:iterator id="permissions" value="permission">
      <ww:url id="permissionUrl" action="permission">
        <ww:param name="permissionId" value="permission.id"/>
      </ww:url>

      <ww:a href="%{permissionUrl}">${permission.name}</ww:a><br/>

    </ww:iterator>

    <p>
      <ww:url id="newPermissionUrl" action="permission">

      </ww:url>
      <ww:a href="%{newPermissionUrl}">new</ww:a><br/>
    </p>
    

  </body>
</html>