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

<%@ taglib prefix="ww" uri="/webwork" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/plexusSecuritySystem" prefix="pss" %>

<html>
<head>
  <title>[Admin] User List</title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/pss/include/formValidationResults.jsp" %>

<pss:ifAuthorized permission="user-management-user-create">
  <div style="float: right">
    <ww:url id="userCreateUrl" action="usercreate!show" namespace="/security"/>
    <ww:a href="%{userCreateUrl}">Create User</ww:a>
  </div>
</pss:ifAuthorized>
<h2>[Admin] User List</h2>

<ww:form action="userlist!show" namespace="/security" theme="xhtml" method="post">
  <ww:hidden name="ascending"/>
  <ww:select label="List users with role"
             list="roles"
             name="roleName"
             value="roleName"
             listKey="name"
             listValue="name"
             headerKey=""
             headerValue="Any"/>
  <ww:select label="User search"
             list="criteria"
             name="criterion"
             value="criterion"/>
  <ww:textfield name="searchKey" size="30" />
  <ww:submit value="Search"/>
</ww:form>

<hr/>

<ww:set name="users" value="users" />
<c:choose>
  <c:when test="${empty(users)}">
    <strong>No users found matching criteria.</strong>
  </c:when>
  <c:otherwise>
    <table>
      <thead>
        <tr>
          <th nowrap="true">
            <c:set var="url">
              <ww:url namespace="/security" action="userlist" method="show">
                <ww:param name="ascending" value="${!ascending}" />
                <ww:param name="roleName" value="roleName" />
                <ww:param name="criterion" value="criterion" />
                <ww:param name="searchKey" value="searchKey" />
              </ww:url>
            </c:set>
            <c:choose>
              <c:when test="${ascending}">
                <a href="${url}"><img align="absmiddle" width="15" height="15" src="<ww:url value='/images/pss/icon_sortdown.gif'/>" title="<ww:text name='Sort descending'/>" border="0" alt=""></a>
              </c:when>
              <c:otherwise>
                <a href="${url}"><img align="absmiddle" width="15" height="15" src="<ww:url value='/images/pss/icon_sortup.gif'/>" title="<ww:text name='Sort ascending'/>" border="0" alt=""></a>
              </c:otherwise>
            </c:choose>
            Username
          </th>
          <th>Full Name</th>
          <th>Email Address</th>
          <th>Validated</th>
          <th>Locked</th>
          <th>&nbsp;</th>
          <th>&nbsp;</th>
        </tr>
      </thead>
      <tbody>

        <c:forEach var="user" items="${users}">
          <tr>
            <td>${user.username}</td>
            <td>${user.fullName}</td>
            <td>${user.email}</td>
            <td>${user.validated}</td>
            <td>${user.locked}</td>
            <td>
              <pss:ifAuthorized permission="user-management-user-edit" resource="${username}">
                <ww:url id="usereditUrl" action="useredit">
                  <ww:param name="username">${user.username}</ww:param>
                </ww:url>
                <ww:a href="%{usereditUrl}">Edit</ww:a>
              </pss:ifAuthorized>
            </td>
            <td>
              <pss:ifAuthorized permission="user-management-user-delete" resource="${username}">
                <ww:url id="userDeleteUrl" action="userdelete">
                  <ww:param name="username">${user.username}</ww:param>
                </ww:url>
                <ww:a href="%{userDeleteUrl}">Delete</ww:a>
              </pss:ifAuthorized>
            </td>
          </tr>
        </c:forEach>

      </tbody>
    </table>
  </c:otherwise>
</c:choose>

</body>

</html>
