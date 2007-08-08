<%--
  ~ Copyright 2005-2006 The Codehaus.
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
<%@ taglib prefix="redback" uri="http://plexus.codehaus.org/redback/taglib-1.0"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<ww:i18n name="org.codehaus.plexus.redback.xwork.default">
<head>
  <title><ww:text name="user.edit.page.title"/></title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/redback/include/formValidationResults.jsp" %>

<h2><ww:text name="user.edit.section.title"/></h2>

<redback:ifAuthorized permission="user-management-user-edit" resource="${user.username}">
  <ww:form action="useredit" namespace="/security" theme="xhtml"
         id="userEditForm" method="post" name="useredit" cssClass="security userEdit">
    <%@ include file="/WEB-INF/jsp/redback/include/userCredentials.jsp" %>
    <ww:checkbox label="%{getText('user.edit.locked.user')}" name="user.locked" />
    <ww:checkbox label="%{getText('user.edit.force.user.change.password')}" name="user.passwordChangeRequired" />    
    <ww:hidden label="Username"    name="username" />
    <ww:submit value="%{getText('update')}" method="submit" />
    <ww:submit value="%{getText('cancel')}" method="cancel" />
  </ww:form>
</redback:ifAuthorized>

<redback:ifAuthorized permission="user-management-user-role" resource="${user.username}">
  <c:if test="${!empty effectivelyAssignedRoles}">
  <h3><ww:text name="effective.roles"/></h3>

  <ul>
    <ww:iterator id="role" value="effectivelyAssignedRoles">
      <li>${role.name}</li>
    </ww:iterator>
  </ul>

</c:if>

  <ww:url id="assignmentUrl" action="assignments">
    <ww:param name="username">${user.username}</ww:param>
  </ww:url>
  <ww:a href="%{assignmentUrl}"><ww:text name="user.edit.roles"/></ww:a>
</redback:ifAuthorized>
</body>
</ww:i18n>
</html>
