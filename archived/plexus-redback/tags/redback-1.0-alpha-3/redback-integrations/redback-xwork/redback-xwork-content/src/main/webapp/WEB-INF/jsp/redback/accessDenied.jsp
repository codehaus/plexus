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
<ww:i18n name="org.codehaus.plexus.redback.xwork.default">
<head>
  <title><ww:text name="access.denied.page.title"/></title>
</head>

<body>

<h4><ww:text name="access.denied.section.title"/></h4>

<div id="results">
  <%-- This is where the "Account Created Successfully" type message goes. --%>
  <div class="success">
    <ww:actionmessage />
  </div>
  <%-- This is where errors from the action and other non-form field specific errors appear. --%>
  <div class="errors">
    <ww:actionerror />
  </div>
</div>

<p>
  <ww.text name="access.denied.message"/>
</p>

</body>
</ww:i18n>
</html>