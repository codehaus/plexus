package org.codehaus.plexus.webdav;

import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;

/**
     * RepositoryRequest
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class RepositoryRequest
    extends HttpServletRequestWrapper
{
    private String repoUrlName;

    public RepositoryRequest( HttpServletRequest request, String repoUrlName )
    {
        super( request );

        this.repoUrlName = "";

        if ( repoUrlName != null )
        {
            this.repoUrlName = repoUrlName;
        }
    }

    /**
     * Adjust the path info value to remove reference to repoUrlName.
     * This is done to satisfy the needs of {@link it.could.webdav.DAVTransaction}
     */
    public String getPathInfo()
    {
        return repoUrlName;

//        String pathInfo = super.getPathInfo();
//
//        if ( pathInfo == null )
//        {
//            return "";
//        }
//
//        if ( ( pathInfo.length() > 1 ) && ( pathInfo.charAt( 0 ) == '/' ) )
//        {
//            pathInfo = pathInfo.substring( 1 );
//        }
//
//        if ( pathInfo.startsWith( repoUrlName ) )
//        {
//            pathInfo = pathInfo.substring( repoUrlName.length() );
//        }
//
//        return pathInfo;
    }

    public String getServletPath()
    {
        return super.getServletPath() + "/" + this.repoUrlName;
    }
}
