package org.codehaus.plexus.appserver.management;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
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

import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.FileUtils;
import org.livetribe.slp.Attributes;
import org.livetribe.slp.Scopes;
import org.livetribe.slp.ServiceInfo;
import org.livetribe.slp.ServiceURL;
import org.livetribe.slp.api.sa.StandardServiceAgent;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * @author <a href="mailto:evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class DefaultAgent
    extends AbstractLogEnabled
    implements Agent, Initializable, Contextualizable
{
    /**
     * @plexus.requirement role="org.codehaus.plexus.appserver.management.MBean"
     */
    private List mbeans;

    /**
     * @plexus.configuration default-value="service:jmx:rmi:///"
     */
    private String serviceUrl;

    /**
     * @plexus.configuration default-value="3427"
     */
    private int slpPort;

    private MBeanServer mbeanServer;

    private JMXServiceURL jmxServiceURL;

    private String tempDirectory;

    public void initialize()
        throws InitializationException
    {
        try
        {
            mbeanServer = MBeanServerFactory.createMBeanServer();

            // Register mbeans
            if ( mbeans != null )
            {
                for ( Iterator i = mbeans.iterator(); i.hasNext(); )
                {
                    MBean mbean = (MBean) i.next();
                    mbeanServer.registerMBean( mbean,
                                               new ObjectName( mbean.getDomain() + ":name=" + mbean.getName() ) );
                }
            }

            // Attach a JMXConnectorServer to the platform MBeanServer
            jmxServiceURL = new JMXServiceURL( serviceUrl );

            JMXConnectorServer connectorServer =
                JMXConnectorServerFactory.newJMXConnectorServer( jmxServiceURL, null, mbeanServer );
            connectorServer.start();

            // Refresh the JMXServiceURL after the JMXConnectorServer has started
            jmxServiceURL = connectorServer.getAddress();

            // Now advertise the JMXConnectorServer service via SLP

            // Convert JMXServiceURL (JMX) to ServiceURL (SLP)
            ServiceURL serviceURL = new ServiceURL( jmxServiceURL.toString() );
            Scopes scopes = Scopes.DEFAULT;
            Attributes attributes = null;
            String language = Locale.ENGLISH.getLanguage();
            ServiceInfo serviceInfo = new ServiceInfo( serviceURL, scopes, attributes, language );

            // Create the SLP ServiceAgent that advertises the JMX service
            StandardServiceAgent serviceAgent = new StandardServiceAgent();
            // Allow this code to be run by non-root users on Linux/Unix
            serviceAgent.setPort( slpPort );
            // Register the service and start the SLP ServiceAgent
            serviceAgent.register( serviceInfo );
            serviceAgent.start();

            File serviceFile = new File( tempDirectory, "agent.properties" );
            Properties props = new Properties();
            props.put( "jmxServiceUrl", jmxServiceURL.toString() );
            props.put( "slpPort", String.valueOf( slpPort ) );
            props.store( new FileOutputStream( serviceFile ), null );
            FileUtils.forceDeleteOnExit( serviceFile );

            getLogger().info( "JMX manager agent is up and running (" + jmxServiceURL + ")." );
            getLogger().info( "JMX agent is registered in SLP service on port " + slpPort );
        }
        catch ( Exception e )
        {
            throw new InitializationException( "Can't load JMX agent.", e );
        }
    }

    public void contextualize( Context context )
        throws ContextException
    {
        tempDirectory = (String) context.get( "plexus.temp" );
    }
}
