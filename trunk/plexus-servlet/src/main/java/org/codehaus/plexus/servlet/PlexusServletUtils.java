/*
 * $Id$
 */

package org.codehaus.plexus.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * A collection of static helper methods for code running within a Servlet
 * environment that needs to access an embedded Plexus container. Such code
 * can either extend {@link PlexusServlet} or invoke these static methods
 * directly.
 *
 * @author <a href="mhw@kremvax.net">Mark Wilkinson</a>
 * @version $Revision$
 */
public final class PlexusServletUtils {
    // prevent instantiation
    private PlexusServletUtils() {
    }

    /**
     * Get a reference to the Plexus container loaded into the
     * <code>ServletContext</code>, if one exists.
     *
     * @param sc The servlet context that Plexus is installed in.
     * @return a <code>PlexusContainer</code> object, or <code>null</code>
     * if none was registered in the servlet context.
     */
    public static PlexusContainer getPlexusContainer(ServletContext sc)  {
        return (PlexusContainer) sc.getAttribute( PlexusConstants.PLEXUS_KEY );
    }

    public static boolean hasComponent( ServletContext sc, String role )
        throws ServletException
    {
        return getPlexusContainer( sc ).hasComponent( role );
    }

    public static boolean hasComponent( ServletContext sc, String role, String id )
        throws ServletException
    {
        return getPlexusContainer( sc ).hasComponent( role, id );
    }

    public static Object lookup(ServletContext sc, String role)
        throws ServletException
    {
        try {
            return getPlexusContainer( sc ).lookup( role );
        } catch (ComponentLookupException e) {
            throw new ServletException("could not lookup service " + role, e);
        }
    }

    public static Object lookup(ServletContext sc, String role, String id)
        throws ServletException
    {
        try {
            return getPlexusContainer( sc ).lookup( role, id );
        } catch (ComponentLookupException e) {
            throw new ServletException("could not lookup service " + role, e);
        }
    }

    public static void release(ServletContext sc, Object service)
        throws ServletException
    {
        try
        {
            getPlexusContainer( sc ).release(service);
        }
        catch( Exception ex )
        {
            throw new ServletException( "Exception while releasing component", ex );
        }
    }
}
