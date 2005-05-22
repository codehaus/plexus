package org.codehaus.plexus.servlet;

import java.net.URL;

/**
 * Will only return null for resource requests.
 * 
 * @author Ben Walding
 * @version $Revision$
 */
public class NoResourceMockServletContext extends MockServletContext
{
    public URL getResource( String resourceName )
    {
        return null;
    }
}