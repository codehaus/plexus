package org.codehaus.plexus.application.deploy;

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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.FileReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.application.PlexusApplicationConstants;
import org.codehaus.plexus.application.service.PlexusService;
import org.codehaus.plexus.application.event.ApplicationListener;
import org.codehaus.plexus.application.event.DefaultDeployEvent;
import org.codehaus.plexus.application.profile.ApplicationRuntimeProfile;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.context.ContextMapAdapter;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.util.Expand;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Mar 19, 2004
 */
public class DefaultApplicationDeployer
    extends AbstractLogEnabled
    implements ApplicationDeployer, Contextualizable, Initializable, Disposable
{
    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private Map deployments;

    // This needs to be changed to PlexusContainer and expand the PlexusContainer
    // interface so that it can be used here. jvz.
    private DefaultPlexusContainer parentPlexus;

    private List applicationListeners;

    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    private String applicationsDirectory;

    private Properties contextValues;

    // ----------------------------------------------------------------------
    // Deployment
    // ----------------------------------------------------------------------

    public void deploy( String name, String url )
        throws Exception
    {
        URL location = new URL( url );

        deploy( name, location );
    }

    public void deploy( String name, URL location )
        throws Exception
    {
        if ( !location.getProtocol().equals( "file" ) )
        {
            throw new RuntimeException( "Remote URLS not yet supported." );
        }

        File locationFile = new File( new URI( location.toString() ) );

        if ( location.toString().endsWith( ".jar" ) )
        {
            deployJar( locationFile, applicationsDirectory );
        }
        else
        {
            throw new Exception( "This deployer can only deploy *.jar files." );
        }
    }

    private void deployJar( File location, String directory )
        throws Exception
    {
        String jarName = location.getName();

        String appName = jarName.substring( 0, jarName.indexOf( ".jar" ) );

        File dest = new File( directory, appName );

        // Don't extract if it has been extracted before.
        if ( dest.exists() )
        {
            getLogger().info( "Application '" + appName + "' already extracted." );
        }
        else
        {
            getLogger().info( "Extracting " + location + " to '" + dest.getAbsolutePath() + "'." );

            Expand expander = new Expand();

            expander.setDest( dest );

            expander.setOverwrite( false );

            expander.setSrc( location );

            try
            {
                expander.execute();
            }
            catch ( Exception e )
            {
                getLogger().error( "Could not extract '" + location + "'.", e );

                return;
            }
        }

        deployApplicationDirectory( appName, dest );
    }

//    protected void deployApplications( String directory )
//        throws Exception
//    {
//        getLogger().info( "Deploying directory '" + directory + "'." );
//
//        File appDir = new File( directory );
//
//        if ( appDir.isDirectory() )
//        {
//            File[] apps = appDir.listFiles();
//
//            for ( int i = 0; i < apps.length; i++ )
//            {
//                deploy( apps[i].getName(), new File( apps[i].getAbsolutePath() ).toURL() );
//            }
//        }
//    }

    protected void deployApplicationDirectory( String name, File location )
        throws Exception
    {
        getLogger().info( "Deploying application '" + name + "' at '" + location.toString() + "'." );

        if ( deployments.containsKey( name ) )
        {
            throw new Exception( "A application with the specified name ('" + name + "') already exists." );
        }

        // ----------------------------------------------------------------------
        // We need to make sure that we have the basic requirements covered
        // when deploying an application. The PAR may be incomplete or corrupt
        // or a directory copied over just may not be intact.
        //
        // -> ${app}/conf/plexus.conf
        // -> ${app}/lib
        // ----------------------------------------------------------------------

        File applicationConfiguration = new File( new File( location, PlexusApplicationConstants.CONF_DIRECTORY ), PlexusApplicationConstants.CONFIGURATION_FILE );

        if ( !applicationConfiguration.exists() )
        {
            throw new Exception( "The application '" + name + "' does not have a valid configuration: " +
                                 applicationConfiguration + " does not exist!" );
        }

        File applicationLibrary = new File( location, PlexusApplicationConstants.LIB_DIRECTORY );

        if ( !applicationLibrary.exists() )
        {
            throw new Exception( "The application '" + name + "' does not have a valid library: " +
                               applicationLibrary + " does not exist!" );
        }

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        DefaultPlexusContainer applicationContainer = new DefaultPlexusContainer();

        InputStream stream = new FileInputStream( applicationConfiguration );

        Reader r = new InputStreamReader( stream );

        applicationContainer.setConfigurationResource( r );

        if ( contextValues != null )
        {
            for ( Iterator i = contextValues.keySet().iterator(); i.hasNext(); )
            {
                String contextName = (String) i.next();

                applicationContainer.addContextValue( contextName, contextValues.getProperty( contextName ) );
            }
        }

        // ----------------------------------------------------------------------
        // We want to set ${app.home} and we want to create a new realm for the
        // application. Need to think about how to really separate the apps
        // from the parent container.
        // ----------------------------------------------------------------------

        applicationContainer.addContextValue( "plexus.home", location.getAbsolutePath() );

        applicationContainer.setParentPlexusContainer( parentPlexus );

        applicationContainer.setClassWorld( parentPlexus.getClassWorld() );

        // ----------------------------------------------------------------------
        // Create the realm for the application
        // ----------------------------------------------------------------------

        ClassRealm realm = parentPlexus.getCoreRealm().createChildRealm( "plexus.application." + name );

        applicationContainer.setCoreRealm( realm );

        // ----------------------------------------------------------------------
        // Start the application
        // ----------------------------------------------------------------------

        try
        {
            applicationContainer.initialize();

            applicationContainer.start();
        }
        catch ( Exception e )
        {
            throw new Exception( "Error starting Plexus.", e );
        }

        ApplicationRuntimeProfile profile = new ApplicationRuntimeProfile( name, location, applicationLibrary, applicationContainer );

        deployments.put( name, profile );

        // ----------------------------------------------------------------------
        // Notify listeners
        // ----------------------------------------------------------------------

        Map context = new ContextMapAdapter( applicationContainer.getContext() );

        Reader configurationReader = new InterpolationFilterReader( new FileReader( applicationConfiguration ), context );

        Xpp3Dom dom = Xpp3DomBuilder.build( configurationReader );

        PlexusConfiguration applicationDescriptor = new XmlPlexusConfiguration( dom );

        PlexusConfiguration[] services = applicationDescriptor.getChild( "services" ).getChildren( "service" );

        for ( int i = 0; i < services.length; i++ )
        {
            PlexusConfiguration serviceConfiguration = services[ i ];

            String id = serviceConfiguration.getChild( "id" ).getValue();

            if ( StringUtils.isEmpty( id ) )
            {
                throw new Exception( "Missing child element 'id' in 'serviceConfiguration'." );
            }

            if ( !parentPlexus.hasComponent( PlexusService.ROLE, id ) )
            {
                getLogger().error( "Error while loading plexus service with id '" + id + "'. " +
                                   "The service doesn't exists.");

                continue;
            }

            Object serviceObject = parentPlexus.lookup( PlexusService.ROLE, id );

            if ( !(serviceObject instanceof PlexusService ) )
            {
                getLogger().error( "Error while loading plexus service with id '" + id + "'. " +
                                   "The component has to implement the PlexusService interface." );

                continue;
            }

            PlexusService service = (PlexusService) serviceObject;

            service.onApplicationStart( profile, serviceConfiguration.getChild( "configuration" ) );
        }

//        DefaultDeployEvent event = createDeployEvent( profile );
//
//        for ( Iterator itr = applicationListeners.iterator(); itr.hasNext(); )
//        {
//            ApplicationListener listener = (ApplicationListener) itr.next();
//
//            listener.deployedApplication( event );
//        }
    }

    // ----------------------------------------------------------------------
    // Redeploy
    // ----------------------------------------------------------------------

    public void redeploy( String name, String url )
        throws Exception
    {
        ApplicationRuntimeProfile profile = getApplicationRuntimeProfile( name );

        undeploy( name );

        deploy( name, url );

        DefaultDeployEvent event = createDeployEvent( profile );

        for ( Iterator itr = applicationListeners.iterator(); itr.hasNext(); )
        {
            ApplicationListener listener = (ApplicationListener) itr.next();

            listener.redeployedApplication( event );
        }
    }

    // ----------------------------------------------------------------------
    // Undeploy
    // ----------------------------------------------------------------------

    public void undeploy( String name )
        throws Exception
    {
        getLogger().info( "Undeploying '" + name + "'." );

        ApplicationRuntimeProfile profile = getApplicationRuntimeProfile( name );

        deployments.remove( name );

        DefaultPlexusContainer app = (DefaultPlexusContainer) profile.getContainer();

        app.dispose();

        ClassRealm realm = app.getCoreRealm();

        realm.getWorld().disposeRealm( realm.getId() );

        DefaultDeployEvent event = createDeployEvent( profile );

        for ( Iterator itr = applicationListeners.iterator(); itr.hasNext(); )
        {
            ApplicationListener listener = (ApplicationListener) itr.next();

            listener.undeployedApplication( event );
        }
    }

    // ----------------------------------------------------------------------
    // Events
    // ----------------------------------------------------------------------

    private DefaultDeployEvent createDeployEvent( ApplicationRuntimeProfile runtimeProfile )
    {
        DefaultDeployEvent event = new DefaultDeployEvent( runtimeProfile );

        return event;
    }

    public ApplicationRuntimeProfile getApplicationRuntimeProfile( String applicationName )
        throws Exception
    {
        ApplicationRuntimeProfile profile = (ApplicationRuntimeProfile) deployments.get( applicationName );

        if ( profile == null )
        {
            throw new Exception( "No such application: '" + applicationName + "'.");
        }

        return profile;
    }

    public void addApplicationListener( ApplicationListener listener )
    {
        applicationListeners.add( listener );
    }

    public void removeApplicationListener( ApplicationListener listener )
    {
        applicationListeners.remove( listener );
    }

    // ----------------------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------------------

    public void contextualize( Context context )
        throws ContextException
    {
        parentPlexus = (DefaultPlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    public void initialize()
        throws Exception
    {
        deployments = new HashMap();

        applicationListeners = new ArrayList();

        getLogger().info( "Applications will be deployed in: '" + applicationsDirectory + "'." );
    }

    public void dispose()
    {
        List names = new ArrayList( deployments.keySet() );

        for ( Iterator it = names.iterator(); it.hasNext(); )
        {
            String name = (String) it.next();

            try
            {
                undeploy( name );
            }
            catch( Exception e )
            {
                getLogger().warn( "Error while undeploying application '" + name + "'.", e );
            }
        }
    }
}
