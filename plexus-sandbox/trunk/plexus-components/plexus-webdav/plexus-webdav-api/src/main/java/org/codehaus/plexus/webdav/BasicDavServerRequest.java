package org.codehaus.plexus.webdav;

/*
 * Copyright 2001-2007 The Codehaus.
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

import javax.servlet.http.HttpServletRequest;

/**
 * BasicDavServerRequest - for requests that have a prefix based off of the servlet path id.
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class BasicDavServerRequest
    implements DavServerRequest
{
    private HttpServletRequest request;

    private String prefix;

    private String logicalResource;

    public BasicDavServerRequest( HttpServletRequest request )
    {
        this.request = request;
        this.prefix = request.getServletPath();
        this.logicalResource = request.getPathInfo();
    }

    public String getLogicalResource()
    {
        return this.logicalResource;
    }

    public String getPrefix()
    {
        return this.prefix;
    }

    public HttpServletRequest getRequest()
    {
        return request;
    }
}
