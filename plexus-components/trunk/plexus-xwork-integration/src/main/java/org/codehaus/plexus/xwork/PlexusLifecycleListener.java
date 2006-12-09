package org.codehaus.plexus.xwork;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;
import org.codehaus.plexus.context.DefaultContext;
import org.codehaus.plexus.util.PropertyUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Web listener that will initialize the Plexus container.
 * 
 * @version $Id$
 */
public class PlexusLifecycleListener
    implements ServletContextListener, HttpSessionListener
{
    private static final String PLEXUS_HOME = "plexus.home";

    private static final String PLEXUS_PROPERTIES_PARAM = "plexus-properties";

    private static final String DEFAULT_PLEXUS_PROPERTIES = "/WEB-INF/plexus.properties";

    public static final String KEY = "webwork.plexus.container";

    private static boolean loaded = false;

    private static void setLoaded( boolean loaded )
    {
        PlexusLifecycleListener.loaded = loaded;
    }

    public static boolean isLoaded()
    {
        return loaded;
    }

    public void contextInitialized( ServletContextEvent servletContextEvent )
    {
        try
        {
            ServletContext ctx = servletContextEvent.getServletContext();
            Map containerContext = new HashMap();

            PlexusContainer appContainer = (PlexusContainer) ctx.getAttribute( PlexusConstants.PLEXUS_KEY );
            if ( appContainer != null )
            {
                // TODO: should this be a parent container? Concerned about classloading, so not doing this right now, just inheriting the context
                DefaultContext appContainerContext = (DefaultContext) appContainer.getContext();
                containerContext.putAll( appContainerContext.getContextData() );
            }

            containerContext.putAll( initializeContext( ctx, resolveContextProperties( ctx ) ) );

            PlexusContainer pc = new DefaultPlexusContainer( "default", getClass().getClassLoader(), setConfigurationStream( ctx ),
                                                             containerContext );

            ctx.setAttribute( KEY, pc );
        }
        catch ( PlexusContainerException e )
        {
            throw new RuntimeException( e );
        }
        catch ( PlexusConfigurationResourceException e )
        {
            throw new RuntimeException( e );
        }
        setLoaded( true );
    }

    private Map initializeContext( ServletContext ctx, Properties properties )
    {
        Map map = new HashMap();

        // When run inside a Maven plugin (eg, the Jetty plugin), this will pick up the Maven core class
        // configuration instead of the webapp. While ideally Maven would isolate the plugin classes from itself,
        // this prevents Plexus from attempting to load a container configuration file as a workaround.
        map.put( PlexusConstants.IGNORE_CONTAINER_CONFIGURATION, Boolean.TRUE );

        // used by the servlet configuration phase
        map.put( ServletContext.class.getName(), ctx );

        map.putAll( properties );

        return map;
    }

    private static Properties resolveContextProperties( ServletContext context )
    {
        Properties properties = new Properties();

        String filename = context.getInitParameter( PLEXUS_PROPERTIES_PARAM );

        if ( filename == null )
        {
            filename = DEFAULT_PLEXUS_PROPERTIES;
        }

        context.log( "Loading plexus context properties from: '" + filename + "'" );

        try
        {
            URL url = context.getResource( filename );
            // bwalding: I think we'd be better off not using this exception swallower!
            properties = PropertyUtils.loadProperties( url );
        }
        catch ( Exception e )
        {
            // michal: I don't think it is that good idea to ignore this error.
            // bwalding: it's actually pretty difficult to get here as the PropertyUtils.loadProperties absorbs all Exceptions
            context.log( "Could not load plexus context properties from: '" + filename + "'" );
        }

        if ( properties == null )
        {
            context.log( "Could not load plexus context properties from: '" + filename + "'" );
            properties = new Properties();
        }

        if ( !properties.containsKey( PLEXUS_HOME ) )
        {
            setPlexusHome( context, properties );
        }

        return properties;
    }

    /**
     * Set plexus.home context variable
     */
    private static void setPlexusHome( ServletContext context, Properties contexProperties )
    {
        String realPath = context.getRealPath( "/WEB-INF" );

        if ( realPath != null )
        {
            File f = new File( realPath );

            contexProperties.setProperty( PLEXUS_HOME, f.getAbsolutePath() );
        }
        else
        {
            context.log( "Not setting 'plexus.home' as plexus is running inside webapp with no 'real path'." );
        }
    }

    private InputStreamReader setConfigurationStream( ServletContext ctx )
        throws PlexusConfigurationResourceException
    {
        InputStream is =
            Thread.currentThread().getContextClassLoader().getResourceAsStream( "META-INF/plexus/application.xml" );
        if ( is == null )
        {
            ctx.log( "Could not find " + "META-INF/plexus/application.xml" + ", skipping" );
            is = new ByteArrayInputStream( "<plexus><components></components></plexus>".getBytes() );
        }
        return new InputStreamReader( is );
    }

    public void contextDestroyed( ServletContextEvent servletContextEvent )
    {
        ServletContext ctx = servletContextEvent.getServletContext();
        PlexusContainer pc = (PlexusContainer) ctx.getAttribute( KEY );
        if ( pc != null )
        {
            pc.dispose();
        }
        setLoaded( false );
    }

    public void sessionCreated( HttpSessionEvent httpSessionEvent )
    {
    }

    public void sessionDestroyed( HttpSessionEvent httpSessionEvent )
    {
    }
}
