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
  <link rel="stylesheet" type="text/css" href="<ww:url value="/css/pss/table.css"/>" media="screen"/>
</head>

<body>

<%@ include file="/WEB-INF/jsp/pss/include/formValidationResults.jsp" %>


<h2>[Admin] List of Users in ${roleName} Role</h2>

<table class="outerTableRegion" cellpadding="0" cellspacing="0">
<tr>
<td><ec:table 
    var="user" 
    items="users" 
    action="${pageContext.request.contextPath}/security/userlist!show.action"
    imagePath="${pageContext.request.contextPath}/images/pss/table/*.gif"
     autoIncludeParameters="false" 
    title="Users"
    showTitle="false"
    showExports="false"
    view="org.codehaus.plexus.security.ui.web.eXc.views.SecurityView" 
    sortRowsCallback="org.codehaus.plexus.security.ui.web.eXc.ProcessUserRowsCallback"
    cellspacing="2"
    cellpadding="3"
    >
    <%-- TODO: Fix export download. --%>
    <ec:export 
       view="csv" 
       fileName="users.csv" 
       imageName="csv"
       tooltip="Export Table to CSV (Comma Seperated Values)."/>
    <ec:export 
       view="xls" 
       fileName="users.xls" 
       imageName="xls"
       tooltip="Export Table to Excel format."/>
    <ec:row>          
        <ec:column property="username" title="User Name">
          <img src="<c:url value="/images/pss/icon-user.gif"/>" />
          <pss:ifAuthorized permission="user-management-user-edit" resource="${user.username}">
            <ww:url id="usereditUrl" action="useredit">
              <ww:param name="username">${user.username}</ww:param>
            </ww:url>
            <ww:a href="%{usereditUrl}">${user.username}</ww:a>
          </pss:ifAuthorized>
          <pss:elseAuthorized>
            <pss:ifAuthorized permission="user-management-user-role" resource="${user.username}">
              <ww:url id="usereditUrl" action="useredit">
                <ww:param name="username">${user.username}</ww:param>
              </ww:url>
              <ww:a href="%{usereditUrl}">${user.username}</ww:a>
            </pss:ifAuthorized>
            <pss:elseAuthorized>
              ${user.username}
            </pss:elseAuthorized>
          </pss:elseAuthorized>
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
              <ww:url id="userdeleteUrl" action="userdelete">
                <ww:param name="username">${user.username}</ww:param>
              </ww:url>
              <ww:a href="%{userdeleteUrl}" title="Delete ${user.username}">
                <img src="<c:url value="/images/pss/delete.gif"/>" border="none"/>
              </ww:a>              
            </pss:ifAuthorized>
          </c:if>
        </ec:column>
    </ec:row>    
</ec:table></td>
</tr>
<tr><td></td></tr>
<tr>
<td>
    <pss:ifAuthorized permission="user-management-user-create">
      <div class="task createUser">
        <ww:form action="usercreate!show" namespace="/security" theme="simple" method="post">
          <ww:submit cssClass="button" value="Create New User" />
        </ww:form>
      </div>
    </pss:ifAuthorized>
</td>
</tr>
</table>

<br>
<br>
<b>Reports</b>
<br>

<table class="tools" border="0" cellspacing="1" cellpadding="0">

      <tr>
        <th class="toolHeading" width="30%">Name</th>
        <th class="toolHeading column">Types</th>
      </tr>
      
      <c:forEach items="${reportMap}" var="reportEntry">
        <tr>
          <td class="reportName" nowrap="nowrap">
            <c:forEach items="${reportEntry.value}" var="report" varStatus="status">
              <c:if test="${status.first}">
                ${report.value.name}
              </c:if>
            </c:forEach>
          </td>
          <td class="reportViews">
          <c:forEach items="${reportEntry.value}" var="report" varStatus="status">
            <a href="<c:url value="/security/report!generate.action?reportId=${report.value.id}&reportType=${report.value.type}"/>" 
              title="${report.value.name} Report (${report.value.type} type)"><img 
              src="<c:url value="/images/pss/table/${report.value.type}.gif"/>" /></a>
          </c:forEach>
          </td>
        </tr>
      </c:forEach>

</table>

</body>

</html>
