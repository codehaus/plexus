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
<ww:i18n name="org.codehaus.plexus.redback.xwork.default">
<head>
  <title><ww:text name="role.model.page.title"/></title>
</head>

<body>

<%@ include file="/WEB-INF/jsp/redback/include/rbacListNavigation.jsp" %>

<h2><ww:text name="role.model.section.title"/></h2>

	<p><ww:text name="role.model.message"/></p>

<%@ include file="/WEB-INF/jsp/redback/include/formValidationResults.jsp" %>

	<h4>${model.application}</h4>

	<h5><ww:text name="resources"/>:</h5>
    <ul>
      <ww:iterator id="resource" value="model.resources">
      <li>
        <ww:text name="role.model.id"/>: ${resource.id}<br/>
        <ww:text name="role.model.name"/>: ${resource.name}<br/>
        <ww:text name="role.model.permanent"/>: ${resource.permanent}<br/>
        <br/>
      </li>
      </ww:iterator>
    </ul>

	<h5><ww:text name="operations"/>:</h5>
    <ul>
      <ww:iterator id="operation" value="model.operations">
        <li>
	        <ww:text name="role.model.id"/>: ${operation.id}<br/>
        	<ww:text name="role.model.name"/>: ${operation.name}<br/>
        	<ww:text name="role.model.permanent"/>: ${operation.permanent}<br/>
        	<br/>
       	</li>
      </ww:iterator>
    </ul>
    
    <h5><ww:text name="roles"/>:</h5>
    <ul>
      <ww:iterator id="role" value="model.roles">        
        <li>
            <ww:text name="role.model.id"/>: ${role.id}<br/>
       		<ww:text name="role.model.name"/>: ${role.name}<br/>
       		<ww:text name="role.model.permanent"/>: ${role.permanent}<br/>
       		<ww:text name="role.model.assignable"/>: ${role.assignable}<br/>
       		<ww:text name="permissions"/>:
       		<ul>
       		  <ww:iterator id="permission" value="#role.permissions">
       		  	<li>
       		  	  <ww:text name="role.model.id"/>: ${permission.id}<br/>
       		  	  <ww:text name="role.model.name"/>: ${permission.name}<br/>
       		  	  <ww:text name="role.model.permanent"/>: ${permission.permanent}<br/>
       		  	  <ww:text name="role.model.operation.id"/>: ${permission.operation}<br/>
       		  	  <ww:text name="role.model.resource.id"/>: ${permission.resource}<br/>
       		  	</li>
       		  </ww:iterator>
       		</ul>
       		<ww:text name="role.model.child.roles"/>:
       		<ul>
       		  <ww:iterator id="childRole" value="#role.childRoles">
       		  	<li>
       		  	  <ww:text name="role.model.role.id"/>: ${childRole}<br/>
       		  	</li>
       		  </ww:iterator>
       		</ul> 
       		<ww:text name="role.model.parent.roles"/>:
       		<ul>
       		  <ww:iterator id="parentRole" value="#role.parentRoles">
       		  	<li>
       		  	  <ww:text name="role.model.role.id"/>: ${parentRole}<br/>
       		  	</li>
       		  </ww:iterator>
       		</ul> 
       	</li>
       	<br/>
      </ww:iterator>
    </ul>
    
    <h5><ww:text name="role.model.templates"/>:</h5>
    <ul>
        <ww:iterator id="template" value="model.templates">        
        <li>
            <ww:text name="role.model.id"/>: ${template.id}<br/>
       		<ww:text name="role.model.name.prefix"/>: ${template.namePrefix}<br/>
       		<ww:text name="role.model.permanent"/>: ${template.permanent}<br/>
       		<ww:text name="role.model.assignable"/>: ${template.assignable}<br/>
       		<ww:text name="role.model.delimeter"/>: ${template.delimiter}<br/>
       		<ww:text name="permissions"/>:
       		<ul>
       		  <ww:iterator id="permission" value="#template.permissions">
       		  	<li>
       		  	  <ww:text name="role.model.id"/>: ${permission.id}<br/>
       		  	  <ww:text name="role.model.name"/>: ${permission.name}<br/>
       		  	  <ww:text name="role.model.permanent"/>: ${permission.permanent}<br/>
       		  	  <ww:text name="role.model.operation.id"/>: ${permission.operation}<br/>
       		  	  <ww:text name="role.model.resource.id"/>: ${permission.resource}<br/>
       		  	</li>
       		  </ww:iterator>
       		</ul>
       		<ww:text name="role.model.child.roles"/>:
       		<ul>
       		  <ww:iterator id="childRole" value="#template.childRoles">
       		  	<li>
       		  	  <ww:text name="role.model.role.id"/>: ${childRole}<br/>
       		  	</li>
       		  </ww:iterator>
       		</ul> 
       		<ww:text name="role.model.parent.roles"/>:
       		<ul>
       		  <ww:iterator id="parentRole" value="#template.parentRoles">
       		  	<li>
       		  	  <ww:text name="role.model.role.id"/>: ${parentRole}<br/>
       		  	</li>
       		  </ww:iterator>
       		</ul> 
       		<ww:text name="role.model.child.templates"/>:
       		<ul>
       		  <ww:iterator id="childTemplate" value="#template.childTemplates">
       		  	<li>
       		  	  <ww:text name="role.model.template.id"/>: ${childTemplate}<br/>
       		  	</li>
       		  </ww:iterator>
       		</ul> 
       		<ww:text name="role.model.parent.templates"/>:
       		<ul>
       		  <ww:iterator id="parentTemplate" value="#template.parentTemplates">
       		  	<li>
       		  	  <ww:text name="role.model.template.id"/>: ${parentTemplate}<br/>
       		  	</li>
       		  </ww:iterator>
       		</ul> 
       	</li>
       	<br/>
      </ww:iterator>
    </ul>

</body>
</ww:i18n>
</html>
