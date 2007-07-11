package org.codehaus.plexus.velocity;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

