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
<head>
  <title>[Admin] Operation List</title>
  <ww:head/>
</head>

<body>

<%@ include file="/WEB-INF/jsp/pss/include/rbacListNavigation.jsp" %>

<h2> [Admin] Operation List </h2>

<%@ include file="/WEB-INF/jsp/pss/include/formValidationResults.jsp" %>

<ww:form action="operations!remove" method="post" theme="simple">

  <table>

    <c:choose>
      <c:when test="${!empty allOperations}">
        <thead>
          <tr>
            <th>&nbsp;</th>
            <th>Name</th>
            <th>Description</th>
          </tr>
        </thead>
        
        <c:forEach var="operation" items="${allOperations}">
          <tr>
            <td>
              <ww:checkbox name="selectedOperations" value="${operation.name}" />
            </td>
            <td>
              <ww:url id="operationUrl" action="operation-edit">
                <ww:param name="operationName" value="${operation.name}" />
              </ww:url>
              <ww:a href="%{operationUrl}"><c:out value="${operation.name}" /></ww:a>
            </td>
            <td>
              <c:out value="${operation.description}" />
            </td>
          </tr>
        </c:forEach>
      </c:when>
      <c:otherwise>
        <p><em>No Operations Available</em></p>
      </c:otherwise>
    </c:choose>
    
    <tr>
      <td colspan="3">
        <ww:submit value="Remove Selected Roles" />
      </td>
    </tr>

  </table>
  
</ww:form>

  </body>
</html>