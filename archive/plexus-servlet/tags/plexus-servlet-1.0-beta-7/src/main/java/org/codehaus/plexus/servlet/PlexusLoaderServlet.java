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

import org.codehaus.plexus.PlexusContainerException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * <p>PlexusLoaderServlet loads a Plexus {@link org.codehaus.plexus.DefaultPlexusContainer} for a web
 * application.  The embedder is put into the
 * <code>ServletContext</code> so <code>PlexusServlet</code>s can retrieve it.
 * It is important to make sure that this class is loaded before the startup of
 * your web application that uses the Plexus <code>Embedder</code>.
 * Alternatively, the servlet container can be loaded as a component within a
 * Plexus manager.  In that case, this class is no longer needed.
 * </p>
 * <p/>
 * To configure this servlet you must specify the location of the plexus
 * configuration file relative to the root of your webapplication.  This must be
 * passed as the "plexus-config" init-parameter to the servlet.
 * </p>
 *
 * @author <a href="dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mhw@kremvax.net@>Mark Wilkinson</a>
 * @since Feb 2, 2003
 */
public class PlexusLoaderServlet
    extends HttpServlet
{
    private ServletContextUtils utils = new ServletContextUtils();

    /**
     * Load Plexus into the ServletContext for PlexusServlets.
     *
     * @see javax.servlet.GenericServlet#init()
     */
    public void init()
        throws ServletException
    {
        super.init();

        try
        {
            utils.start( getServletContext() );
        }
        catch ( PlexusContainerException e )
        {
            throw new ServletException( "Could not start the Plexus container.", e );
        }
    }

    /**
     * Shutdown plexus.
     *
     * @see javax.servlet.Servlet#destroy()
     */
    public void destroy()
    {
        utils.stop();

        super.destroy();
    }
}
