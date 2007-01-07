package org.codehaus.plexus.webdav;

import org.codehaus.plexus.util.FileUtils;

import javax.servlet.http.HttpServletRequest;

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
    private HttpServletRequest request;

    private String prefix;

    private String logicalResource;

    public MultiplexedDavServerRequest( HttpServletRequest request )
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
        
        this.request = new RepositoryRequest( request, this.logicalResource );
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
