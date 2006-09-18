<%--
  ~ Copyright 2005-2006 The Apache Software Foundation.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~      http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<%@ taglib prefix="ww" uri="/webwork"%>
<%@ taglib prefix="pss" uri="plexusSecuritySystem"%>

<html>
<head>
  <title>[Admin] User Edit</title>
  <ww:head/>
</head>

<body>

<%@ include file="/WEB-INF/jsp/pss/include/formValidationResults.jspf" %>

<h2>[Admin] User Edit</h2>

<ww:form action="useredit!submit" namespace="/security" theme="xhtml"
         id="userEditForm" method="post" name="useredit" cssClass="security userEdit">
  <%@ include file="/WEB-INF/jsp/pss/include/userCredentials.jspf"%>
  <ww:checkbox label="Locked User" name="user.locked" />
  <ww:checkbox label="Change Password Next Login" name="user.passwordChangeRequired" />
  <ww:hidden   label="Username"    name="username" />
  <ww:submit value="Update User" />
</ww:form>

<h2>Currently Assigned Roles</h2>

<ul>
<c:choose>
  <c:when test="${!empty edituser.assignedRoles}">
    <ww:iterator id="role" value="edituser.assignedRoles">
      <li><em>${role}</em></li>
    </ww:iterator>
  </c:when>
  <c:otherwise>
    <li><em>No Roles Assigned (yet)</em></li>
  </c:otherwise>
</c:choose>
</ul>

<%--<pss:ifAnyAuthorized permissions="grant-roles,remove-roles">--%>
  <h2>Role Management</h2>

<%--  <pss:ifAuthorized permission="grant-roles">--%>
    <h3>Grant</h3>
    <ww:action name="userRolesToGrant" executeResult="true" >
      <ww:param name="principal">${user.username}</ww:param>
    </ww:action>

<%--  </pss:ifAuthorized> --%>

 <%--:ifAuthorized permission="remove-roles"> --%>
    <h3>Remove</h3>
    <ww:action name="userRolesToRemove" executeResult="true" >
      <ww:param name="principal">${user.username}</ww:param>
    </ww:action>

<%--pss:ifAuthorized>--%>
<%--</pss:ifAnyAuthorized>--%>

</body>

</html>
