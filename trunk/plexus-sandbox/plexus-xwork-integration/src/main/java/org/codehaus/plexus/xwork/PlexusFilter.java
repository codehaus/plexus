package org.codehaus.plexus.xwork;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.plexus.PlexusContainer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;

/**
 * @author Patrick Lightbody (plightbo at gmail dot com)
 * @author Emmanuel Venisse (evenisse at apache dot org)
 */
public class PlexusFilter
    implements Filter
{
    private static final Log log = LogFactory.getLog( PlexusFilter.class );
    private static final String CHILD_CONTAINER_NAME = "request";

    public static boolean loaded = false;

    private ServletContext ctx;

    public void init( FilterConfig filterConfig )
        throws ServletException
    {
        ctx = filterConfig.getServletContext();
        loaded = true;
    }

    public void doFilter( ServletRequest req, ServletResponse res, FilterChain chain )
        throws IOException, ServletException
    {
        PlexusContainer child = null;

        try
        {
            try
            {
                HttpServletRequest request = (HttpServletRequest) req;
                HttpSession session = request.getSession( false );
                PlexusContainer parent;

                if ( session != null )
                {
                    if ( log.isDebugEnabled() )
                    {
                        log.debug( "Loading parent Plexus container from Session" );
                    }
                    parent = (PlexusContainer) session.getAttribute( PlexusLifecycleListener.KEY );
                    if ( parent == null )
                    {
                        throw new ServletException( "Parent Plexus container is not defined in Session" );
                    }
                }
                else
                {
                    if ( log.isDebugEnabled() )
                    {
                        log.debug( "Loading parent Plexus container from Servlet Context" );
                    }
                    parent = (PlexusContainer) ctx.getAttribute( PlexusLifecycleListener.KEY );
                    if ( parent == null )
                    {
                        throw new ServletException( "Parent Plexus container is not defined in Servlet Context" );
                    }
                }

                if ( parent.hasChildContainer( CHILD_CONTAINER_NAME ) )
                {
                    log.warn( "Plexus container (scope: request) alredy exist." );
                    child = parent.getChildContainer( CHILD_CONTAINER_NAME );
                }
                else
                {
                    child = parent.createChildContainer( CHILD_CONTAINER_NAME, Collections.EMPTY_LIST, Collections.EMPTY_MAP );
                    PlexusUtils.configure( child, "plexus-request.xml" );
                    child.initialize();
                    child.start();
                }
                PlexusThreadLocal.setPlexusContainer( child );
            }
            catch ( Exception e )
            {
                log.error( "Error initializing plexus container (scope: request)", e );
            }

            chain.doFilter( req, res );
        }
        finally
        {
            try
            {
                if ( child != null )
                {
                    child.dispose();
                }

                PlexusThreadLocal.setPlexusContainer( null );
            }
            catch ( Exception e )
            {
                log.error( "Error disposing plexus container (scope: request)", e );
            }
        }
    }

    public void destroy()
    {
    }
}
