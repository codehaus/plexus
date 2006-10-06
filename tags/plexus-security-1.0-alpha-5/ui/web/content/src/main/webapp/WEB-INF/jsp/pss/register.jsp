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
  <title>Registration Page</title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/pss/include/formValidationResults.jsp" %>

<h2>Register for an Account</h2>
   
<ww:form action="register!register" namespace="/security" theme="xhtml"
         id="registerForm" method="post" name="register" cssClass="security register">     
  
  <ww:textfield label="Username"         name="user.username" size="30" required="true"/>
  <ww:textfield label="Full Name"        name="user.fullName" size="30" required="true"/>
  <ww:textfield label="Email Address"    name="user.email" size="50"    required="true"/>
  
  <c:choose>
    <c:when test="${! emailValidationRequired}">
      <ww:password  label="Password"         name="user.password" size="20" required="true"/>
      <ww:password  label="Confirm Password" name="user.confirmPassword" size="20" required="true"/>
      <ww:submit type="input" value="Register" />
    </c:when>
    <c:otherwise>
      <ww:submit type="input" value="Validate Me" />
    </c:otherwise>
  </c:choose>
  
  <ww:submit type="button" value="Cancel"   name="cancelButton" />
</ww:form>

</body>

</html>
