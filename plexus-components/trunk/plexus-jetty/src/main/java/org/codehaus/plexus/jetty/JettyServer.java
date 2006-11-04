package org.codehaus.plexus.jetty;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.WebApplicationContext;

import java.io.File;
import java.io.IOException;

/** A subclass of the standard Jetty <code>Server</code> that
 *  provides a way to set the directory where WARs are expanded to
 *  and adds some classloading trickery to make the component
 *  work in Plexus.
 *
 *  @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 *
 *  @version $Id$
 *
 * @todo We need to give the Jetty component access to the JARs it needs
 *       to operate but we need to give it a ClassLoader with narrower
 *       scope. Currently it's getting the service brokers classloader
 *       which could certainly lead to problems.
 */
public class JettyServer
    extends Server
{
    private File warDeployDirectory;

    private ClassLoader classLoader;

    private PlexusContainer plexusContainer;

    private String webappConfiguration;

    public JettyServer()
    {
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void setWebappConfiguration( String webappConfiguration )
    {
        this.webappConfiguration = webappConfiguration;
    }

    public ClassLoader getClassLoader()
    {
        return classLoader;
    }

    public void setClassLoader( ClassLoader classLoader )
    {
        this.classLoader = classLoader;
    }
    
    public PlexusContainer getPlexusContainer()
    {
        return plexusContainer;
    }

    public void setPlexusContainer(PlexusContainer plexusContainer)
    {
        this.plexusContainer = plexusContainer;
    }

    public void setWarDeployDirectory( File warDeployDirectory )
    {
        this.warDeployDirectory = warDeployDirectory;
    }

    public File getWarDeployDirectory()
    {
        return warDeployDirectory;
    }

    public WebApplicationContext addWebApplication( String virtualHost,
                                                    String contextPathSpec,
                                                    String webApp )
        throws IOException
    {
        WebApplicationContext appContext = new WebApplicationContext( webApp );

        appContext.setDefaultsDescriptor( webappConfiguration );

        appContext.setContextPath( contextPathSpec );

        // The servlet classloader should be isolated, not sure if we need
        // this anymore.
        appContext.setParentClassLoader( classLoader );

        // Make the ServiceBroker available in context for use by PlexusServlets
        appContext.setAttribute( PlexusConstants.PLEXUS_KEY, getPlexusContainer() );

        // Add this webapp context to the running server.
        if ( virtualHost.equals("*") )
        {
            addContext( appContext );
        }
        else
        {
            addContext( virtualHost, appContext );
        }

        return appContext;
    }
}
