package org.codehaus.plexus.servlet;

import javax.servlet.ServletContext;

import junit.framework.TestCase;

import org.codehaus.plexus.embed.Embedder;

/**
 * @author Ben Walding
 * @version $Revision$
 */
public class ServletContextUtilsTest extends TestCase
{
    public void testResolveConfig() throws Exception
    {
        ServletContext msc = new MockServletContext();

        Embedder embedder = (Embedder) ServletContextUtils.createContainer( msc, null );

        assertNotNull( "embedder != null", embedder );

        embedder.stop();
    }
}