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
  <title><ww:text name="role.create.page.title"/></title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/redback/include/formValidationResults.jsp" %>

<h2><ww:text name="role.create.section.title"/></h2>

<ww:form action="rolecreate!submit" method="post" theme="xhtml"
         name="roleCreateForm" cssClass="securiy rolecreate">
  <ww:textfield label="%{getText('role.name')}" name="roleName" />
  <ww:textfield label="%{getText('role.description')}" name="description" />
  <tr>
    <td valign="top"><ww:text name="permissions"/></td>
    <td>
    
      <table cellspacing="0" cellpadding="2" cssClass="permission">
        <thead>
        <tr>
          <th><ww:text name="name"/></th>
          <th><ww:text name="role.create.operation"/></th>
          <th><ww:text name="role.create.resource"/></th>
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
          <em><ww:text name="role.create.no.permissions.defined"/></em>
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
          <ww:submit value="%{getText('role.create.add.permission')}" theme="simple" 
                     onclick="setSubmitMode('addPermission')" />
        </td>
      </tr>
      </table>
      
    </td>
  </tr>
  <ww:hidden name="submitMode" value="normal" />
  <ww:submit value="%{getText('submit')}" onclick="setSubmitMode('normal')" />
</ww:form>

<script language="javascript">
  function setSubmitMode(mode)
  {
    document.forms["roleCreateForm"].submitMode.value = mode;
  }
</script>

</body>
</ww:i18n>
</html>
