package org.codehaus.plexus.xwork;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.configuration.PlexusConfigurationResourceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Enumeration;

public class PlexusLifecycleListener implements ServletContextListener, HttpSessionListener {
    // TODO: do we need logging in here?
    private static final Log log = LogFactory.getLog(PlexusLifecycleListener.class);

    public static boolean loaded = false;
    public static final String KEY = "webwork.plexus.container";

    public void contextInitialized(ServletContextEvent servletContextEvent) {

        try {
            // TODO: we should sort something out in Plexus so that this isn't necessary.
            // When run inside a Maven plugin (eg, the Jetty plugin), this will pick up the Maven core class
            // configuration instead of the webapp. While ideally Maven would isolate the plugin classes from itself,
            // Plexus should also be more flexible about how it is configured.
            // The workaround is just to filter out requests for META-INF/plexus/plexus.xml when it is requested
            // during construction of the container
            PlexusContainer pc = new DefaultPlexusContainer( "default", new ClassLoader( getClass().getClassLoader() )
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
            } );

            setConfigurationStream( pc );
            ServletContext ctx = servletContextEvent.getServletContext();
            ctx.setAttribute(KEY, pc);

            // used by the servlet configuration phase
            pc.getContext().put( ServletContext.class.getName(), ctx );

            pc.initialize();
            pc.start();
        } catch (PlexusContainerException e) {
            throw new RuntimeException(e);
        } catch (PlexusConfigurationResourceException e) {
            throw new RuntimeException(e);
        }
        loaded = true;
    }

    private void setConfigurationStream( PlexusContainer pc )
        throws PlexusConfigurationResourceException
    {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/plexus/application.xml" );
        if (is == null) {
            log.info("Could not find " + "META-INF/plexus/application.xml" + ", skipping");
            is = new ByteArrayInputStream("<plexus><components></components></plexus>".getBytes());
        }
        pc.setConfigurationResource(new InputStreamReader(is));
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ServletContext ctx = servletContextEvent.getServletContext();
        PlexusContainer pc = (PlexusContainer) ctx.getAttribute(KEY);
        if (pc != null) {
            pc.dispose();
        }
        loaded = false;
    }

    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
    }

    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
    }
}
