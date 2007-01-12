package org.codehaus.plexus.webdav.servlet.multiplexed;

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

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.webdav.servlet.DavServerRequest;
import org.codehaus.plexus.webdav.util.WrappedRepositoryRequest;

/**
 * <p>
 * MultiplexedDavServerRequest - For requests that contain the server prefix information within the requested 
 * servlet's pathInfo parameter (as the first path entry).
 * </p> 
 * 
 * <p>
 * You would use this dav server request object when you are working with a single servlet that is handling
 * multiple dav server components.
 * </p>
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class MultiplexedDavServerRequest
    implements DavServerRequest
{
    private WrappedRepositoryRequest request;

    private String prefix;

    private String logicalResource;

    public MultiplexedDavServerRequest( WrappedRepositoryRequest request )
    {
        /* Perform a simple security normalization of the requested pathinfo.
         * This is to prevent requests for information outside of the root directory.
         */
        String requestPathInfo = FileUtils.normalize( request.getPathInfo() );

        // Remove prefixing slash as the repository id doesn't contain it;
        if ( requestPathInfo.startsWith( "/" ) )
        {
            requestPathInfo = requestPathInfo.substring( 1 );
        }

        // Find first element, if slash exists.
        int slash = requestPathInfo.indexOf( '/' );
        if ( slash > 0 )
        {
            // Filtered: "central/org/apache/maven/" -> "central"
            this.prefix = requestPathInfo.substring( 0, slash );

            this.logicalResource = requestPathInfo.substring( slash );

            if ( this.logicalResource.endsWith( "/.." ) )
            {
                this.logicalResource += "/";
            }

            if ( this.logicalResource == null )
            {
                this.logicalResource = "/";
            }
        }
        else
        {
            this.prefix = requestPathInfo;
            this.logicalResource = "/";
        }
        
        this.request = request;
        this.request.setPathInfo( logicalResource );
    }

    public String getLogicalResource()
    {
        return this.logicalResource;
    }

    public String getPrefix()
    {
        return this.prefix;
    }

    public WrappedRepositoryRequest getRequest()
    {
        return request;
    }
}
