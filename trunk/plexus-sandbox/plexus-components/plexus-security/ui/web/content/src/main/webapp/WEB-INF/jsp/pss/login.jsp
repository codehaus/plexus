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

<html>
<head>
  <title>Login Page</title>
  <ww:head/>
</head>

<body onload="javascript:document.forms['login'].username.focus();">

<h2>Login</h2>

<%@ include file="/WEB-INF/jsp/pss/include/formValidationResults.jspf" %>

<ww:form action="login!login" namespace="/security" theme="xhtml" 
         id="loginForm" method="post" name="login" cssClass="security login">
  <ww:textfield label="Username" name="username" size="30" required="true" />
  <ww:password  label="Password" name="password" size="20" required="true" />
  <ww:submit type="submit" value="Login" name="submitButton" />
  <ww:submit type="button" value="Cancel" name="cancelButton" />
</ww:form>

<%-- TODO: Figure out how to auto-focus to first field --%>

<ul class="tips">
  <%--
  <li>
     Forgot your Username? 
     <ww:url id="forgottenAccount" action="findAccount" />
     <ww:a href="%{forgottenAccount}">Email me my account information.</ww:a>
  </li>
  <li>
     Forgot your Password? 
     <ww:url id="forgottenPassword" action="resetPassword" />
     <ww:a href="%{forgottenPassword}">Request a password reset.</ww:a>
  </li>
    --%>
  <li>
    Need an Account?
    <ww:url id="registerUrl" action="register" />
    <ww:a href="%{registerUrl}">Register!</ww:a>
</ul>

</body>

</html>
