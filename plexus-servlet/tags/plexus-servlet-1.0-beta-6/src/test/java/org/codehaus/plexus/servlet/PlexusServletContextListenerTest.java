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

import junit.framework.TestCase;
import org.codehaus.plexus.PlexusContainer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * @author Ben Walding
 * @version $Id$
 */
public class PlexusServletContextListenerTest
    extends TestCase
{
    public void testSimple()
    {
        ServletContext sc = new MockServletContext();

        PlexusServletContextListener pacl = new PlexusServletContextListener();
        ServletContextEvent sce = new ServletContextEvent( sc );

        pacl.contextInitialized( sce );
        assertNotNull( PlexusServletUtils.getPlexusContainer( sc ) );

        pacl.contextDestroyed( sce );
        assertNull( PlexusServletUtils.getPlexusContainer( sc ) );
    }

    /**
     * Test the static methods.
     */
    public void testStaticMethods()
    {
        ServletContext sc = new MockServletContext();
        PlexusServletContextListener pacl = new PlexusServletContextListener();
        ServletContextEvent sce = new ServletContextEvent( sc );

        pacl.contextInitialized( sce );
        assertNotNull( PlexusServletUtils.getPlexusContainer( sc ) );

        pacl.contextDestroyed( sce );
        assertNull( PlexusServletUtils.getPlexusContainer( sc ) );
    }
}
