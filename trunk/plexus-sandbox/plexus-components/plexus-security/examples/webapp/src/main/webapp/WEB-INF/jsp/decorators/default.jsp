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
    body {
      background-color: #ccfdfd;
    }
    #breadcrumbs {
      background-color: #ddddff;
      margin: 0px;
      padding-top 3px;
    }
    
    #breadcrumbs .xright {
      float:right;
    }
    
    #nestedContent {
      margin: 0px 30px 30px 30px;
      padding: 5px;
      border: 1px black solid;
      background-color: white;
    }
    
    .note {
      margin: 30px 0px 0px 30px;
      padding-bottom: 0px;
      font-size: 80%;
      color: gray;
      font-style: italic;
    }
    
    .errorMessage,
    .errorLabel {
      color: red;
    }
    
    .required {
      color: red;
    }
  </style>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
</head>

<body onload="<decorator:getProperty property="body.onload" />" class="composite">

<div id="banner">
  <h1>Plexus Security Example Webapp</h1>
  <div class="clear">
    <hr/>
  </div>
</div>

<div id="breadcrumbs">
  
  <div class="xright">
    <a href="http://www.codehaus.org/">Codehaus</a> |
    <a href="http://plexus.codehaus.org/">Plexus</a> 
  </div>

  <div class="xleft">
    <ww:url id="loginUrl" action="login" method="input" namespace="/" includeParams="none"/>
    <ww:url id="registerUrl" action="register" method="input" namespace="/" includeParams="none"/>

    <ww:if test="${sessionScope.authStatus != true}">
      <ww:a href="%{loginUrl}">Login</ww:a> - <ww:a href="%{registerUrl}">Register</ww:a>
    </ww:if>
    <ww:else>
      <ww:url id="logoutUrl" action="logout" namespace="/" includeParams="none"/>
      Welcome, <b>${sessionScope.SecuritySessionUser.username}</b> -
      <ww:a href="%{logoutUrl}">Logout</ww:a>
    </ww:else>
  </div>
  
  <div class="clear">
    <hr/>
  </div>
</div>

  <p class="note">Everything within this white box is the actual jsp content.</p>
  <div id="nestedContent">
    <decorator:body/>
  </div>

<div class="clear">
  <hr/>
</div>

<div id="footer">
  <div class="xright">&#169; 2006 Codehaus.org </div>

  <div class="clear">
    <hr/>

  </div>
</div>
</body>
</html>
