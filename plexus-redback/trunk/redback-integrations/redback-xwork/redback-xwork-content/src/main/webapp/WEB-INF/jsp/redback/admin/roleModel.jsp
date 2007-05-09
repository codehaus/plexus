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
<%@ taglib prefix="redback" uri="http://plexus.codehaus.org/redback/taglib-1.0"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
  <title>[Admin] Role Model</title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/redback/include/rbacListNavigation.jsp" %>

<h2> [Admin] Role Model </h2>

	<p>These are the resources, operations, roles and role templates that are known by the role manager. They are not the current content of the RBAC store as those contains the resource, operations, and roles declared below _and_ any dynamic data created from creating new roles from the templates below.</p>

<%@ include file="/WEB-INF/jsp/redback/include/formValidationResults.jsp" %>

	<h4>${model.application}</h4>

	<h5>Resources:</h5>
    <ul>
      <ww:iterator id="resource" value="model.resources">
      <li>
        Id: ${resource.id}<br/>
        Name: ${resource.name}<br/>
        Permanent: ${resource.permanent}<br/>
        <br/>
      </li>
      </ww:iterator>
    </ul>

	<h5>Operations:</h5>
    <ul>
      <ww:iterator id="operation" value="model.operations">
        <li>
	        Id: ${operation.id}<br/>
        	Name: ${operation.name}<br/>
        	Permanent: ${operation.permanent}<br/>
        	<br/>
       	</li>
      </ww:iterator>
    </ul>
    
    <h5>Roles:</h5>
    <ul>
      <ww:iterator id="role" value="model.roles">        
        <li>
            Id: ${role.id}<br/>
       		Name: ${role.name}<br/>
       		Permanent: ${role.permanent}<br/>
       		Assignable: ${role.assignable}<br/>
       		Permissions:
       		<ul>
       		  <ww:iterator id="permission" value="#role.permissions">
       		  	<li>
       		  	  Id: ${permission.id}<br/>
       		  	  Name: ${permission.name}<br/>
       		  	  Permanent: ${permission.permanent}<br/>
       		  	  Operation Id: ${permission.operation}<br/>
       		  	  Resource Id: ${permission.resource}<br/>
       		  	</li>
       		  </ww:iterator>
       		</ul>
       		Child Roles:
       		<ul>
       		  <ww:iterator id="childRole" value="#role.childRoles">
       		  	<li>
       		  	  Role Id: ${childRole}<br/>
       		  	</li>
       		  </ww:iterator>
       		</ul> 
       		Parent Roles:
       		<ul>
       		  <ww:iterator id="parentRole" value="#role.parentRoles">
       		  	<li>
       		  	  Role Id: ${parentRole}<br/>
       		  	</li>
       		  </ww:iterator>
       		</ul> 
       	</li>
       	<br/>
      </ww:iterator>
    </ul>
    
    <h5>Templates:</h5>
    <ul>
        <ww:iterator id="template" value="model.templates">        
        <li>
            Id: ${template.id}<br/>
       		Name Prefix: ${template.namePrefix}<br/>
       		Permanent: ${template.permanent}<br/>
       		Assignable: ${template.assignable}<br/>
       		Delimiter: ${template.delimiter}<br/>
       		Permissions:
       		<ul>
       		  <ww:iterator id="permission" value="#template.permissions">
       		  	<li>
       		  	  Id: ${permission.id}<br/>
       		  	  Name: ${permission.name}<br/>
       		  	  Permanent: ${permission.permanent}<br/>
       		  	  Operation Id: ${permission.operation}<br/>
       		  	  Resource Id: ${permission.resource}<br/>
       		  	</li>
       		  </ww:iterator>
       		</ul>
       		Child Roles:
       		<ul>
       		  <ww:iterator id="childRole" value="#template.childRoles">
       		  	<li>
       		  	  Role Id: ${childRole}<br/>
       		  	</li>
       		  </ww:iterator>
       		</ul> 
       		Parent Roles:
       		<ul>
       		  <ww:iterator id="parentRole" value="#template.parentRoles">
       		  	<li>
       		  	  Role Id: ${parentRole}<br/>
       		  	</li>
       		  </ww:iterator>
       		</ul> 
       		Child Templates:
       		<ul>
       		  <ww:iterator id="childTemplate" value="#template.childTemplates">
       		  	<li>
       		  	  Template Id: ${childTemplate}<br/>
       		  	</li>
       		  </ww:iterator>
       		</ul> 
       		Parent Templates:
       		<ul>
       		  <ww:iterator id="parentTemplate" value="#template.parentTemplates">
       		  	<li>
       		  	  Template Id: ${parentTemplate}<br/>
       		  	</li>
       		  </ww:iterator>
       		</ul> 
       	</li>
       	<br/>
      </ww:iterator>
    </ul>

  </body>
</html>
