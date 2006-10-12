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

<h2>[Admin] User List</h2>

<table class="securityTable" border="1" cellspacing="0" cellpadding="2" width="80%">
  <thead>
    <tr>
      <th>Username</th>
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

<div class="buttonbar">
  <pss:ifAuthorized permission="user-management-user-create">
    <ww:form action="usercreate!show" namespace="/security" theme="xhtml"
             id="buttonBar" method="post" name="buttonbar" cssClass="security buttonbar">
      <ww:submit value="Create User"/>
    </ww:form>
  </pss:ifAuthorized>
</div>


</body>

</html>
