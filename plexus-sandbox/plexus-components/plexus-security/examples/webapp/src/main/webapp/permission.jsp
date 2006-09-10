<%@ taglib uri="/webwork" prefix="ww" %>

<html>
  <head>
    <title>Plexus Example Webapp Permission</title>
  </head>

  <body>
    <p>
      Permission Modification Page
    </p>
    <ww:actionerror/>
    <ww:form action="savePermission" method="post">
     <ww:hidden name="permissionId"/>

     <ww:textfield label="Name" name="name"/> <br/>
     <ww:textfield label="Description" name="description"/> <br/>
     <ww:select label="Operation" name="operationId" list="operations" listKey="id" listValue="name" value="operation.id"/> <br/>
     <ww:select label="Resource" name="resourceId" list="resources" listKey="id" listValue="identifier" value="resource.id"/>

      <center><ww:submit/></center>
    </ww:form>

  </body>
</html>