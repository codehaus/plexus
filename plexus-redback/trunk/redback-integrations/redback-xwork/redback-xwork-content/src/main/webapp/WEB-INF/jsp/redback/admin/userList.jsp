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
<%@ taglib uri="http://plexus.codehaus.org/redback/taglib-1.0" prefix="redback" %>
<%@ taglib uri="http://www.extremecomponents.org" prefix="ec" %>

<html> 
<ww:i18n name="org.codehaus.plexus.redback.xwork.default">
<head>
  <title><ww:text name="user.list.page.title"/></title>
  <link rel="stylesheet" type="text/css" href="<ww:url value="/css/redback/table.css"/>" media="screen"/>
</head>

<body>

<%@ include file="/WEB-INF/jsp/redback/include/formValidationResults.jsp" %>

<!-- ec on redback should not be dependent on the resource bundle of the project using it -->
<ww:set name="username" value="%{getText('username')}"/>
<ww:set name="fullName" value="%{getText('full.name')}"/>
<ww:set name="email" value="%{getText('email')}"/>
<ww:set name="permanent" value="%{getText('user.list.permanent')}"/>
<ww:set name="validated" value="%{getText('user.list.validated')}"/>
<ww:set name="locked" value="%{getText('user.list.locked')}"/>
<ww:set name="tasks" value="%{getText('user.list.tasks')}"/>

<h2><ww:text name="user.list.section.title.1"/>${roleName}<ww:text name="user.list.section.title.2"/></h2>

<table class="outerTableRegion" cellpadding="0" cellspacing="0">
<tr>
<td><ec:table 
    var="user" 
    items="users" 
    action="${pageContext.request.contextPath}/security/userlist!show.action"
    imagePath="${pageContext.request.contextPath}/images/redback/table/*.gif"
    autoIncludeParameters="false" 
    title="Users"
    filterable="true"
    showTitle="false"
    showExports="false"
    view="org.codehaus.plexus.redback.xwork.eXc.views.SecurityView" 
    sortRowsCallback="org.codehaus.plexus.redback.xwork.eXc.ProcessUserRowsCallback"
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
        <ec:column property="username" title="${username}" 
        	filterCell="org.codehaus.plexus.redback.xwork.eXc.SecurityFilterCell">
          <img src="<c:url value="/images/redback/icon-user.gif"/>" />
          <redback:ifAuthorized permission="user-management-user-edit" resource="${user.username}">
            <ww:url id="usereditUrl" action="useredit">
              <ww:param name="username">${user.username}</ww:param>
            </ww:url>
            <ww:a href="%{usereditUrl}">${user.username}</ww:a>
          </redback:ifAuthorized>
          <redback:elseAuthorized>
            <redback:ifAuthorized permission="user-management-user-role" resource="${user.username}">
              <ww:url id="usereditUrl" action="useredit">
                <ww:param name="username">${user.username}</ww:param>
              </ww:url>
              <ww:a href="%{usereditUrl}">${user.username}</ww:a>
            </redback:ifAuthorized>
            <redback:elseAuthorized>
              ${user.username}
            </redback:elseAuthorized>
          </redback:elseAuthorized>
        </ec:column>
        <ec:column property="fullName" title="${fullName}" alias="fullname" 
        	filterCell="org.codehaus.plexus.redback.xwork.eXc.SecurityFilterCell" />
        <ec:column property="email" title="${email}" cell="org.codehaus.plexus.redback.xwork.eXc.MailtoCell" 
        	filterCell="org.codehaus.plexus.redback.xwork.eXc.SecurityFilterCell" />
        <ec:column property="permanent" cell="org.codehaus.plexus.redback.xwork.eXc.CheckboxImageCell" 
        	style="text-align: center" title="${permanent}" filterable="false"/> <%-- Boolean's can't be filtered --%>
        <ec:column property="validated" cell="org.codehaus.plexus.redback.xwork.eXc.CheckboxImageCell" 
        	style="text-align: center" title="${validated}" filterable="false"/> <%-- Boolean's can't be filtered --%>
        <ec:column property="locked" cell="org.codehaus.plexus.redback.xwork.eXc.CheckboxImageCell" 
        	style="text-align: center" title="${locked}" filterable="false"/> <%-- Boolean's can't be filtered --%>
        
        <ec:column title="${tasks}" alias="tasks" sortable="false" filterable="false" styleClass="tasks">
          <c:if test="${user.permanent eq false}">
            <redback:ifAuthorized permission="user-management-user-delete" resource="${user.username}">
              <ww:url id="userdeleteUrl" action="userdelete">
                <ww:param name="username">${user.username}</ww:param>
              </ww:url>
              <ww:a href="%{userdeleteUrl}" title="%{getText('delete')} ${user.username}">
                <img src="<c:url value="/images/redback/delete.gif"/>" border="none"/>
              </ww:a>              
            </redback:ifAuthorized>
          </c:if>
        </ec:column>
    </ec:row>    
</ec:table></td>
</tr>

<%--
<tr><td></td></tr>
<tr>

<td>
    <redback:ifAuthorized permission="user-management-user-create">
      <div class="task createUser">
        <ww:form action="usercreate!show" namespace="/security" theme="simple" method="post">
          <ww:submit cssClass="button" value="Create New User" />
        </ww:form>
      </div>
    </redback:ifAuthorized>
</td>
</tr> --%>
</table>

<br>
<br>
<b><ww:text name="user.list.tools"/></b>
<br>

<table class="tools" border="0" cellspacing="1" cellpadding="0">

<tr>
  <th class="toolHeading"><ww:text name="user.list.tasks"/></th>
  <th class="toolHeading column"><ww:text name="user.list.reports"/></th>
</tr>

<tr>
  <td valign="top">
    <p class="description"><ww:text name="user.list.message"/></p>
     
    <redback:isNotReadOnlyUserManager>
    <redback:ifAuthorized permission="user-management-user-create">
      <div class="task createUser">
        <ww:form action="usercreate!show" namespace="/security" theme="simple" method="post">
          <ww:submit cssClass="button" value="%{getText('user.list.create.new.user')}" />
        </ww:form>
      </div>
    </redback:ifAuthorized>
    </redback:isNotReadOnlyUserManager>

    <div class="task showRoles">
      <ww:form action="userlist!show" namespace="/security" theme="simple" method="get">
        <ww:submit cssClass="button" value="%{getText('user.list.show.users.in.role')}" />
        
        <ww:select list="roles"
                   name="roleName"
                   value="roleName"
                   listKey="name"
                   listValue="name"
                   headerKey=""
                   headerValue="Any"/>
      </ww:form>
    </div>
    
  </td>
  
  <td valign="top" class="column">
    <table cellspacing="0" cellpadding="0" border="0" class="reports">
      <tr>
        <th><ww:text name="name"/></th>
        <th><ww:text name="user.list.types"/></th>
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
              src="<c:url value="/images/redback/table/${report.value.type}.gif"/>" /></a>
          </c:forEach>
          </td>
        </tr>
      </c:forEach>
    </table>
  </td>
</tr>

</table>

</body>
</ww:i18n>
</html>
