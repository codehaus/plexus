package org.codehaus.plexus.resource.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

/**
 * @plexus.component role-hint="url"
 */
public class URLResourceLoader
    extends AbstractResourceLoader
{
    /** @plexus.configuration */
    private String[] roots;

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

        for ( int i = 0; i < roots.length; i++ )
        {
            try
            {
                URL u = new URL( roots[i] + name );

                inputStream = u.openStream();

                if ( inputStream != null )
                {
                    if ( log.isDebugEnabled() )
                    {
                        log.debug( "URLResourceLoader: Found '" + name + "' at '" + roots[i] + "'" );
                    }

                    // save this root for later re-use
                    templateRoots.put( name, roots[i] );

                    break;
                }
            }
            catch ( IOException ioe )
            {
                if ( log.isDebugEnabled() )
                {
                    log.debug( "URLResourceLoader: Exception when looking for '" + name + "' at '" + roots[i] + "'",
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
