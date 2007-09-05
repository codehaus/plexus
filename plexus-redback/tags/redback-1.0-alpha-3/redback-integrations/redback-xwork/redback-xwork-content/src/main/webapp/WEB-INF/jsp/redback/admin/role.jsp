<%@ taglib uri="/webwork" prefix="ww" %>

<html>
<ww:i18n name="org.codehaus.plexus.redback.xwork.default">
<head>
  <title><ww:text name="role.page.title"/></title>
</head>

<body>
<p>
  <ww:text name="role.section.title"/>

</p>
<ww:actionerror/>
<ww:form action="saveRole" method="post">
  <ww:hidden name="roleName"/>

  <ww:textfield label="%{getText('name')}" name="name"/> <br/>
  <ww:textfield label="%{getText('description')}" name="description"/> <br/>
  <ww:checkbox label="%{getText('role.assignable')}" name="assignable"/><br/>
  <br/>
  <ww:text name="role.currently.assigned.permissions"/><br/>
  <ww:iterator id="permission" value="permissions">
    <ww:url id="removeAssignedPermissionUrl" action="removeAssignedPermission">
      <ww:param name="roleName" value="roleName"/>
      <ww:param name="removePermissionName">${permission.name}</ww:param>
    </ww:url>
    ${permission.name} | <ww:a href="%{removeAssignedPermissionUrl}"><ww:text name="remove"/></ww:a><br/>
  </ww:iterator>
  <br/>
  <ww:select label="%{getText('role.add.new.permission')}" name="assignPermissionName" list="assignablePermissions"  listKey="name" listValue="name" emptyOption="true"/><br/>
  <br/>
  <ww:text name="role.currently.assigned.roles"/><br/>
  <ww:iterator id="arole" value="childRoles.roles">
    <ww:url id="removeAssignedRoleUrl" action="removeAssignedRole">
      <ww:param name="roleName" value="roleName"/>
      <ww:param name="removeRoleName" value="${arole.name}"/>
    </ww:url>
    ${arole.name} | <ww:a href="%{removeAssignedRoleUrl}"><ww:text name="remove"/></ww:a><br/>
  </ww:iterator>
  <br/>
  <ww:select label="%{getText('role.add.sub.role')}" name="assignedRoleName" list="assignableRoles" listKey="name" listValue="name" emptyOption="true"/><br/>

  <p>
    <ww:submit/>
  </p>
</ww:form>

</body>
</ww:i18n>
</html>