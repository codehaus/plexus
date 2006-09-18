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

<%@ taglib prefix="ww" uri="/webwork"%>

<html>
<head>
  <title>[Admin] User Create</title>
  <ww:head/>
</head>

<body>

<%@ include file="/WEB-INF/jsp/pss/include/formValidationResults.jspf" %>

<h2>[Admin] User Create</h2>

<ww:form action="usercreate!submit" namespace="/security" theme="xhtml"
         id="userCreateForm" method="post" name="usercreate" cssClass="security userCreate">
  <%@ include file="/WEB-INF/jsp/security/userCredentials.jspf"%>
  <ww:submit value="Create User" />
</ww:form>

</body>

</html>
