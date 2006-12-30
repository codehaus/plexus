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
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.embed.Embedder;
import org.codehaus.plexus.embed.EmbedderException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

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
    private ServletContextUtils utils = new ServletContextUtils();

    public void contextInitialized( ServletContextEvent sce )
    {
        try
        {
            utils.start( sce.getServletContext() );
        }
        catch ( PlexusContainerException e )
        {
            throw new RuntimeException( "Error while starting the Plexus container.", e );
        }
    }

    public void contextDestroyed( ServletContextEvent sce )
    {
        utils.stop();
    }
}
