package org.codehaus.plexus.xwork;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;
import org.codehaus.plexus.util.PropertyUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PlexusLifecycleListener
    implements ServletContextListener, HttpSessionListener
{
    private static final String PLEXUS_HOME = "plexus.home";

    private static final String PLEXUS_PROPERTIES_PARAM = "plexus-properties";

    private static final String DEFAULT_PLEXUS_PROPERTIES = "/WEB-INF/plexus.properties";

    public static final String KEY = "webwork.plexus.container";

    public static boolean loaded = false;

    public void contextInitialized( ServletContextEvent servletContextEvent )
    {

        try
        {
            // TODO: we should sort something out in Plexus so that this isn't necessary.
            // When run inside a Maven plugin (eg, the Jetty plugin), this will pick up the Maven core class
            // configuration instead of the webapp. While ideally Maven would isolate the plugin classes from itself,
            // Plexus should also be more flexible about how it is configured.
            // The workaround is just to filter out requests for META-INF/plexus/plexus.xml when it is requested
            // during construction of the container
            ClassLoader loader = new ClassLoader( getClass().getClassLoader() )
            {
                // Note! This method is final in JDK 1.4, so this will only run under JDK 5+
                // Normally it shouldn't need to be overridden, but ClassWorlds will use it as part of it's findResource
                // implementation
                public Enumeration getResources( String name )
                    throws IOException
                {
                    List l = new ArrayList();
                    for ( Enumeration e = super.getResources( name ); e.hasMoreElements(); )
                    {
                        URL url = (URL) e.nextElement();

                        if ( !url.toExternalForm().endsWith( "META-INF/plexus/plexus.xml" ) )
                        {
                            l.add( url );
                        }
                    }

                    return Collections.enumeration( l );
                }

                protected Enumeration findResources( String name )
                    throws IOException
                {
                    if ( !name.equals( "META-INF/plexus/plexus.xml" ) )
                    {
                        return super.findResources( name );
                    }
                    else
                    {
                        return Collections.enumeration( Collections.EMPTY_LIST );
                    }
                }
            };

            ServletContext ctx = servletContextEvent.getServletContext();

            PlexusContainer pc = new DefaultPlexusContainer( "default", loader, setConfigurationStream( ctx ), initializeContext( ctx, resolveContextProperties( ctx ) ) );

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
        loaded = true;
    }

    private Map initializeContext( ServletContext ctx, Properties properties )
    {
        Map map = new HashMap();

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
        loaded = false;
    }

    public void sessionCreated( HttpSessionEvent httpSessionEvent )
    {
    }

    public void sessionDestroyed( HttpSessionEvent httpSessionEvent )
    {
    }
}
