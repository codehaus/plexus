package org.codehaus.plexus.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.codehaus.plexus.PlexusContainer;

import junit.framework.TestCase;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class PlexusServletContextListenerTest extends TestCase
{
    public void testSimple()
    {
        ServletContext sc = new MockServletContext();

        PlexusServletContextListener pacl = new PlexusServletContextListener();
        ServletContextEvent sce = new ServletContextEvent(sc);

        pacl.contextInitialized(sce);
        {
            PlexusContainer pc = PlexusServletUtils.getPlexusContainer( sc );
            assertNotNull("pc", pc);
        }

        pacl.contextDestroyed(sce);
        {
            PlexusContainer pc = PlexusServletUtils.getPlexusContainer( sc );
            assertNull("pc", pc);
        }
    }

    /**
     * Test the static methods.
     */
    public void testStaticMethods()
    {
        ServletContext sc = new MockServletContext();
        PlexusServletContextListener pacl = new PlexusServletContextListener();
        ServletContextEvent sce = new ServletContextEvent(sc);

        pacl.contextInitialized( sce );
        assertNotNull( PlexusServletUtils.getPlexusContainer( sc ) );

        pacl.contextDestroyed( sce );
        assertNull( PlexusServletUtils.getPlexusContainer( sc ) );
    }
}
