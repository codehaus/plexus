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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/plexusSecuritySystem" prefix="pss" %>
<%@ taglib uri="http://www.extremecomponents.org" prefix="ec" %>

<html>
<head>
  <title>[Admin] User List</title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/pss/include/formValidationResults.jsp" %>


<h2>[Admin] User List</h2>

<div class="users">
  <div class="reports">
    Reports: 
    <c:forEach items="${reportMap}" var="reportEntry">
       <span class="report">
         <c:forEach items="${reportEntry.value}" var="report" varStatus="status">
           <a href="${pageContext.request.contextPath}/security/report!generate.action?reportId=${report.value.id}&reportType=${report.value.type}" 
              title="${report.value.name} Report (${report.value.type} type)">
             <c:if test="${status.count eq 1}">
               <span class="name">
                 ${report.value.name}
               </span>
             </c:if>
             <img src="${pageContext.request.contextPath}/images/pss/table/${report.value.type}.gif" /></a>
         </c:forEach>
       </span>
    </c:forEach>
  </div>
  
<div class="extraFilters">
<ww:form action="userlist!show" namespace="/security" theme="simple" method="get">
  <ww:label value="Filter By Role:" />
  <ww:select list="roles"
             name="roleName"
             value="roleName"
             listKey="name"
             listValue="name"
             headerKey=""
             headerValue="Any"/>
</ww:form>
</div>
<ec:table 
    var="user" 
    items="users" 
    action="${pageContext.request.contextPath}/security/userlist!show.action" 
    imagePath="${pageContext.request.contextPath}/images/pss/table/*.gif"
    title="Users"
    showTitle="false"
    view="compact" sortRowsCallback="org.codehaus.plexus.security.ui.web.eXc.ProcessUserRowsCallback"
	>
    <ec:export 
       view="csv" 
       fileName="users.csv" 
       imageName="csv"
       tooltip="Export CSV (Comma Seperated Values)"/>
    <ec:export 
       view="xls" 
       fileName="users.xls" 
       imageName="xls"
       tooltip="Export Excel"/>
    <ec:row>          
	    <ec:column property="username" title="User Name">
          <img src="${pageContext.request.contextPath}/images/pss/icon-user.gif" />
          <pss:ifAuthorized permission="user-management-user-edit" resource="${user.username}">
            <ww:url id="usereditUrl" action="useredit">
              <ww:param name="username">${user.username}</ww:param>
            </ww:url>
            <ww:a href="%{usereditUrl}">${user.username}</ww:a>
          </pss:ifAuthorized>
        </ec:column>
        <ec:column property="fullName" title="Full Name" alias="fullname" />
        <ec:column property="email" title="Email" cell="org.codehaus.plexus.security.ui.web.eXc.MailtoCell" />
        <ec:column property="permanent" cell="org.codehaus.plexus.security.ui.web.eXc.CheckboxImageCell" style="text-align: center"
          title="Permanent" filterable="false"/> <%-- Boolean's can't be filtered --%>
        <ec:column property="validated" cell="org.codehaus.plexus.security.ui.web.eXc.CheckboxImageCell" style="text-align: center"
          title="Validated" filterable="false"/> <%-- Boolean's can't be filtered --%>
        <ec:column property="locked" cell="org.codehaus.plexus.security.ui.web.eXc.CheckboxImageCell" style="text-align: center"
          title="Locked" filterable="false"/> <%-- Boolean's can't be filtered --%>
        
        <ec:column title="Tasks" alias="tasks" sortable="false" filterable="false" styleClass="tasks">
          <c:if test="${user.permanent eq false}">
            <pss:ifAuthorized permission="user-management-user-delete" resource="${user.username}">
              <ww:form action="userdelete" namespace="/security" theme="simple" method="post">
                <ww:hidden name="username" value="${user.username}" />
                <ww:submit type="image" title="Delete ${user.username}" src="${pageContext.request.contextPath}/images/pss/delete.gif" />
              </ww:form>
            </pss:ifAuthorized>
          </c:if>
        </ec:column>
    </ec:row>    
</ec:table>
<pss:ifAuthorized permission="user-management-user-create">
  <div class="createUser">
    <ww:form action="usercreate!show" namespace="/security" theme="xhtml" method="post">
      <ww:submit value="Create User" />
    </ww:form>
  </div>
</pss:ifAuthorized>
</div>

</body>

</html>
