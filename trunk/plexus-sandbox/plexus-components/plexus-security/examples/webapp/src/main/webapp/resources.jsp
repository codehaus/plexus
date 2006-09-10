<%@ taglib uri="/webwork" prefix="ww" %>

<html>
  <head>
    <title>Plexus Example Webapp Resource</title>
  </head>

  <body>
  <ww:url id="rolesUrl" action="roles"/>
  <ww:url id="permissionsUrl" action="permissions"/>
  <ww:url id="operationsUrl" action="operations"/>
  <ww:url id="resourcesUrl" action="resources"/>

  <p><ww:a href="%{rolesUrl}">Roles</ww:a>|<ww:a href="%{permissionsUrl}">Permissions</ww:a>|<ww:a href="%{operationsUrl}">Operations</ww:a>|<ww:a href="%{resourcesUrl}">Resources</ww:a> </p>

    <p>
      Resources list page
    </p>
    <ww:actionerror/>

    <ww:iterator id="resource" value="resources">
      <ww:url id="resourceUrl" action="resource">
        <ww:param name="resourceId" value="${resource.id}"/>
      </ww:url>
      <ww:a href="%{resourceUrl}">${resource.identifier}</ww:a><br/>
    </ww:iterator>

    <p>
      <ww:url id="newResourceUrl" action="resource"/>

      <ww:a href="%{newResourceUrl}">new</ww:a><br/>
    </p>
    

  </body>
</html>