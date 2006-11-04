package org.codehaus.plexus.tomcat;

import org.apache.catalina.Connector;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Logger;
import org.apache.catalina.startup.Embedded;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.FileUtils;

import javax.management.ObjectName;
import java.io.File;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultPlexusTomcat
    extends AbstractLogEnabled
    implements PlexusTomcat, Initializable
{
    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    private File catalinaHome;

    private File contextRoot;

    private String defaultHost;

    private Logger logger;

    // ----------------------------------------------------------------------
    // Instance Variables
    // ----------------------------------------------------------------------

    private Embedded embedded;

    private Engine engine;

    // ----------------------------------------------------------------------
    // PlexusTomcat Implementation
    // ----------------------------------------------------------------------

    public void setCatalinaHome( File catalinaHome )
    {
        this.catalinaHome = catalinaHome;

        System.setProperty( "catalina.home", catalinaHome.getAbsolutePath() );
    }

    public void setContextRoot( File contextRoot )
    {
        this.contextRoot = contextRoot;
    }

    public void setLogger( Logger logger )
    {
        this.logger = logger;
    }

    public void setDefaultHost( String defaultHost )
    {
        this.defaultHost = defaultHost;
    }

    public Engine getEngine()
    {
        return engine;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public Host addHost( String hostName, String webapps )
    {
        Host host = embedded.createHost( hostName, webapps );

//        engine.addChild( host );

//        if ( engine.getDefaultHost() == null )
//        {
//            System.err.println( "Setting default host to: '" + host.getName() + "'." );
//            engine.setDefaultHost( host.getName() );
//        }

        return host;
    }

    public Connector addConnector( String host, int port, boolean secure )
    {
        Connector connector = embedded.createConnector( host, port, secure );

        embedded.addConnector( connector );

        return connector;
    }

    public Context addContext( String hostName, String contextName, String contextPath, File path )
    {
        hostName = hostName.toLowerCase();

        Context context = embedded.createContext( contextPath, path.getAbsolutePath() );

        context.setName( contextName );

        Container child = engine.findChild( hostName );

        if ( child == null )
        {
            throw new RuntimeException( "Could not find host '" + hostName + "'." );
        }

        if ( !( child instanceof Host ) )
        {
            throw new RuntimeException( "Child is not of type Host, but rather: " + child.getClass() );
        }

//        Host host = (Host) child;
//        System.err.println( "Adding " + context.getName() + " as a child of " + host.getName() );
//        host.addChild( context );

        return context;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void startTomcat()
        throws LifecycleException
    {
        if ( catalinaHome == null )
        {
            throw new LifecycleException( "Catalina home has to be set." );
        }

        if ( contextRoot == null )
        {
            throw new LifecycleException( "The context root has to be set." );
        }

        FileUtils.mkdir( catalinaHome.getAbsolutePath() );
        FileUtils.mkdir( contextRoot.getAbsolutePath() );

        System.setProperty( "catalina.home", catalinaHome.getAbsolutePath() );

//        if ( logger == null )
//        {
//            embedded.setLogger( new PlexusCatalinaLogger( getLogger() ) );
//        }
//        else
//        {
//            embedded.setLogger( new SystemErrLogger() );
//        }

//        FileLogger fileLog = new FileLogger();
//        fileLog.setDirectory( "/tmp" );
//        fileLog.setPrefix( "tomcat" );
//        fileLog.setSuffix( ".log" );
//        fileLog.setTimestamp( true );
//        embedded.setLogger( fileLog );
//
//        embedded.setDebug( 5 );
//
//        if ( defaultHost != null )
//        {
//            engine.setDefaultHost( defaultHost );
//        }
//
        embedded.setName( "Plexus Tomcat Embedded" );

        embedded.addEngine( engine );

        embedded.start();

        addConnector( "localhost", 8080, false ).initialize();

        // ----------------------------------------------------------------------
        // Debugging
        // ----------------------------------------------------------------------

        System.err.println( "Container name: " + embedded.getContainerName() );

        ObjectName[] connectorNames = embedded.getConnectorNames();
        for ( ObjectName connectorName : connectorNames )
        {
            System.err.println( "Connector name: " + connectorName );
        }
    }

    public void stopTomcat()
        throws LifecycleException
    {
        try
        {
            embedded.stop();
        }
        finally
        {
            embedded.destroy();
        }
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        embedded = new Embedded();

        engine = embedded.createEngine();

        engine.setName( "Plexus Tomcat Engine" );

        engine.setDefaultHost( "localhost" );
    }
}
