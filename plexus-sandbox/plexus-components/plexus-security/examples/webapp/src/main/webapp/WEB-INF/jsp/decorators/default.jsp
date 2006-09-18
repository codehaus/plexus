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

<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="pss" uri="plexusSecuritySystem" %>
<html>
<head>
  <title>Plexus Security Example Webapp ::
    <decorator:title default="Plexus Security Example Webapp"/>
  </title>

  <style type="text/css" media="all">
    @IMPORT url("/css/main.css");
  </style>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
</head>

<body onload="<decorator:getProperty property="body.onload" />" class="composite">

<div id="banner">
  <ww:url id="main" action="main" namespace="/" includeParams="none"/>
  <h1><ww:a href="%{main}">Plexus Security Example Webapp</ww:a></h1>
  <div class="clear">
  </div>
</div>

<div id="breadcrumbs">
  
  <div class="xright">
    <a href="http://www.codehaus.org/">Codehaus</a> |
    <a href="http://plexus.codehaus.org/">Plexus</a> 
  </div>

  <div class="xleft">
    <ww:url id="loginUrl" action="login" namespace="/security" includeParams="none"/>
    <ww:url id="registerUrl" action="register" namespace="/security" includeParams="none"/>

    <ww:if test="${sessionScope.securityAuth != true}">
      <ww:a href="%{loginUrl}">Login</ww:a> - <ww:a href="%{registerUrl}">Register</ww:a>
    </ww:if>
    <ww:else>
      <ww:url id="logoutUrl" action="logout" namespace="/security" includeParams="none"/>
      Welcome, 
      <c:choose>
        <c:when test="${sessionScope.securityUser != null}">
          <b>${sessionScope.securityUser.username}</b>
        </c:when>
        <c:otherwise>
          <b>Unknown User</b>
        </c:otherwise>
      </c:choose>
       -
      <ww:a href="%{logoutUrl}">Logout</ww:a>
    </ww:else>
  </div>
  
  <div class="clear">
  </div>
</div>

  <p class="note">The gray content is arriving via the /WEB-INF/jsp/decorators/default.jsp managed by sitemesh.<br/>
  Everything within the white box below is the actual jsp content.</p>
  <div id="nestedContent">
    <decorator:body/>
  </div>

  <div id="xworkinfo">
  
    <strong>request scope:</strong>
    <ul>
    <c:forEach var="rs" items="${requestScope}">
      <li>
        <em><c:out value="${rs.key}" /></em> : 
          <c:choose>
            <c:when test="${rs.value != null}">
              (<c:out value="${rs.value.class.name}" /> ) <br />
              &nbsp; &nbsp; &nbsp; <c:out value="${rs.value}" />
            </c:when>
            <c:otherwise>
              &lt;null&gt;
            </c:otherwise>
          </c:choose>
      </li>
    </c:forEach>
    </ul>
    
    <strong>session scope:</strong>
    <ul>
    <c:forEach var="ss" items="${sessionScope}">
      <li>
        <em><c:out value="${ss.key}" /></em> : 
          <c:choose>
            <c:when test="${ss.value != null}">
              (<c:out value="${ss.value.class.name}" /> ) <br />
              &nbsp; &nbsp; &nbsp; <c:out value="${ss.value}" />
            </c:when>
            <c:otherwise>
              &lt;null&gt;
            </c:otherwise>
          </c:choose>
      </li>
    </c:forEach>
    </ul>
    
    <%-- 
       Application Scope is empty.
       Could be an xwork/webwork specific thing.
    <strong>application scope:</strong>
    <ul>
    <c:forEach var="as" items="${applicationScope.keys}">
      <li>
        [<c:out value="${as}" /> ]
      </li>
    </c:forEach>
    </ul>
     --%>
     
  </div>

<div class="clear">
</div>

<div id="footer">
  <div class="xright">&#169; 2006 Codehaus.org </div>
  <div class="clear">
  </div>
</div>
</body>
</html>
