<%@ taglib uri="/webwork" prefix="ww" %>

<html>
  <head>
    <title>Plexus Example Webapp Permission</title>
  </head>

  <body>
  <ww:url id="rolesUrl" action="roles"/>
  <ww:url id="permissionsUrl" action="permissions"/>
  <ww:url id="operationsUrl" action="operations"/>
  <ww:url id="resourcesUrl" action="resources"/>

  <p><ww:a href="%{rolesUrl}">Roles</ww:a>|<ww:a href="%{permissionsUrl}">Permissions</ww:a>|<ww:a href="%{operationsUrl}">Operations</ww:a>|<ww:a href="%{resourcesUrl}">Resources</ww:a> </p>

    <p>
      Permissions list page
    </p>
    <ww:actionerror/>

    <ww:iterator id="permission" value="permissions">
      <ww:url id="permissionUrl" action="permission">
        <ww:param name="permissionId" value="${permission.id}"/>
      </ww:url>

      <ww:a href="%{permissionUrl}">${permission.name}</ww:a><br/>
    </ww:iterator>

    <p>
      <ww:url id="newPermissionUrl" action="permission"/>

      <ww:a href="%{newPermissionUrl}">new</ww:a><br/>
    </p>
    

  </body>
</html>