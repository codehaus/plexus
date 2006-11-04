package org.codehaus.plexus.velocity;

import java.io.InputStream;

import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.exception.ResourceNotFoundException;

import org.apache.commons.collections.ExtendedProperties;

public class ContextClassLoaderResourceLoader
    extends ResourceLoader
{
    public void init( ExtendedProperties configuration)
    {
    }

    public synchronized InputStream getResourceStream( String name )
        throws ResourceNotFoundException
    {
        InputStream result = null;
        
        if (name == null || name.length() == 0)
        {
            throw new ResourceNotFoundException ("No template name provided");
        }
        
        try 
        {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            result= classLoader.getResourceAsStream( name );
        }
        catch( Exception fnfe )
        {
            throw new ResourceNotFoundException( fnfe.getMessage() );
        }
        
        return result;
    }
    
    public boolean isSourceModified(Resource resource)
    {
        return false;
    }

    public long getLastModified(Resource resource)
    {
        return 0;
    }
}

