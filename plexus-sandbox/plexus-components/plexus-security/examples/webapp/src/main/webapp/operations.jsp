<%@ taglib uri="/webwork" prefix="ww" %>

<html>
  <head>
    <title>Plexus Example Webapp Operation</title>
  </head>

  <body>
  <ww:url id="rolesUrl" action="roles"/>
  <ww:url id="permissionsUrl" action="permissions"/>
  <ww:url id="operationsUrl" action="operations"/>
  <ww:url id="resourcesUrl" action="resources"/>

  <p><ww:a href="%{rolesUrl}">Roles</ww:a>|<ww:a href="%{permissionsUrl}">Permissions</ww:a>|<ww:a href="%{operationsUrl}">Operations</ww:a>|<ww:a href="%{resourcesUrl}">Resources</ww:a> </p>

    <p>
      Operations list page
    </p>
    <ww:actionerror/>

    <ww:iterator id="operation" value="operations">
      <ww:url id="operationUrl" action="operation">
        <ww:param name="operationId" value="${operation.id}"/>
      </ww:url>

      <ww:a href="%{operationUrl}">${operation.name}</ww:a><br/>
    </ww:iterator>

    <p>
      <ww:url id="newOperationUrl" action="operation"/>

      <ww:a href="%{newOperationUrl}">new</ww:a><br/>
    </p>
    

  </body>
</html>