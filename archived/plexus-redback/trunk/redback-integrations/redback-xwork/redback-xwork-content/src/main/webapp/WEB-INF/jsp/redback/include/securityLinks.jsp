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

<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://plexus.codehaus.org/redback/taglib-1.0" prefix="redback" %>

<span class="securityLinks">

<c:choose>
  <c:when test="${sessionScope.securitySession.authenticated != true}">
    <ww:url id="loginUrl" action="login" namespace="/security" includeParams="none"/>  
    <ww:url id="registerUrl" action="register" namespace="/security" includeParams="none"/>
    <ww:a href="%{loginUrl}"><ww:text name="login"/></ww:a><redback:isNotReadOnlyUserManager> - <ww:a href="%{registerUrl}"><ww:text name="register"/></ww:a></redback:isNotReadOnlyUserManager>
  </c:when>
  <c:otherwise>
    <ww:url id="logoutUrl" action="logout" namespace="/security" includeParams="none"/>
    <ww:url id="accountUrl" action="account" namespace="/security" includeParams="none" />
    
    <ww:text name="current.user"/>
    <c:choose>
      <c:when test="${sessionScope.securitySession.user != null}">
        <span class="fullname"><ww:a href="%{accountUrl}" cssClass="edit">${sessionScope.securitySession.user.fullName}</ww:a></span>
        (<span class="username">${sessionScope.securitySession.user.username}</span>)
      </c:when>
      <c:otherwise>
        <span class="fullname"><ww:text name="unknown.user"/></span>
      </c:otherwise>
    </c:choose>
    
    <redback:isNotReadOnlyUserManager>
    - <ww:a href="%{accountUrl}" cssClass="edit"><ww:text name="edit.details"/></ww:a>
    </redback:isNotReadOnlyUserManager>
    - <ww:a href="%{logoutUrl}" cssClass="logout"><ww:text name="logout"/></ww:a>
  </c:otherwise>
</c:choose>

</span>
