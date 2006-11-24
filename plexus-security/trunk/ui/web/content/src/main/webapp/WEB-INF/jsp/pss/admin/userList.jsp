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
  <tr>
    <td colspan="3">
      <table cellpadding="0" cellspacing="0">
        <ww:select label="List users with role"
                   list="roles"
                   name="roleName"
                   value="roleName"
                   listKey="name"
                   listValue="name"
                   headerKey=""
                   headerValue="Any"/>
      </table>
    </td>
  </tr>
  <tr>
    <td nowrap="true">
      <table cellpadding="0" cellspacing="0">
        <ww:select label="User search"
                   list="criteria"
                   name="criterion"
                   value="criterion"/>
      </table>
    </td>
    <td>
      <table cellpadding="0" cellspacing="0">
        <ww:textfield name="searchKey"/>
      </table>
    </td>
    <td colspan="2" align="right">
      <table cellpadding="0" cellspacing="0">
        <ww:submit value="Search"/>
      </table>
    </td>
  </tr>
</ww:form>

<hr/>

<table class="securityTable" border="1" cellspacing="0" cellpadding="2" width="80%">
  <thead>
    <tr>
      <th nowrap="true">
        <ww:form id="sortlist" name="sortlist" action="userlist!show" namespace="/security" theme="xhtml" method="post">
          <ww:if test="${ascending}">
            <ww:a href="javascript: sortlist.submit()"><img src="<ww:url value='/images/icon_sortdown.gif'/>"
                                                            title="<ww:text name='Sort descending'/>" border="0"></ww:a>
            Username
          </ww:if>
          <ww:else>
            <ww:a href="javascript: sortlist.submit()"><img src="<ww:url value='/images/icon_sortup.gif'/>"
                                                            title="<ww:text name='Sort ascending'/>" border="0"></ww:a>
            Username
          </ww:else>
          <ww:hidden name="ascending" value="${!ascending}"/>
          <ww:hidden name="roleName"/>
          <ww:hidden name="criterion"/>
          <ww:hidden name="searchKey"/>
        </ww:form>
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

    <ww:iterator value="users">
      <tr>
        <td>
          <ww:property value="username"/>
        </td>
        <td>
          <ww:property value="fullName"/>
        </td>
        <td>
          <ww:property value="email"/>
        </td>
        <td>
          <ww:property value="validated"/>
        </td>
        <td>
          <ww:property value="locked"/>
        </td>
        <td>
          <pss:ifAuthorized permission="user-management-user-edit" resource="${username}">
            <ww:url id="usereditUrl" action="useredit">
              <ww:param name="username">${username}</ww:param>
            </ww:url>
            <ww:a href="%{usereditUrl}">Edit</ww:a>
          </pss:ifAuthorized>
        </td>
        <td>
          <pss:ifAuthorized permission="user-management-user-delete" resource="${username}">
            <ww:url id="userDeleteUrl" action="userdelete">
              <ww:param name="username">${username}</ww:param>
            </ww:url>
            <ww:a href="%{userDeleteUrl}">Delete</ww:a>
          </pss:ifAuthorized>
        </td>
      </tr>
    </ww:iterator>

  </tbody>
</table>
</body>

</html>
