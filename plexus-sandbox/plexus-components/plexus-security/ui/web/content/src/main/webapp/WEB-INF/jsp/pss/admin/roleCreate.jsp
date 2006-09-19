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
<%@ taglib prefix="pss" uri="/plexusSecuritySystem"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
  <title>[Admin] Create Role</title>
  <ww:head/>
</head>

<body>

<c:import url="/WEB-INF/jsp/pss/include/formValidationResults.jspf" />

<h2>[Admin] Create Role</h2>

<ww:form action="rolecreate!submit" method="post" theme="xhtml"
         name="roleCreateForm" cssClass="securiy rolecreate">
  <ww:textfield label="Role Name" name="roleName" />
  <ww:textfield label="Role Description" name="description" />
  <tr>
    <td valign="top">Permissions</td>
    <td>
    
      <table cellspacing="0" cellpadding="2" cssClass="permission">
        <thead>
        <tr>
          <th>Name</th>
          <th>Operation</th>
          <th>Resource</th>
        </tr>
        </thead>
      <c:choose>
        <c:when test="${!empty permissions}">
          <c:forEach var="permission" varStatus="loop" items="${permissions}">
            <tr>
              <td>
                <input type="text" name="permissions(${loop.index}).name"
                  value="${permission.name}" />
              </td>
              <td>
                <input type="text" name="permissions(${loop.index}).operationName"
                  value="${permission.operationName}" />
              </td>
              <td>
                <input type="text" name="permissions(${loop.index}).resourceIdentifier"
                  value="${permission.resourceIdentifier}" />
              </td>
            </tr>
          </c:forEach>
        </c:when>
        <c:otherwise>
          <em>No Permissions Defined for this Role (yet)</em>
        </c:otherwise>
      </c:choose>
      
      <tr class="addPermission">
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
          <ww:submit type="button" value="Add Permission" theme="simple" 
                     onclick="setSubmitMode('addPermission')" />
        </td>
      </tr>
      </table>
      
    </td>
  </tr>
  <ww:hidden name="submitMode" value="normal" />
  <ww:submit value="Submit" onclick="setSubmitMode('normal')" />
</ww:form>

<script language="javascript">
  function setSubmitMode(mode)
  {
    document.forms["roleCreateForm"].submitMode.value = mode;
  }
</script>

</body>
</html>
