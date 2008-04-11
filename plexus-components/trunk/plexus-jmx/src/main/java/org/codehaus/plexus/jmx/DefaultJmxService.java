package org.codehaus.plexus.jmx;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultJmxService
    extends AbstractLogEnabled
    implements JmxService, Startable, Initializable
{
    /**
     * @plexus.configuration
     */
    private List connectorServers;

    private List connectorServerUrls;

    private List connectorServerInstances;

    private MBeanServer mBeanServer;

    private List registeredMBeans;

    // -----------------------------------------------------------------------
    // JmxService Implementation
    // -----------------------------------------------------------------------

    public MBeanServer getMBeanServer()
    {
        return mBeanServer;
    }

    public ObjectName registerMBean( Object object, String objectName )
        throws MBeanRegistrationException, InstanceAlreadyExistsException, NotCompliantMBeanException,
        MalformedObjectNameException
    {
        ObjectName on = ObjectName.getInstance( objectName );

        getMBeanServer().registerMBean( object, on );

        registeredMBeans.add( on );

        return on;
    }

    public void releaseAllMBeansQuiet()
    {
        MBeanServer mBeanServer = getMBeanServer();

        for ( Iterator it = registeredMBeans.iterator(); it.hasNext(); )
        {
            ObjectName objectName = (ObjectName) it.next();

            it.remove();

            try
            {
                mBeanServer.unregisterMBean( objectName );
            }
            catch ( InstanceNotFoundException e )
            {
                getLogger().error( "Error while releasing MBean '" + objectName.getCanonicalName() + "'.", e );
            }
            catch ( MBeanRegistrationException e )
            {
                getLogger().error( "Error while releasing MBean '" + objectName.getCanonicalName() + "'.", e );
            }
        }
    }

    // -----------------------------------------------------------------------
    // Component Lifecycle
    // -----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        connectorServerUrls = new ArrayList();
        connectorServerInstances = new ArrayList();

        if ( connectorServers == null )
        {
            connectorServers = new ArrayList();
        }
        else
        {
            for ( Iterator it = connectorServers.iterator(); it.hasNext(); )
            {
                String string = (String) it.next();

                JMXServiceURL serviceUrl;

                serviceUrl = makeUrl( string );

                connectorServerUrls.add( serviceUrl );
            }
        }

        registeredMBeans = new ArrayList();
    }

    public void start()
        throws StartingException
    {
        getLogger().info( "Starting JMX service." );

        mBeanServer = MBeanServerFactory.createMBeanServer();

        try
        {
            for ( Iterator it = connectorServerUrls.iterator(); it.hasNext(); )
            {
                JMXServiceURL serviceUrl = (JMXServiceURL) it.next();

                JMXConnectorServer connectorServer =
                    JMXConnectorServerFactory.newJMXConnectorServer( serviceUrl, null, mBeanServer );
                connectorServer.start();
                connectorServerInstances.add( connectorServer );
                getLogger().info( "JMX connector started on " + serviceUrl.toString() );
            }
        }
        catch ( IOException e )
        {
            closeConnectorServerInstances();

            throw new StartingException( "Error while starting JMX server.", e );
        }
    }

    public void stop()
        throws StoppingException
    {
        getLogger().info( "Stopping JMX Service" );

        releaseAllMBeansQuiet();

        closeConnectorServerInstances();
    }

    // -----------------------------------------------------------------------
    // Private
    // -----------------------------------------------------------------------

    private JMXServiceURL makeUrl( String string )
        throws InitializationException
    {
        try
        {
            return new JMXServiceURL( string );
        }
        catch ( MalformedURLException e )
        {
            throw new InitializationException( "Could not create a JMX service URL from the string '" + string + "'.", e );
        }
    }

    private void closeConnectorServerInstances()
    {
        for ( Iterator it = connectorServerInstances.iterator(); it.hasNext(); )
        {
            JMXConnectorServer connectorServer = (JMXConnectorServer) it.next();

            try
            {
                connectorServer.stop();
            }
            catch ( IOException e )
            {
                getLogger().error( "Error while stopping the connector server '" + connectorServer.getAddress().getURLPath() + "'.", e );
            }
        }
    }
}
