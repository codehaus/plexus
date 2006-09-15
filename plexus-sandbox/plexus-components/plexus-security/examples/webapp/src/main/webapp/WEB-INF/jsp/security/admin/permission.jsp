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
     <ww:hidden name="permissionName"/>

     <ww:textfield label="Name" name="name"/> <br/>
     <ww:textfield label="Description" name="description"/> <br/>
     <ww:select label="Operation" name="operationName" list="operations" listKey="name" listValue="name" value="operation.name" emptyOption="true"/> <br/>
      <br/>
     <ww:select label="Resource" name="resourceIdentifier" list="resources" listKey="identifier" listValue="identifier" value="resource.identifier" emptyOption="true"/><br/>
      or <br/>
     <ww:checkbox label="Is GlobalResource?" name="globalResource"/><br/>

      <center><ww:submit/></center>
    </ww:form>

  </body>
</html>