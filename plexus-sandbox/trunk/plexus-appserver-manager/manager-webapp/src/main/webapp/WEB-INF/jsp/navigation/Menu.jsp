<!--
/*
 * Copyright 2007 The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
-->

<%@ taglib uri="/webwork" prefix="ww" %>

<ww:i18n name="i18n.AppServerManager">
  <div id="navcolumn">
    <div id="management" class="toolgroup">
      <div class="label"><ww:text name="menu.management"/></div>
      <div>
        <div class="body">
          <ww:url id="listAppUrl" action="listApplications" namespace="/" includeParams="none"/>
          <ww:a href="%{listAppUrl}">
            <ww:text name="menu.management.list"/>
          </ww:a>
        </div>
      </div>
      <div class="body">
        <ww:url id="addAppUrl" action="displayAddApplication" method="input" namespace="/" includeParams="none"/>
          <ww:a href="%{addAppUrl}">
            <ww:text name="menu.management.add"/>
          </ww:a>
        </div>
      </div>      
    </div>  
    <div id="aboutmenu" class="toolgroup">
      <div class="label"><ww:text name="menu.appservermanager"/></div>
      <div>
        <div class="body">
          <ww:url id="aboutUrl" action="about" namespace="/" includeParams="none"/>
          <ww:a href="%{aboutUrl}">
            <ww:text name="menu.about"/>
          </ww:a>
        </div>
      </div>
    </div>
  </div>
</ww:i18n>
