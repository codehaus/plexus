package org.codehaus.plexus.servlet;

/**
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * A collection of static helper methods for code running within a Servlet
 * environment that needs to access an embedded Plexus container. Such code
 * can either extend {@link PlexusServlet} or invoke these static methods
 * directly.
 *
 * @author <a href="mhw@kremvax.net">Mark Wilkinson</a>
 * @version $Revision$
 */
public final class PlexusServletUtils
{
    // prevent instantiation
    private PlexusServletUtils()
    {
    }

    /**
     * Get a reference to the Plexus container loaded into the
     * <code>ServletContext</code>, if one exists.
     *
     * @param sc The servlet context that Plexus is installed in.
     * @return a <code>PlexusContainer</code> object, or <code>null</code>
     *         if none was registered in the servlet context.
     */
    public static PlexusContainer getPlexusContainer( ServletContext sc )
    {
        return (PlexusContainer) sc.getAttribute( PlexusConstants.PLEXUS_KEY );
    }

    public static boolean hasComponent( ServletContext sc, String role )
    {
        return getPlexusContainer( sc ).hasComponent( role );
    }

    public static boolean hasComponent( ServletContext sc, String role, String id )
    {
        return getPlexusContainer( sc ).hasComponent( role, id );
    }

    public static Object lookup( ServletContext sc, String role )
        throws ServletException
    {
        try
        {
            return getPlexusContainer( sc ).lookup( role );
        }
        catch ( ComponentLookupException e )
        {
            throw new ServletException( "could not lookup service " + role, e );
        }
    }

    public static Object lookup( ServletContext sc, String role, String id )
        throws ServletException
    {
        try
        {
            return getPlexusContainer( sc ).lookup( role, id );
        }
        catch ( ComponentLookupException e )
        {
            throw new ServletException( "could not lookup service " + role, e );
        }
    }

    public static void release( ServletContext sc, Object service )
        throws ServletException
    {
        try
        {
            getPlexusContainer( sc ).release( service );
        }
        catch ( Exception ex )
        {
            throw new ServletException( "Exception while releasing component", ex );
        }
    }
}
