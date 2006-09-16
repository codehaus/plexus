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
  <title>Plexus Security Example Webapp</title>
  <ww:head />
</head>

<body>

<h4>This is the example mainpage</h4>

<ol>
<li>
  <ww:url id="login" action="main">
    <ww:param name="dest">login</ww:param>
  </ww:url>
  Go Ahead <ww:a href="%{login}">Login.</ww:a>
</li>
</ol>

</body>

</html>