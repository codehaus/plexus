package org.codehaus.plexus.velocity;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.exception.ResourceNotFoundException;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Alloww you to dynamically add resources that you want to be processed by Velocity. For
 * example if you want ot generate a site and pull in some random files to be interpolated
 * by Velocity you can use this resource loader.
 *
 * @author Jason van Zyl
 */
public class SiteResourceLoader
    extends ResourceLoader
{
    private static String resource;

    public static void setResource( String staticResource )
    {
        resource = staticResource;
    }

    public void init( ExtendedProperties p )
    {
    }

    public InputStream getResourceStream( String name )
        throws ResourceNotFoundException
    {
        if ( resource != null )
        {
            try
            {
                return new FileInputStream( resource );
            }
            catch ( FileNotFoundException e )
            {
                throw new ResourceNotFoundException( "Cannot find resource, make sure you set the right resource." );
            }
        }

        return null;
    }

    public boolean isSourceModified( org.apache.velocity.runtime.resource.Resource resource )
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public long getLastModified( org.apache.velocity.runtime.resource.Resource resource )
    {
        return 0;
    }
}

