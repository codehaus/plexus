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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Extension of {@link HttpServlet} that provides convenience methods for
 * servlets that need to access services provided by a Plexus container
 * embedded in a Servlet context. The Plexus container can be created using
 * either {@link PlexusLoaderServlet} or {@link PlexusServletContextListener}
 * as part of a web application's configuration. Alternatively the servlet
 * container may be a Plexus component, as with the Plexus jetty component.
 *
 * @author <a href="mhw@kremvax.net">Mark Wilkinson</a>
 * @version $Revision$
 */
public abstract class PlexusServlet
    extends HttpServlet
{
    public final boolean hasComponent( String role )
        throws ServletException
    {
        return PlexusServletUtils.hasComponent( getServletContext(), role );
    }

    public final boolean hasComponent( String role, String id )
        throws ServletException
    {
        return PlexusServletUtils.hasComponent( getServletContext(), role, id );
    }

    public final Object lookup( String role )
        throws ServletException
    {
        return PlexusServletUtils.lookup( getServletContext(), role );
    }

    public final Object lookup( String role, String id )
        throws ServletException
    {
        return PlexusServletUtils.lookup( getServletContext(), role, id );
    }

    public final void release( Object service )
        throws ServletException
    {
        PlexusServletUtils.release( getServletContext(), service );
    }
}
