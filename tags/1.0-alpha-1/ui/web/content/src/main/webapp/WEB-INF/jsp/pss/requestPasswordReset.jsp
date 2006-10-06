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

<%@ taglib prefix="ww" uri="/webwork" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
  <title>Request Password Reset</title>
</head>

<body onload="javascript:document.forms['passwordReset'].username.focus();">

<h2>Request Password Reset</h2>

<%@ include file="/WEB-INF/jsp/pss/include/formValidationResults.jsp" %>

<ww:form action="passwordReset!reset" namespace="/security" theme="xhtml" 
         id="passwordResetForm" method="post" name="passwordReset" cssClass="security passwordReset">
  <ww:textfield label="Username" name="username" size="30" required="true" />
  <ww:submit type="submit" value="Request Reset" name="submitButton" />
  <ww:submit type="button" value="Cancel" name="cancelButton" />
</ww:form>

</body>

</html>
