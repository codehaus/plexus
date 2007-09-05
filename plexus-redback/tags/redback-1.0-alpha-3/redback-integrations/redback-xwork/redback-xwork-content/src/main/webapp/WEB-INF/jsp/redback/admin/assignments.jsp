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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html>
<ww:i18n name="org.codehaus.plexus.redback.xwork.default">
<head>
  <title><ww:text name="assignments.page.title"/></title>
</head>

<body>

<h2><ww:text name="assignments.section.title"/></h2>

    <div class="axial">
      <table border="1" cellspacing="2" cellpadding="3" width="100%">
        <ww:label label="%{getText('username')}" name="principal"/>
        <ww:label label="%{getText('full.name')}" name="user.fullName"/>
        <ww:label label="%{getText('email')}" name="user.email"/>
      </table>
    </div>
    
<h3><ww:text name="assignments.available.roles"/></h3>

    <ww:form action="addRolesToUser" namespace="/security" name="addRoles">
      <ww:hidden name="principal"/>
      <ww:hidden name="addRolesButton" value="true"/>
      
      <h4>Global Roles</h4>
      <ww:checkboxlist list="nondynamicroles" name="addNDSelectedRoles" value="NDRoles" theme="redback"/>
      <br/>
      
      <h4>Resource Roles</h4>
      <c:choose>
        <c:when test="${!empty availableRoles}"> 
          <c:set var="numtemplates" value="0"/>
          <table border="1">
           <tr>
             <td>&nbsp</td>
             <ww:iterator id="template" value="templates">
      	       <td>${template.namePrefix}</td>
      	       <c:set var="numtemplates" value="${numtemplates + 1}"/>
              </ww:iterator>
           </tr>
           <tr>
             <c:set var="count" value="0"/>        
             <ww:iterator id="dynamicrole" value="dynamicroles" status="row_status">
               <c:if test="${count == 0}">
                 <td>${dynamicrole.resource}</td>
               </c:if>
               <c:set var="chkbx" value="<input type='checkbox' name='addDSelectedRoles' value='${dynamicrole.name}'/>"/>
               <ww:iterator id="drole" value="DRoles">
                 <c:if test="${(drole == dynamicrole.name)}">
                   <c:set var="chkbx" value="<input type='checkbox' name='addDSelectedRoles' value='${dynamicrole.name}' checked='yes'/>"/>
                 </c:if>
               </ww:iterator>
               <td><center>${chkbx}</center></td>
               <c:set var="count" value="${count + 1}"/>
               <c:if test="${count == numtemplates}">
                 <c:choose>
                   <c:when test="${row_status.last}">
                     </tr>
                   </c:when>
                   <c:otherwise>
                     </tr><tr>
                   </c:otherwise>
                 </c:choose>
                 <c:set var="count" value="0"/>
               </c:if>
             </ww:iterator>
          </table>
        </c:when>
        <c:otherwise>
          <p><em><ww:text name="assignments.no.roles.to.grant"/></em></p>
        </c:otherwise>
      </c:choose>

      <br/>
      <ww:submit value="%{getText('assignments.submit')}" name="submitRolesButton" theme="simple" />
      <br/>
      <ww:reset type="button" value="%{getText('assignments.reset')}" name="resetRolesButton" theme="simple" />
    </ww:form>
  
</body>
</ww:i18n>
</html>
