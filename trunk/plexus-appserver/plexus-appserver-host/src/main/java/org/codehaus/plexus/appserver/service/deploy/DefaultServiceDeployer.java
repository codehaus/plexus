package org.codehaus.plexus.appserver.service.deploy;

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

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.appserver.ApplicationServerException;
import org.codehaus.plexus.appserver.PlexusServiceConstants;
import org.codehaus.plexus.appserver.deploy.AbstractDeployer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Jul 17, 2004
 */
public class DefaultServiceDeployer
    extends AbstractDeployer
    implements ServiceDeployer, Initializable, Contextualizable
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
    // ServiceDeployer Implementation
    // ----------------------------------------------------------------------

    public void deploy( String name, File location )
        throws ApplicationServerException
    {
        try
        {
            deploy( name, location, new File( serviceDirectory ), new File( serviceDirectory ) );
        }
        catch ( Exception e )
        {
            throw new ApplicationServerException( "Cannot deploy service " + name, e );
        }
    }

    private void deploy( String name, File jar, File services, File configurations )
        throws Exception
    {
        File serviceDir = new File( services, name );

        if ( serviceDir.exists() )
        {
            getLogger().info( "Removing old service." );

            try
            {
                FileUtils.deleteDirectory( serviceDir );
            }
            catch ( IOException e )
            {
                throw new Exception( "Cannot delete old service deployment in " + serviceDir, e );
            }
        }

        expand( jar, serviceDir, false );

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

        // Copy over the user configurator if there is one.
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
     * The first time the configurator runs, we don't care
     * what the user configurator is, so only use the components.xml
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

    private void startComponents( PlexusConfiguration serviceConfig )
        throws PlexusConfigurationException, ComponentLookupException
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
        //throws Exception
    {
        container.addJarRepository( jarDir );
    }

    private void addClasses( File classes )
        throws Exception
    {
        container.addJarResource( classes );
    }

    public void redeploy( String name, File location )
        throws ApplicationServerException
    {
    }

    public void undeploy( String name )
        throws ApplicationServerException
    {
    }
}
