<!--
/*
 * Copyright 2007 The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
-->

<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<html>
  <ww:i18n name="i18n.AppServerManager">
    <head>
				<META HTTP-EQUIV="refresh" CONTENT="2;url=<ww:url includeParams="all"/>"/>
    </head>
    <body>
			<img src="images/sablier.gif">
			  <ww:text name="page.addApplication.wait.label">
			    <ww:param value="applicationId" />
			  </ww:text>
		</body>
	</ww:i18n>
</html>