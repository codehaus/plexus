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

<html>
<head>
  <title>[Admin] User Delete</title>
</head>

<body>

<h2>[Admin] User Delete</h2>

<ww:form action="userdelete!submit" namespace="/security">
  <p>
    The following user will be deleted: <b><ww:property value="username"/></b>
  </p>
  <ww:hidden label="Username" name="username" />
  <ww:submit value="Delete User" />
</ww:form>

</body>

</html>
