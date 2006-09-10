<%@ taglib uri="/webwork" prefix="ww" %>

<html>
  <head>
    <title>Plexus Example Webapp Role</title>
  </head>

  <body>
    <ww:url id="rolesUrl" action="roles"/>
    <ww:url id="permissionsUrl" action="permissions"/>
    <ww:url id="operationsUrl" action="operations"/>
    <ww:url id="resourcesUrl" action="resources"/>

    <p><ww:a href="%{rolesUrl}">Roles</ww:a>|<ww:a href="%{permissionsUrl}">Permissions</ww:a>|<ww:a href="%{operationsUrl}">Operations</ww:a>|<ww:a href="%{resourcesUrl}">Resources</ww:a> </p>


    <p>
      Roles list page
    </p>

    <ww:actionerror/>

    <ww:iterator id="role" value="roles">
      <ww:url id="roleUrl" action="role">
        <ww:param name="roleId" value="${role.id}"/>
      </ww:url>

      <ww:a href="%{roleUrl}">${role.name}</ww:a><br/>
    </ww:iterator>

    <p>
      <ww:url id="newRoleUrl" action="role"/>

      <ww:a href="%{newRoleUrl}">new</ww:a><br/>
    </p>
    

  </body>
</html>