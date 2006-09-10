<%@ taglib uri="/webwork" prefix="ww" %>

<html>
<head>
  <title>Plexus Example Webapp Role</title>
</head>

<body>
<p>
  Role Modification Page

</p>
<ww:actionerror/>
<ww:form action="saveRole" method="post">
  <ww:hidden name="roleId"/>

  <ww:textfield label="name" name="name"/> <br/>
  <ww:textfield label="description" name="description"/> <br/>
  <ww:checkbox label="assignable?" name="assignable"/><br/>
  <ww:select label="add new permission" name="permissionId" list="permissions" listKey="id" listValue="name"/> <br/>
  <ww:select label="add sub role" name="childRoleId" list="roles" listKey="id" listValue="name"/> <br/>


  <p>
    <ww:submit/>
  </p>
</ww:form>

</body>
</html>