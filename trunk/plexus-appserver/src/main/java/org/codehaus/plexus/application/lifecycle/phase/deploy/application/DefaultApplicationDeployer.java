package org.codehaus.plexus.application.lifecycle.phase.deploy.application;

/*
 * Copyright (c) 2004, Codehausv.org
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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.NoSuchRealmException;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.DuplicateRealmException;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.application.ApplicationServerException;
import org.codehaus.plexus.application.PlexusApplicationConstants;
import org.codehaus.plexus.application.event.ApplicationListener;
import org.codehaus.plexus.application.event.DefaultDeployEvent;
import org.codehaus.plexus.application.profile.ApplicationRuntimeProfile;
import org.codehaus.plexus.application.lifecycle.phase.deploy.service.PlexusService;
import org.codehaus.plexus.application.lifecycle.phase.deploy.application.ApplicationDeployer;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.context.ContextMapAdapter;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.Expand;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Mar 19, 2004
 */
public class DefaultApplicationDeployer
    extends AbstractLogEnabled
    implements ApplicationDeployer,
    Contextualizable,
    Initializable,
    Disposable
{
    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private Map deployments;

    // This needs to be changed to PlexusContainer and expand the PlexusContainer
    // interface so that it can be used here. jvz.
    private DefaultPlexusContainer applicationServerContainer;

    private List applicationListeners;

    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    private String applicationsDirectory;

    private Properties contextValues;

    // ----------------------------------------------------------------------
    // Deployment
    // ----------------------------------------------------------------------

    public void deploy( String name,
                        String url )
        throws ApplicationServerException
    {
        URL location;

        try
        {
            location = new URL( url );
        }
        catch ( MalformedURLException e )
        {
            throw new ApplicationServerException( "Could not construct a URL from the string '" + url + "'.", e );
        }

        deploy( name, location );
    }

    public void deploy( String name,
                        URL url )
        throws ApplicationServerException
    {
        if ( !url.getProtocol().equals( "file" ) )
        {
            throw new ApplicationServerException( "Remote URLS not yet supported." );
        }

        if ( !url.toString().endsWith( ".jar" ) )
        {
            throw new ApplicationServerException( "This deployer can only deploy *.jar files." );
        }

        File file = new File( url.getFile() );

        try
        {
            deployJar( file, applicationsDirectory );
        }
        catch ( IOException e )
        {
            throw new ApplicationServerException( "Could not deploy the JAR.", e );
        }
        catch ( XmlPullParserException e )
        {
            throw new ApplicationServerException( "Could not deploy the JAR.", e );
        }
    }

    private void deployJar( File file,
                            String directory )
        throws IOException, ApplicationServerException, XmlPullParserException
    {
        // ----------------------------------------------------------------------
        // Extract the META-INF/plexus/application.xml file to read the metadata
        // ----------------------------------------------------------------------

        JarFile jarFile = new JarFile( file );

        ZipEntry entry = jarFile.getEntry( PlexusApplicationConstants.METADATA_FILE );

        if ( entry == null )
        {
            throw new ApplicationServerException( "The Plexus application jar is missing it's metadata file '" +
                PlexusApplicationConstants.METADATA_FILE + "'." );
        }

        Reader reader = new InputStreamReader( jarFile.getInputStream( entry ) );

        Xpp3Dom dom = Xpp3DomBuilder.build( reader );

        String appId = dom.getChild( "name" ).getValue();

        if ( StringUtils.isEmpty( appId ) )
        {
            throw new ApplicationServerException( "Missing 'name' element in the application metadata file." );
        }

        // ----------------------------------------------------------------------
        // Deploy the jar
        // ----------------------------------------------------------------------

        File dest = new File( directory, appId );

        // Don't extract if it has been extracted before.
        if ( dest.exists() )
        {
            getLogger().info( "Application '" + appId + "' already extracted." );
        }
        else
        {
            getLogger().info( "Extracting " + file + " to '" + dest.getAbsolutePath() + "'." );

            Expand expander = new Expand();

            expander.setDest( dest );

            expander.setOverwrite( false );

            expander.setSrc( file );

            try
            {
                expander.execute();
            }
            catch ( Exception e )
            {
                getLogger().error( "Could not extract '" + file + "'.", e );

                return;
            }
        }

        try
        {
            deployApplicationDirectory( appId, dest );
        }
        catch ( Exception e )
        {
            throw new ApplicationServerException( "Could not deploy the JAR", e );
        }
    }

    protected void deployApplicationDirectory( String name,
                                               File location )
        throws Exception
    {
        getLogger().info( "Deploying application '" + name + "' at '" + location.getAbsolutePath() + "'." );

        if ( deployments.containsKey( name ) )
        {
            throw new ApplicationServerException(
                "A application with the specified name ('" + name + "') already exists." );
        }

        // ----------------------------------------------------------------------
        // We need to make sure that we have the basic requirements covered
        // when deploying an application. The PAR may be incomplete or corrupt
        // or a directory copied over just may not be intact.
        //
        // -> ${app}/conf/plexus.conf
        // -> ${app}/lib
        // ----------------------------------------------------------------------

        File applicationConfigurationFile = new File( new File( location, PlexusApplicationConstants.CONF_DIRECTORY ),
                                                      PlexusApplicationConstants.CONFIGURATION_FILE );

        if ( !applicationConfigurationFile.exists() )
        {
            throw new ApplicationServerException( "The application '" + name + "' does not have a valid " +
                "configuration: " + applicationConfigurationFile + " does not exist!" );
        }

        File applicationLibrary = new File( location, PlexusApplicationConstants.LIB_DIRECTORY );

        if ( !applicationLibrary.exists() )
        {
            throw new ApplicationServerException( "The application '" + name + "' does not have a valid library: " +
                applicationLibrary + " does not exist!" );
        }

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        DefaultPlexusContainer applicationContainer = new DefaultPlexusContainer();

        InputStream stream = new FileInputStream( applicationConfigurationFile );

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

        Object appserver = applicationServerContainer.getContext().get( "plexus.appserver" );

        applicationContainer.addContextValue( "plexus.appserver", appserver );

        applicationContainer.setParentPlexusContainer( applicationServerContainer );

        applicationContainer.setClassWorld( applicationServerContainer.getClassWorld() );

        // ----------------------------------------------------------------------
        // Create the realm for the application
        // ----------------------------------------------------------------------

        // When this is not done things appear to work but everything is put in the core realm of the
        // container

        ClassRealm realm = new SimpleClassRealm( "plexus.application." + name, new SimpleClassLoader( applicationServerContainer.getContainerRealm().getClassLoader() ), applicationServerContainer.getClassWorld() );

        applicationContainer.setCoreRealm( realm );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        Map context = new ContextMapAdapter( applicationContainer.getContext() );

        Reader configurationReader =
            new InterpolationFilterReader( new FileReader( applicationConfigurationFile ), context );

        Xpp3Dom dom = Xpp3DomBuilder.build( configurationReader );

        PlexusConfiguration applicationConfiguration = new XmlPlexusConfiguration( dom );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        ApplicationRuntimeProfile profile = new ApplicationRuntimeProfile( name, location, applicationLibrary,
                                                                           applicationContainer,
                                                                           applicationServerContainer,
                                                                           applicationConfiguration );

        PlexusConfiguration[] services = applicationConfiguration.getChild( "services" ).getChildren( "service" );

        for ( int i = 0; i < services.length; i++ )
        {
            PlexusConfiguration serviceConfiguration = services[i];

            String id = serviceConfiguration.getChild( "id" ).getValue();

            if ( StringUtils.isEmpty( id ) )
            {
                throw new Exception( "Missing child element 'id' in 'service'." );
            }

            if ( !applicationServerContainer.hasComponent( PlexusService.ROLE, id ) )
            {
                getLogger().error(
                    "Error while loading plexus service with id '" + id + "'. " + "The service doesn't exists." );

                continue;
            }

            Object serviceObject = applicationServerContainer.lookup( PlexusService.ROLE, id );

            if ( !( serviceObject instanceof PlexusService ) )
            {
                getLogger().error( "Error while loading plexus service with id '" + id + "'. " +
                    "The component has to implement the PlexusService interface." );

                continue;
            }

            PlexusService service = (PlexusService) serviceObject;

            profile.getServices().add( service );

            PlexusConfiguration conf = serviceConfiguration.getChild( "configuration" );

            profile.getServiceConfigurations().add( conf );

            service.beforeApplicationStart( profile, conf );
        }

        deployments.put( name, profile );

        // ----------------------------------------------------------------------
        // Start the application
        // ----------------------------------------------------------------------

        // This is here in the case of the jetty service where the application.xml specifies resources
        // that are in the path of an extracted WAR file. The Jetty service does the unpacking and then
        // the application can start up correctly. Otherwise during the initialization of the application
        // container we will get a failure. Chop this up into phases so it's clear.

        try
        {
            applicationContainer.initialize();

            applicationContainer.start();
        }
        catch ( Exception e )
        {
            throw new Exception( "Error starting Plexus.", e );
        }


        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        for ( int i = 0; i < profile.getServices().size(); i++ )
        {
            PlexusService service = (PlexusService) profile.getServices().get( i );

            PlexusConfiguration configuration = (PlexusConfiguration) profile.getServiceConfigurations().get( i );

            service.afterApplicationStart( profile, configuration );
        }
    }

    // ----------------------------------------------------------------------
    // Redeploy
    // ----------------------------------------------------------------------

    public void redeploy( String name,
                          String url )
        throws ApplicationServerException
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
        throws ApplicationServerException
    {
        getLogger().info( "Undeploying '" + name + "'." );

        ApplicationRuntimeProfile profile = getApplicationRuntimeProfile( name );

        deployments.remove( name );

        DefaultPlexusContainer app = profile.getApplicationContainer();

        app.dispose();

        ClassRealm realm = app.getCoreRealm();

        try
        {
            realm.getWorld().disposeRealm( realm.getId() );
        }
        catch ( NoSuchRealmException e )
        {
            getLogger().warn( "Error while disposing application realm '" + realm.getId() + "'" );
        }

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
        return new DefaultDeployEvent( runtimeProfile );
    }

    public ApplicationRuntimeProfile getApplicationRuntimeProfile( String applicationName )
        throws ApplicationServerException
    {
        ApplicationRuntimeProfile profile = (ApplicationRuntimeProfile) deployments.get( applicationName );

        if ( profile == null )
        {
            throw new ApplicationServerException( "No such application: '" + applicationName + "'." );
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
        applicationServerContainer = (DefaultPlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    public void initialize()
        throws InitializationException
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
            catch ( Exception e )
            {
                getLogger().warn( "Error while undeploying application '" + name + "'.", e );
            }
        }
    }

    class SimpleClassLoader
        extends URLClassLoader
    {
        public SimpleClassLoader( ClassLoader classLoader )
        {
            super( new URL[0], classLoader );
        }

        public void addURL( URL url )
        {
            super.addURL( url );
        }
    }

    class SimpleClassRealm
        implements ClassRealm
    {
        private String id;

        private ClassWorld world;

        SimpleClassLoader classLoader;

        public SimpleClassRealm( String id,
                                 SimpleClassLoader classLoader, ClassWorld world )
        {
            this.id = id;
            this.classLoader = classLoader;
            this.world = world;
        }

        public String getId()
        {
            return id;
        }

        public void addConstituent( URL url )
        {
            classLoader.addURL( url );
        }

        public ClassRealm locateSourceRealm( String a )
        {
            throw new UnsupportedOperationException();
        }

        public ClassLoader getClassLoader()
        {
            return classLoader;
        }

        public URL[] getConstituents()
        {
            return classLoader.getURLs();
        }

        public Class loadClass( String name )
            throws ClassNotFoundException
        {
            return classLoader.loadClass( name );
        }

        public URL getResource( String name )
        {
            return classLoader.getResource( name );
        }

        public Enumeration findResources( String name )
            throws IOException
        {
            return classLoader.findResources( name );
        }

        // ----------------------------------------------------------------------------
        // Things we don't care about, we'll use normal classloader semantics.
        // ----------------------------------------------------------------------------

        public ClassWorld getWorld()
        {
            return world;
        }

        public void importFrom( String a, String b )
        {
            throw new UnsupportedOperationException();
        }

        public void setParent( ClassRealm c )
        {
            throw new UnsupportedOperationException();
        }

        public InputStream getResourceAsStream( String name )
        {
            return classLoader.getResourceAsStream( name );
        }


        public ClassRealm getParent()
        {
            throw new UnsupportedOperationException();
        }

        public ClassRealm createChildRealm( String id )
            throws DuplicateRealmException
        {
            throw new UnsupportedOperationException();
        }

        public void display()
        {
        }
    }
}
