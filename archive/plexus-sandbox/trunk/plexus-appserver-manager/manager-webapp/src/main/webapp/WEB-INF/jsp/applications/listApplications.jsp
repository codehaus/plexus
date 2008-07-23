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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
  <ww:i18n name="i18n.AppServerManager">
    <head>
        <title><ww:text name="page.listApplications.title"/></title>
    </head>
    <body>
      <div id="axial" class="h3">
        <h3><ww:text name="page.listApplications.section.title"/></h3>
        <div class="axial">
					<c:if test="${!empty actionMessages}">
					  <div class="success">
					    <ww:actionmessage />
					  </div>
					</c:if>        
					<c:if test="${!empty actionErrors}">
					  <div class="errormessage">
					    <c:forEach items="${actionErrors}" var="actionError">
					      <p><ww:text name="${actionError}"/></p>
					    </c:forEach>
					  </div>
					</c:if>					
          <table border="1" cellpadding="3" cellspacing="2" width="100%">
            <tbody>
              <tr>
                <th>
                  <div align="left"><ww:text name="page.listApplications.name.label" /></div>
                </th>
                <th>
                  <div align="left"><ww:text name="page.listApplications.remove.label" /></div>
                </th>   
                <!--th>
                  <div align="left"><ww:text name="page.listApplications.remove.label" /></div>
                </th-->                             
              </tr>
              <ww:iterator value="appRuntimeProfiles">
                <tr>
                  <td><ww:property value="name"/></td>
                  <td>
                    <ww:url id="deleteAppUrl" action="deleteApplication" namespace="/" includeParams="none">
                      <ww:param name="name" value="name"/>
                    </ww:url>
					          <ww:a href="%{deleteAppUrl}">
					            <img src="images/delete.gif" title="<ww:property value="common.remove.label"/>"/>
					          </ww:a>                    
                  </td>
                </tr>
              </ww:iterator>
            </tbody>
          </table>
        </div>
      </div>
    </body>
  </ww:i18n>
</html>