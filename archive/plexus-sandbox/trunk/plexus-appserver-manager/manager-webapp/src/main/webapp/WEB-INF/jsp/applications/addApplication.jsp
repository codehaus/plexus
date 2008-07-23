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
        <title><ww:text name="page.addApplication.title"/></title>
    </head>
    <body>
      <div id="axial" class="h3">
        <h3><ww:text name="page.addApplication.section.title"/></h3>
        <div class="axial">
          <ww:form method="post" action="addApplication.action" name="addApplication" enctype="multipart/form-data">
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
                  <ww:file name="applicationFile" label="%{getText('page.addApplication.file.label')}"  />
                </tr>
              </tbody>
            </table>
					  <div class="functnbar3">
					    <div align="left">
					      <ww:submit value="%{getText('common.add.label')}" />
					    </div>
					  </div> 	            
          </ww:form>
        </div>
      </div>
    </body>
  </ww:i18n>
</html>