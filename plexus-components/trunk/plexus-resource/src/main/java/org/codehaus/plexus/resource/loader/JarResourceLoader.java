package org.codehaus.plexus.resource.loader;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * @author Jason van Zyl
 * @plexus.component role-hint="jar"
 */
public class JarResourceLoader
    extends AbstractResourceLoader
{
    /** @plexus.configuration */
    private List paths;

    /**
     * Maps entries to the parent JAR File
     * Key = the entry *excluding* plain directories
     * Value = the JAR URL
     */
    private Map entryDirectory = new HashMap( 559 );

    /**
     * Maps JAR URLs to the actual JAR
     * Key = the JAR URL
     * Value = the JAR
     */
    private Map jarfiles = new HashMap( 89 );

    public void initialize()
        throws InitializationException
    {
        if ( paths != null )
        {
            for ( int i = 0; i < paths.size(); i++ )
            {
                loadJar( (String) paths.get( i ) );
            }
        }
    }

    private void loadJar( String path )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug( "JarResourceLoader : trying to load \"" + path + "\"" );
        }

        // Check path information
        if ( path == null )
        {
            log.error( "JarResourceLoader : can not load JAR - JAR path is null" );
        }
        if ( !path.startsWith( "jar:" ) )
        {
            log.error( "JarResourceLoader : JAR path must start with jar: -> " +
                "see java.net.JarURLConnection for information" );
        }
        if ( !path.endsWith( "!/" ) )
        {
            path += "!/";
        }

        // Close the jar if it's already open
        // this is useful for a reload
        closeJar( path );

        // Create a new JarHolder
        JarHolder temp = new JarHolder( path );

        // Add it's entries to the entryCollection
        addEntries( temp.getEntries() );

        // Add it to the Jar table
        jarfiles.put( temp.getUrlPath(), temp );
    }

    /**
     * Closes a Jar file and set its URLConnection
     * to null.
     */
    private void closeJar( String path )
    {
        if ( jarfiles.containsKey( path ) )
        {
            JarHolder theJar = (JarHolder) jarfiles.get( path );

            theJar.close();
        }
    }

    /**
     * Copy all the entries into the entryDirectory
     * It will overwrite any duplicate keys.
     */
    private void addEntries( Hashtable entries )
    {
        entryDirectory.putAll( entries );
    }

    /**
     * Get an InputStream so that the Runtime can build a
     * template with it.
     *
     * @param source name of template to get
     * @return InputStream containing the template
     * @throws ResourceNotFoundException if template not found
     *                                   in the file template path.
     */
    public InputStream getResourceAsInputStream( String source)
        throws ResourceNotFoundException
    {
        InputStream results = null;

        if ( source == null || source.length() == 0 )
        {
            throw new ResourceNotFoundException( "Need to have a resource!" );
        }

        if ( source == null || source.length() == 0 )
        {
            String msg = "JAR resource error : argument " + source +
                " contains .. and may be trying to access " + "content outside of template root.  Rejected.";

            log.error( "JarResourceLoader : " + msg );

            throw new ResourceNotFoundException( msg );
        }

        /*
        *  if a / leads off, then just nip that :)
        */
        if ( source.startsWith( "/" ) )
        {
            source = source.substring( 1 );
        }

        if ( entryDirectory.containsKey( source ) )
        {
            String jarurl = (String) entryDirectory.get( source );

            if ( jarfiles.containsKey( jarurl ) )
            {
                JarHolder holder = (JarHolder) jarfiles.get( jarurl );

                results = holder.getResource( source );
                
                return results;
            }
        }

        throw new ResourceNotFoundException( "JarResourceLoader Error: cannot find resource " + source );
    }
}
