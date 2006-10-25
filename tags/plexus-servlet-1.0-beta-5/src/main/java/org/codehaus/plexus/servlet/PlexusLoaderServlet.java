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
import org.codehaus.plexus.embed.Embedder;
import org.codehaus.plexus.embed.EmbedderException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.IOException;

/**
 * <p>PlexusLoaderServlet loads a Plexus {@link Embedder} for a web
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
    /**
     * Embedded Plexus.
     */
    private Embedder embedder;

    /**
     * Load Plexus into the ServletContext for PlexusServlets.
     *
     * @see javax.servlet.GenericServlet#init()
     */
    public void init()
        throws ServletException
    {
        super.init();

        log( "Initializing Plexus..." );
        String configName = getInitParameter( ServletContextUtils.PLEXUS_CONFIG_PARAM );

        try
        {
            embedder = ServletContextUtils.createContainer( getServletContext(),
                                                            configName );
        }
        catch ( EmbedderException e )
        {
            throw new ServletException( "Could not start the Plexus container.", e );
        }
        catch ( IOException e )
        {
            throw new ServletException( "Could not start the Plexus container.", e );
        }
        catch ( PlexusContainerException e )
        {
            throw new ServletException( "Could not start the Plexus container.", e );
        }

        log( "Plexus Initialized." );
    }

    /**
     * Shutdown plexus.
     *
     * @see javax.servlet.Servlet#destroy()
     */
    public void destroy()
    {
        log( "Shutting down Plexus..." );
        ServletContextUtils.destroyContainer( embedder, getServletContext() );
        log( "... Plexus shutdown. Goodbye" );

        super.destroy();
    }
}
