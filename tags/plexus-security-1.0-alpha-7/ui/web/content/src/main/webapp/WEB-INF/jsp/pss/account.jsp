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

<html>
<head>
  <title>Account Details</title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/pss/include/formValidationResults.jsp" %>

<h2>Account Details</h2>
   
<ww:form action="account!submit" namespace="/security" theme="xhtml"
         id="registerForm" method="post" name="register" cssClass="security register">     
  <%@ include file="/WEB-INF/jsp/pss/include/userCredentials.jsp" %>
  <ww:submit type="submit" value="Submit"   name="submitButton" />
  <ww:submit type="button" value="Cancel"   name="cancelButton"   />
</ww:form>

</body>

</html>
