package org.codehaus.plexus.appserver.application.deploy.lifecycle.phase;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.DuplicateRealmException;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentContext;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.context.ContextMapAdapter;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * @author Jason van Zyl
 */
public class CreateAppContainerPhase
    extends AbstractAppDeploymentPhase
{
    public void execute( AppDeploymentContext context )
        throws AppDeploymentException
    {
        DefaultPlexusContainer appServerContainer = context.getAppServerContainer();

        String name = "plexus.application." + context.getApplicationId();

        getLogger().info( "Using appDir = " + context.getAppDir() );

        DefaultPlexusContainer applicationContainer = null;

        try
        {
            applicationContainer =
                new DefaultPlexusContainer( name, appServerContainer.getClassWorld(), appServerContainer );
        }
        catch ( PlexusContainerException e )
        {
            throw new AppDeploymentException( "Error starting container.", e );
        }

        try
        {
            InputStream stream = new FileInputStream( context.getAppConfigurationFile() );

            Reader r = new InputStreamReader( stream );

            applicationContainer.setConfigurationResource( r );
        }
        catch ( Exception e )
        {
            throw new AppDeploymentException( "Error processing application configurator.", e );
        }

        Properties contextValues = context.getContext();

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
        // appserver. Need to think about how to really separate the apps
        // from the parent container.
        // ----------------------------------------------------------------------

        try
        {
            String plexusHome = (String) context.getAppServerContainer().getContext().get( "plexus.home" );

            applicationContainer.addContextValue( "appserver.home", new File( plexusHome ).getCanonicalPath() );
        }
        catch ( Exception e )
        {
            // Won't happen
        }

        // ----------------------------------------------------------------------------
        // Make the application's home directory available in the context
        // ----------------------------------------------------------------------------
        applicationContainer.addContextValue( "plexus.home", context.getAppDir().getAbsolutePath() );

        applicationContainer.addContextValue( "app.home", context.getAppDir().getAbsolutePath() );

        // ----------------------------------------------------------------------------
        // Make the user's home directory available in the context
        // ----------------------------------------------------------------------------
        applicationContainer.addContextValue( "user.home", System.getProperty( "user.home" ) );

        Object appserver = null;

        try
        {
            appserver = appServerContainer.getContext().get( "plexus.appserver" );
        }
        catch ( ContextException e )
        {
            // won't happen.
        }

        applicationContainer.addContextValue( "plexus.appserver", appserver );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        Map ctx = new ContextMapAdapter( applicationContainer.getContext() );

        Xpp3Dom dom;

        try
        {
            Reader configurationReader =
                new InterpolationFilterReader( new FileReader( context.getAppConfigurationFile() ), ctx );

            dom = Xpp3DomBuilder.build( configurationReader );
        }
        catch ( Exception e )
        {
            throw new AppDeploymentException( "Error processing application configurator.", e );
        }

        PlexusConfiguration applicationConfiguration = new XmlPlexusConfiguration( dom );

        context.setAppConfiguration( applicationConfiguration );

        context.setApplicationContainer( applicationContainer );
    }

    // ----------------------------------------------------------------------------
    // These were specifically made so that a WAR file deploy with Jetty in a
    // standard way would work properly. The relationship that ClassWorlds sets
    // up among classloaders doesn't appear to work in standard situations
    // which is bad.
    // ----------------------------------------------------------------------------

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

        public SimpleClassRealm( String id, SimpleClassLoader classLoader, ClassWorld world )
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
            URL[] urls = classLoader.getURLs();

            for ( int i = 0; i < urls.length; i++ )
            {
                System.out.println( "url = " + urls[i] );
            }
        }
    }
}
