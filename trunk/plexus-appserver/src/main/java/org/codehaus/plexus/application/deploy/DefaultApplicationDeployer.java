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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Map;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.application.event.ApplicationListener;
import org.codehaus.plexus.application.event.DefaultDeployEvent;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.util.Expand;

/**
 * @component
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Mar 19, 2004
 */
public class DefaultApplicationDeployer
    extends AbstractLogEnabled
    implements ApplicationDeployer, Contextualizable, Initializable, Disposable
{
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
    // Accessors
    // ----------------------------------------------------------------------

    

    // ----------------------------------------------------------------------
    // Deployment
    // ----------------------------------------------------------------------

    public boolean deploy( String name, String url )
        throws Exception
    {
        URL location = new URL( url );

        return deploy( name, location );
    }

    public boolean deploy( String name, URL location )
        throws Exception
    {
        if ( !location.getProtocol().equals( "file" ) )
        {
            throw new RuntimeException( "Remote URLS not yet supported." );
        }

        File locationFile = new File( new URI( location.toString() ) );

        if ( location.toString().endsWith( ".jar" ) )
        {
            return deployJar( locationFile, applicationsDirectory );
        }
        else
        {
            return deployApplicationDirectory( name, locationFile );
        }
    }

    private boolean deployJar( File location, String directory )
        throws Exception
    {
        String jarName = location.getName();

        String appName = jarName.substring( 0, jarName.indexOf( ".jar" ) );

        File dest = new File( directory, appName );

        getLogger().info( "Extracting " + location + " to " + dest.getAbsolutePath() + "." );
        
        // Don't extract if it has been extracted before.
        if ( dest.exists() )
        {
            getLogger().info( "Application " + appName + " already extracted. Skipping." );
            return true;
        }
        else
        {
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
                getLogger().error( "Could not extract " + location + ".", e );
            }

            return deployApplicationDirectory( appName, dest );
        }
    }

    protected void deployApplications( String directory )
        throws Exception
    {
        getLogger().info( "Deploying directory " + directory + "." );

        File appDir = new File( directory );

        if ( appDir.isDirectory() )
        {
            File[] apps = appDir.listFiles();

            for ( int i = 0; i < apps.length; i++ )
            {
                deploy( apps[i].getName(), new File( apps[i].getAbsolutePath() ).toURL() );
            }
        }
    }

    protected boolean deployApplicationDirectory( String name, File location )
        throws Exception
    {
        getLogger().info( "Attempting to deploy application " + name + " at " + location.toString() );

        // ----------------------------------------------------------------------
        // We need to make sure that we have the basic requirements covered
        // when deploying an application. The PAR may be incomplete or corrupt
        // or a directory copied over just may not be intact.
        //
        // -> ${app}/conf/plexus.conf
        // -> ${app}/lib
        // ----------------------------------------------------------------------

        File applicationConfiguration = new File( location, "conf/plexus.conf" );

        if ( !applicationConfiguration.exists() )
        {
            getLogger().error( "The application " + name + " does not have a valid configuration: " +
                               applicationConfiguration + " does not exist!" );

            return false;
        }

        File applicationLibrary = new File( location, "lib" );

        if ( !applicationLibrary.exists() )
        {
            getLogger().error( "The application " + name + " does not have a valid library: " +
                               applicationLibrary + " does not exist!" );

            return false;
        }

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        DefaultPlexusContainer application = new DefaultPlexusContainer();

        deployments.put( name, application );

        InputStream stream = new FileInputStream( applicationConfiguration );

        Reader r = new InputStreamReader( stream );

        application.setConfigurationResource( r );

        if ( contextValues != null )
        {
            for ( Iterator i = contextValues.keySet().iterator(); i.hasNext(); )
            {
                String contextName = (String) i.next();

                application.addContextValue( contextName, contextValues.getProperty( contextName ) );
            }
        }

        // ----------------------------------------------------------------------
        // We want to set ${app.home} and we want to create a new realm for the
        // application. Need to think about how to really separate the apps
        // from the parent container.
        // ----------------------------------------------------------------------

        application.addContextValue( "plexus.home", location.getAbsolutePath() );

        application.setParentPlexusContainer( parentPlexus );

        application.setClassWorld( parentPlexus.getClassWorld() );

        application.setCoreRealm( parentPlexus.getCoreRealm() );

        try
        {
            application.initialize();

            application.start();
        }
        catch ( Exception e )
        {
            getLogger().error( "Error starting plexus.", e );

            return false;
        }

        DefaultDeployEvent event = createDeployEvent( name );

        for ( Iterator itr = applicationListeners.iterator(); itr.hasNext(); )
        {
            ApplicationListener a = (ApplicationListener) itr.next();

            a.deployedApplication( event );
        }

        return true;
    }

    // ----------------------------------------------------------------------
    // Redeploy
    // ----------------------------------------------------------------------

    public boolean redeploy( String name, String url ) throws Exception
    {
        undeploy( name );

        deploy( name, url );

        DefaultDeployEvent event = createDeployEvent( name );

        for ( Iterator itr = applicationListeners.iterator(); itr.hasNext(); )
        {
            ApplicationListener a = (ApplicationListener) itr.next();
            a.redeployedApplication( event );
        }

        return true;
    }

    // ----------------------------------------------------------------------
    // Undeploy
    // ----------------------------------------------------------------------

    public boolean undeploy( String name ) throws Exception
    {
        getLogger().info( "Undeploying " + name + "." );

        if ( deployments.containsKey( name ) )
        {
            DefaultPlexusContainer app = (DefaultPlexusContainer) deployments.remove( name );

            app.dispose();

            ClassRealm realm = app.getCoreRealm();

            realm.getWorld().disposeRealm( realm.getId() );
        }
        else
        {
            getLogger().warn( "Application " + name + " does not exist!" );
        }

        DefaultDeployEvent event = createDeployEvent( name );

        for ( Iterator itr = applicationListeners.iterator(); itr.hasNext(); )
        {
            ApplicationListener a = (ApplicationListener) itr.next();

            a.undeployedApplication( event );
        }

        return true;
    }

    // ----------------------------------------------------------------------
    // Events
    // ----------------------------------------------------------------------

    private DefaultDeployEvent createDeployEvent( String name )
    {
        DefaultDeployEvent event = new DefaultDeployEvent();

        event.setApplicationName( name );

        event.setSender( this );

        return event;
    }


    public PlexusContainer getApplication( String string )
    {
        return (PlexusContainer) deployments.get( string );
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

    public void initialize() throws Exception
    {
        deployments = new Hashtable();

        applicationListeners = new ArrayList();

        applicationsDirectory = new File( System.getProperty( "plexus.home" ), "apps" ).getPath();

        System.out.println( "applicationsDirectory = " + applicationsDirectory );

        if ( applicationsDirectory != null )
        {
            deployApplications( applicationsDirectory );
        }
    }

    public void dispose()
    {
        for ( Iterator itr = deployments.keySet().iterator(); itr.hasNext(); )
        {
            String name = (String) itr.next();

            DefaultPlexusContainer application = (DefaultPlexusContainer) deployments.remove( name );

            try
            {
                application.dispose();
            }
            catch ( Exception e )
            {
                getLogger().error( "Couldn't dipose of " + name, e );
            }
        }
    }
}
