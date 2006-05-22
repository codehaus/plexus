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
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.embed.Embedder;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;

/**
 * By adding this to the listeners for your web application, a Plexus container
 * will be instantiated and added to the attributes of the ServletContext.
 * <p/>
 * The interface that this class implements appeared in the Java Servlet
 * API 2.3. For compatability with Java Servlet API 2.2 and before use
 * {@link PlexusLoaderServlet}.
 *
 * @author <a href="bwalding@apache.org">Ben Walding</a>
 * @author <a href="mhw@kremvax.net">Mark Wilkinson</a>
 * @version $Id$
 * @see PlexusLoaderServlet
 */
public class PlexusServletContextListener
    implements ServletContextListener
{
    private Embedder embedder = null;

    public void contextInitialized( ServletContextEvent sce )
    {
        ServletContext context = sce.getServletContext();

        if ( context.getAttribute( PlexusConstants.PLEXUS_KEY ) != null )
        {
            context.log( "Plexus container already in context." );

            return;
        }

        String configName = context.getInitParameter( ServletContextUtils.PLEXUS_CONFIG_PARAM );

        context.log( "Initializing Plexus container..." );

        try
        {
            embedder = ServletContextUtils.createContainer( context, configName );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Could not start the Plexus container.", e );
        }
        catch ( PlexusContainerException e )
        {
            throw new RuntimeException( "Could not start the Plexus container.", e );
        }

        context.log( "Plexus container initialized." );
    }

    public void contextDestroyed( ServletContextEvent sce )
    {
        ServletContext context = sce.getServletContext();

        context.log( "Disposing of Plexus container." );

        ServletContextUtils.destroyContainer( embedder, context );
    }
}
