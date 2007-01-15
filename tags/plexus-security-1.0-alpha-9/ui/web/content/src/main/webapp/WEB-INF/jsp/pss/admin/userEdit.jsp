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
  <title>[Admin] User Edit</title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/pss/include/formValidationResults.jsp" %>

<h2>[Admin] User Edit</h2>

<ww:form action="useredit" namespace="/security" theme="xhtml"
         id="userEditForm" method="post" name="useredit" cssClass="security userEdit">
  <%@ include file="/WEB-INF/jsp/pss/include/userCredentials.jsp" %>
  <ww:checkbox label="Locked User" name="user.locked" />
  <ww:checkbox label="Change Password Next Login" name="user.passwordChangeRequired" />
  <ww:hidden   label="Username"    name="username" />
  <ww:submit value="Update" method="submit" />
  <ww:submit value="Cancel" method="cancel" />
</ww:form>

  <ww:action name="assignments" executeResult="true" >
    <ww:param name="principal">${user.username}</ww:param>
  </ww:action>

</body>

</html>
