<%--
  ~ Copyright 2007 The Apache Software Foundation.
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
<ww:i18n name="org.codehaus.plexus.redback.xwork.default">
<head>
  <title><ww:text name="password.reset.page.title"/></title>
</head>

<body>

<p>
  <ww:text name="password.reset.message"/>
</p>

<p>
<ww:url id="loginUrl" action="login" />
<ww:text name="password.reset.go.to"/><ww:a href="%{loginUrl}"><ww:text name="login"/></ww:a>
</p>

</body>
</ww:i18n>
</html>
