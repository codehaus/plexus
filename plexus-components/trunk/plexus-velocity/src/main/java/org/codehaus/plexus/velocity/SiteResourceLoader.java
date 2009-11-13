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

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.exception.ResourceNotFoundException;

import java.io.InputStream;
import java.io.File;
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

    private static File resourceDir;

    public static void setResource( String staticResource )
    {
        resource = staticResource;
        resourceDir = new File( resource ).getParentFile();
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
                File f = new File( resourceDir, name );
                if ( f.exists() )
                {
                    return new FileInputStream( f );
                }
                else
                {
                    return new FileInputStream( resource );
                }
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

