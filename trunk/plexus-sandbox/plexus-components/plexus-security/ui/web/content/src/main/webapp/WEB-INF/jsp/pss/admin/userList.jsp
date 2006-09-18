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
  <title>[Admin] User List</title>
  <ww:head/>
</head>

<body>

<%@ include file="/WEB-INF/jsp/pss/include/formValidationResults.jspf" %>

<h2>[Admin] User List</h2>

<table border="1" cellspacing="0" cellpadding="2" width="80%">
  <thead>
    <tr>
      <th>Username</th>
      <th>Full Name</th>
      <th>Email Address</th>
      <th>Locked</th>
    </tr>
  </thead>
  <tbody>

    <ww:iterator value="users">
      <tr>
        <td><!--  TODO: link the username to user edit action. -->
        <ww:property value="username" />
        </td>
        <td><ww:property value="fullName" /></td>
        <td><ww:property value="email" /></td>
        <td><ww:property value="locked" /></td>
      </tr>
    </ww:iterator>

  </tbody>
</table>

<div class="buttonbar">
  <ww:form cssClass="buttonbar" action="userCreate" method="post" namespace="/security">
    <ww:submit value="Create User" />
  </ww:form>
</div>


</body>

</html>
