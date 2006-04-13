package org.codehaus.plexus.application.service;

/*
 * Copyright (c) 2004, Codehaus.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.File;
import java.io.FileReader;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.application.PlexusServiceConstants;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.Expand;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Jul 17, 2004
 */
public class DefaultServiceDiscoverer
    extends AbstractLogEnabled
    implements ServiceDiscoverer, Initializable, Contextualizable
{
    private String serviceDirectory;

    private DefaultPlexusContainer container;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        getLogger().info( "Services will be deployed in: '" + serviceDirectory + "'." );
    }

    public void contextualize( Context context )
        throws ContextException
    {
        container = (DefaultPlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    // ----------------------------------------------------------------------
    // ServiceDiscoverer Implementation
    // ----------------------------------------------------------------------

    public void deploy( String name, String location )
        throws Exception
    {
        File jar = new File( location );

        deploy( name, jar, new File( serviceDirectory ), new File( serviceDirectory ) );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------
/*
    private void deployServices( String directory )
        throws Exception
    {
        getLogger().info( "Extracting services in " + directory + "." );

        File serviceDir = new File( directory );

        if ( serviceDir.isDirectory() )
        {
            File[] apps = serviceDir.listFiles();

            for ( int i = 0; i < apps.length; i++ )
            {
                String fileName = apps[i].getName();
                if ( fileName.endsWith( ".jar" ) )
                {
                    String name = fileName.substring( 0, fileName.indexOf( ".jar" ) );
                    deploy( name,
                            new File( apps[i].getAbsolutePath() ),
                            new File( serviceDirectory ),
                            serviceDir );
                }
            }
        }
        else
        {
            getLogger().warn( "The services directory '" + directory + "' is not a directory!" );
        }
    }
*/
    private void deploy( String name, File jar, File services, File configurations )
        throws Exception
    {
        File serviceDir = new File( services, name );

        if ( serviceDir.exists() )
        {
            getLogger().info( "Removing old service." );

            FileUtils.deleteDirectory( serviceDir );
        }

        Expand ex = new Expand();
        ex.setDest( serviceDir );
        ex.setOverwrite( false );
        ex.setSrc( jar );

        try
        {
            getLogger().info( "Extracting service jar '" + jar + "' to '" + serviceDir + "'." );

            ex.execute();
        }
        catch ( Exception e )
        {
            getLogger().error( "Could not extract " + serviceDir + ".", e );
        }

        // ----------------------------------------------------------------------
        // Set up the classpath for the service
        // ----------------------------------------------------------------------

        File libDir = new File( serviceDir, "lib" );

        if ( !libDir.exists() )
        {
            throw new Exception( "The service must have a /lib directory." );
        }

        addJars( libDir );

        File classesDir = new File( serviceDir, "classes" );

        if ( !classesDir.exists() )
        {
            throw new Exception( "The service must have a /classesDir directory." );
        }

        addClasses( classesDir );

        // ----------------------------------------------------------------------
        // Discover any components in the service
        // ----------------------------------------------------------------------

        container.discoverComponents( container.getCoreRealm() );

        // Copy over the user configuration if there is one.
        File serviceConfig = new File( configurations, name + ".xml" );
        if ( !serviceConfig.exists() )
        {
            File config = new File( new File( serviceDir, PlexusServiceConstants.CONF_DIRECTORY ),
                                    PlexusServiceConstants.CONFIGURATION_FILE );

            if ( config.exists() )
            {
                config.renameTo( serviceConfig );

                addConfiguration( serviceConfig );
            }
        }
        else
        {
            addConfiguration( serviceConfig );
        }
    }

    /**
     * The first time the configuration runs, we don't care
     * what the user configuration is, so only use the components.xml
     * files.  However, we still need to load on start components.
     *
     * @throws Exception
     */
    private void addConfiguration( File config )
        throws Exception
    {
        PlexusConfiguration serviceConfig =
            PlexusTools.buildConfiguration( config.getPath(), new FileReader( config ) );

        startComponents( serviceConfig );
    }

    private void startComponents( PlexusConfiguration serviceConfig ) throws PlexusConfigurationException, ComponentLookupException
    {
        PlexusConfiguration[] loadOnStartComponents = serviceConfig.getChild( "load-on-start" ).getChildren( "component" );

        getLogger().debug( "Found " + loadOnStartComponents.length + " components to load on start" );

        for ( int i = 0; i < loadOnStartComponents.length; i++ )
        {
            String role = loadOnStartComponents[i].getChild( "role" ).getValue( null );

            String roleHint = loadOnStartComponents[i].getChild( "role-hint" ).getValue();

            if ( role == null )
            {
                throw new PlexusConfigurationException( "Missing 'role' element from load-on-start." );
            }

            if ( roleHint == null )
            {
                getLogger().info( "Loading on start [role]: " + "[" + role + "]" );
            }
            else
            {
                getLogger().info( "Loading on start [role,roleHint]: " + "[" + role + "," + roleHint + "]" );
            }

            if ( roleHint == null )
            {
                container.lookup( role );
            }
            else
            {
                container.lookup( role, roleHint );
            }
        }
    }

    private void addJars( File jarDir )
        throws Exception
    {
        container.addJarRepository( jarDir );
    }

    private void addClasses( File classes )
        throws Exception
    {
        container.addJarResource( classes );
    }
}
