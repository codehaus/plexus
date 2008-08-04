package org.codehaus.plexus.resource.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.codehaus.plexus.resource.PlexusResource;

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
    public PlexusResource getResource( String name )
        throws ResourceNotFoundException
    {
        if ( name == null || name.length() == 0 )
        {
            throw new ResourceNotFoundException( "URLResourceLoader : No template name provided" );
        }

        Exception exception = null;

        for ( Iterator i = paths.iterator(); i.hasNext(); )
        {
            String path = (String) i.next();

            try
            {
                URL u = new URL( path + name );

                final InputStream inputStream = u.openStream();

                if ( inputStream != null )
                {
                    if ( getLogger().isDebugEnabled() )
                    {
                        getLogger().debug( "URLResourceLoader: Found '" + name + "' at '" + path + "'" );
                    }

                    // save this root for later re-use
                    templateRoots.put( name, path );

                    return new URLPlexusResource( u ){
                        private boolean useSuper;
                        public synchronized InputStream getInputStream() throws IOException
                        {
                            if ( !useSuper )
                            {
                                useSuper = true;
                                return inputStream;
                            }
                            return super.getInputStream();
                        }
                    };
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
}
