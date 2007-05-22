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
<head>
  <title>[Admin] Role Summary</title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/redback/include/rbacListNavigation.jsp" %>

<h2> [Admin] Role Summary </h2>

<%@ include file="/WEB-INF/jsp/redback/include/formValidationResults.jsp" %>

    <ul>
      <ww:iterator id="role" value="allRoles">
        <li>Role: ${role.name}</li>
        <ul>
        <ww:iterator id="permission" value="#role.permissions">
          <li>P[${permission.name}] (${permission.operation.name}, ${permission.resource.identifier})</li>
        </ww:iterator>
        </ul>
      </ww:iterator>
    </ul>

  </body>
</html>
