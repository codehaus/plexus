package org.codehaus.plexus.webdav;

import org.codehaus.plexus.servlet.PlexusServletUtils;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.File;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusWebDavServlet
    implements Servlet
{
    private ServletConfig config;

    private DavProxy proxy;

    private SimpleRepositoryMapper repositoryMapper;

    // -----------------------------------------------------------------------
    // Overridables
    // -----------------------------------------------------------------------

    protected DavProxy lookupDavProxy( ServletConfig config )
        throws ServletException
    {
        return (DavProxy) PlexusServletUtils.lookup( config.getServletContext(), DavProxy.ROLE );
    }

    protected void releaseDavProxy( DavProxy proxy )
        throws ServletException
    {
        PlexusServletUtils.release( config.getServletContext(), proxy );
    }

    protected RepositoryMapper getRepositoryMapper()
        throws ServletException
    {
        if ( repositoryMapper != null )
        {
            return repositoryMapper;
        }

        String root = getServletConfig().getInitParameter( "dav.root" );

        if ( root == null )
        {
            throw new ServletException( "Missing init-parameter 'dav.root'." );
        }

        File f = new File( root );

        if ( !f.isDirectory() )
        {
            throw new ServletException( "Invalid configuration, the specified dav root is not a directory: '" + f.getAbsolutePath() + "'." );
        }

        return repositoryMapper = new SimpleRepositoryMapper( f );
    }

    // -----------------------------------------------------------------------
    // Servlet Implementation
    // -----------------------------------------------------------------------

    public void init( ServletConfig config )
        throws ServletException
    {
        this.config = config;

        proxy = lookupDavProxy( config );
    }

    public void destroy()
    {
        try
        {
            releaseDavProxy( proxy );
        }
        catch ( ServletException e )
        {
            config.getServletContext().log( "Error while releasing platform component.", e );
        }
    }

    public ServletConfig getServletConfig()
    {
        return config;
    }

    public String getServletInfo()
    {
        return "Plexus WebDav Servlet";
    }

    public void service( ServletRequest req, ServletResponse res )
        throws ServletException, IOException
    {
        if ( !( req instanceof HttpServletRequest ) )
        {
            throw new ServletException( "PlexusComponentServlet can only handle HttpServletRequests." );
        }

        if ( !( res instanceof HttpServletResponse ) )
        {
            throw new ServletException( "PlexusComponentServlet can only handle HttpServletResponse." );
        }

        proxy.service( (HttpServletRequest) req, (HttpServletResponse) res, getRepositoryMapper() );
    }
}
