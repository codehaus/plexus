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
<%@ taglib prefix="pss" uri="plexusSecuritySystem"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
  <title>[Admin] Create Role</title>
  <ww:head/>
</head>

<body>

<%@ include file="/WEB-INF/jsp/pss/include/formValidationResults.jspf" %>

<h2>[Admin] Create Role</h2>

<ww:form action="rolecreate!submit" method="post" theme="xhtml">
  <ww:textfield label="Role Name" name="role.name" />
  <ww:textfield label="Role Description" name="role.description" />
  <tr>
    <td valign="top">Permissions</td>
    </td>
    <td>
    
      <table>
        <thead>
          <th>Name</th>
          <th>Operation</th>
          <th>Resource</th>
        </thead>
      <c:choose>
        <c:when test="${!empty role.permissions}">
          <ww:iterator id="permission" value="role.permissions">
            <tr>
              <td>
                <c:out value="${permission.name}" />
              </td>
              <td>
                <c:out value="${permission.operationName}" />
              </td>
              <td>
                <c:out value="${permission.resourceIdentifier}" />
              </td>
            </tr>
          </ww:iterator>
        </c:when>
        <c:otherwise>
          <em>No Permissions Defined for this Role (yet)</em>
        </c:otherwise>
      </c:choose>
      
      <tr>
        <td>
          <ww:textfield name="addpermission.name" theme="simple"/>
        </td>
        <td>
          <ww:textfield name="addpermission.operationName" theme="simple" />
        </td>
        <td>
          <ww:textfield name="addpermission.resourceIdentifier" theme="simple" />
        </td>
        <td>
          <ww:submit value="Add Permission" name="addPermissionButton" theme="simple" />
        </td>
      </tr>
      </table>
      
    </td>
  </tr>
  <ww:submit value="Submit" name="submitButton" />
</ww:form>

</body>
</html>