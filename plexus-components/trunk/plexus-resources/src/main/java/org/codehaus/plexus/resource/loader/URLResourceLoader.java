package org.codehaus.plexus.resource.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Jason van Zyl
 * @plexus.component role-hint="url"
 */
public class URLResourceLoader
    extends AbstractResourceLoader
{
    protected HashMap templateRoots = new HashMap();

    /**
     * Get an InputStream so that the Runtime can build a
     * template with it.
     *
     * @param name name of template to fetch bytestream of
     * @return InputStream containing the template
     * @throws ResourceNotFoundException if template not found
     *                                   in the file template path.
     */
    public InputStream getResourceAsInputStream( String name )
        throws ResourceNotFoundException
    {
        if ( name == null || name.length() == 0 )
        {
            throw new ResourceNotFoundException( "URLResourceLoader : No template name provided" );
        }

        InputStream inputStream = null;

        Exception exception = null;

        for ( Iterator i = paths.iterator(); i.hasNext(); )
        {
            String path = (String) i.next();

            try
            {
                URL u = new URL( path + name );

                inputStream = u.openStream();

                if ( inputStream != null )
                {
                    if ( getLogger().isDebugEnabled() )
                    {
                        getLogger().debug( "URLResourceLoader: Found '" + name + "' at '" + path + "'" );
                    }

                    // save this root for later re-use
                    templateRoots.put( name, path );

                    break;
                }
            }
            catch ( IOException ioe )
            {
                if ( getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "URLResourceLoader: Exception when looking for '" + name + "' at '" + path + "'",
                               ioe );
                }

                // only save the first one for later throwing
                if ( exception == null )
                {
                    exception = ioe;
                }
            }
        }

        // if we never found the template
        if ( inputStream == null )
        {
            String msg;
            if ( exception == null )
            {
                msg = "URLResourceLoader : Resource '" + name + "' not found.";
            }
            else
            {
                msg = exception.getMessage();
            }
            // convert to a general Velocity ResourceNotFoundException
            throw new ResourceNotFoundException( msg );
        }

        return inputStream;
    }
}
