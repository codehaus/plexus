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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="/webwork" prefix="ww" %>

<html>
  <head>
    <title>
      <decorator:title/>
    </title>
    <link rel="stylesheet" type="text/css" href="<ww:url value="/css/tigris.css"/>" media="screen"/>
    <link rel="stylesheet" type="text/css" href="<ww:url value="/css/print.css"/>" media="print"/>
    <decorator:head/>
  </head>
  <body marginwidth="0" marginheight="0" class="composite">
    <%@ include file="/WEB-INF/jsp/navigation/DefaultTop.jsp" %>
    <table id="main" border="0" cellpadding="4" cellspacing="0" width="100%">
      <tbody>
        <tr valign="top">
          <td id="leftcol" width="180">
            <br/> <br/>
            <%@ include file="/WEB-INF/jsp/navigation/Menu.jsp" %>
          </td>
          <td width="86%">
          <br/>
            <div id="bodycol">
              <div class="app">
                <decorator:body/>
              </div>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
    <!-- TODO bottom -->
  </body>
</html>  
  