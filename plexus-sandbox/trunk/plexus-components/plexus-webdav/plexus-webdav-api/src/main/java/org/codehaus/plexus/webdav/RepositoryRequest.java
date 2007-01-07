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

import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;

/**
 * RepositoryRequest - wrapped servlet request to eliminate the prefix from the
 * pathInfo portion of the URL requested. (only used in {@link MultiplexedWebDavServlet}).
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class RepositoryRequest
    extends HttpServletRequestWrapper
{
    private String pathInfo;

    public RepositoryRequest( HttpServletRequest request, String adjustedPathInfo )
    {
        super( request );

        this.pathInfo = "";

        if ( adjustedPathInfo != null )
        {
            this.pathInfo = adjustedPathInfo;
        }
    }

    /**
     * Adjust the path info value to remove reference to prefix.
     * This is done to satisfy the needs of {@link it.could.webdav.DAVTransaction}
     */
    public String getPathInfo()
    {
        return pathInfo;
    }

    public String getServletPath()
    {
        return super.getServletPath() + "/" + this.pathInfo;
    }
}
