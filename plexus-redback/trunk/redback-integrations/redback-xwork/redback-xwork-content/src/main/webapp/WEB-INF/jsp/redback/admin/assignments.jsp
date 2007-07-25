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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<ww:i18n name="org.codehaus.plexus.redback.xwork.default">
<head>
  <title><ww:text name="assignments.page.title"/></title>
</head>

<body>

<h2><ww:text name="assignments.section.title"/></h2>

    <div class="axial">
      <table border="1" cellspacing="2" cellpadding="3" width="100%">
        <ww:label label="%{getText('username')}" name="principal"/>
        <ww:label label="%{getText('full.name')}" name="user.fullName"/>
        <ww:label label="%{getText('email')}" name="user.email"/>
      </table>
    </div>
    
<c:if test="${!empty effectivelyAssignedRoles}">
<h3><ww:text name="effective.roles"/></h3>

  <ul>
    <ww:iterator id="role" value="effectivelyAssignedRoles">
      <li>${role.name}</li>
    </ww:iterator>
  </ul>

</c:if>

<h3><ww:text name="assignments.assigned.roles"/></h3>

<c:choose>
  <c:when test="${!empty assignedRoles}">

    <ww:form action="removeRolesFromUser" namespace="/security" name="removeRoles">
      <ww:hidden name="principal"/>
      <ww:hidden name="removeRolesButton" value="true"/>
      <ww:checkboxlist list="assignedRoles" name="removeSelectedRoles" listValue="name" listKey="name" theme="redback"/>
      <br/>
      <ww:submit value="%{getText('assignments.remove.roles')}" name="removeRolesButton" theme="simple" />
    </ww:form>
  </c:when>
  <c:otherwise>
    <p><em><ww:text name="assignments.no.roles"/></em></p>
  </c:otherwise>
</c:choose>

<h3><ww:text name="assignments.available.roles"/></h3>

<c:choose>
  <c:when test="${!empty availableRoles}">
    <ww:form action="addRolesToUser" namespace="/security" name="addRoles">
      <ww:hidden name="principal"/>
      <ww:hidden name="addRolesButton" value="true"/>
      <ww:checkboxlist list="availableRoles" name="addSelectedRoles" listValue="name" listKey="name" theme="redback"/>
      <br/>
      <ww:submit value="%{getText('assignments.add.roles')}" name="addRolesButton" theme="simple" />
    </ww:form>
  </c:when>
  <c:otherwise>
    <p><em><ww:text name="assignments.no.roles.to.grant"/></em></p>
  </c:otherwise>
</c:choose>

</body>
</ww:i18n>
</html>
