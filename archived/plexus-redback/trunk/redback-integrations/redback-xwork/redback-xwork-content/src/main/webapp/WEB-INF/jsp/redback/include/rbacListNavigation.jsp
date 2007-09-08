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

<div class="rbac-navigation-bar">

<ww:url id="rolesUrl" action="roles" namespace="/security" method="list"/>
<ww:url id="permissionsUrl" action="permissions" namespace="/security" method="list"/>
<ww:url id="operationsUrl" action="operations" namespace="/security" method="list"/>
<ww:url id="resourcesUrl" action="resources" namespace="/security" method="list"/>

<ww:a href="%{rolesUrl}"><ww:text name="roles"/></ww:a> | 
<ww:a href="%{permissionsUrl}"><ww:text name="permissions"/></ww:a> | 
<ww:a href="%{operationsUrl}"><ww:text name="operations"/></ww:a> | 
<ww:a href="%{resourcesUrl}"><ww:text name="resources"/></ww:a> 

</div>
