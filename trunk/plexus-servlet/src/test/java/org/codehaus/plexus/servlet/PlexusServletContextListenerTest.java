package org.codehaus.plexus.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.avalon.framework.service.ServiceManager;

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
            ServiceManager sm = PlexusServletUtils.getServiceManager( sc );
            assertNotNull("pc", pc);
            assertNotNull("sm", sm);
        }

        pacl.contextDestroyed(sce);
        {
            PlexusContainer pc = PlexusServletUtils.getPlexusContainer( sc );
            ServiceManager sm = PlexusServletUtils.getServiceManager( sc );
            assertNull("pc", pc);
            assertNull("sm", sm);
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
        assertNotNull( PlexusServletUtils.getServiceManager( sc ) );
        assertNotNull( PlexusServletUtils.getPlexusContainer( sc ) );

        pacl.contextDestroyed( sce );
        assertNull( PlexusServletUtils.getServiceManager( sc ) );
        assertNull( PlexusServletUtils.getPlexusContainer( sc ) );
    }
}
