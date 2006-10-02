/**
 * 
 */
package org.codehaus.plexus.xsiter.vhost;

import java.io.Reader;
import java.util.List;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @dated: 14/09/2006, 12:08:05 PM
 */
public interface VirtualHostManager
{

    public String ROLE = VirtualHostManager.class.getName();

    /**
     * Name of the property element in th pom.xml that holds the Virtual Hosts
     * configuration as XML.
     */
    public static final String ELT_POM_VHOSTS_CONFIG = "vhosts.configuration";

    /**
     * Identifier for the configuration in case of multiple Virtual
     * Configuration elements.
     */
    public static final String PROP_VHOST_ID = "id";

    /**
     * Constant for Virtual Host Directory.
     */
    public static final String PROP_VHOST_DIRECTORY = "vhostDirectory";

    /**
     * Constant for Virtual Host Connector Port property name.
     */
    public static final String PROP_VHOST_CONNECTOR_PORT = "vhostConnectorPort";

    /**
     * Constant for Virtual Host Connector protocol property name.
     */
    public static final String PROP_VHOST_CONNECTOR_PROTOCOL = "vhostConnectorProtocol";

    /**
     * Constant for Virtual Host Log Directory property name.
     */
    public static final String PROP_VHOST_LOG_DIRECTORY = "vhostLogDirectory";

    /**
     * Constant for Virtual Host Name property name.
     */
    public static final String PROP_VHOST_NAME = "vhostName";

    /**
     * Constant for Virtual Host IP property name.
     */
    public static final String PROP_VHOST_IP = "vhostIP";

    /**
     * Constant for the Virutal Host Velocity Template name and location.
     */
    public static final String PROP_VHOST_TEMPLATE = "vhostTemplate";

    /**
     * Creates a VirtualHost using the passed in
     * {@link VirtualHostConfiguration}.
     * 
     * @param config configuration that specifies the parameters for creation of
     *            a virtual host.
     * @param preserveExisting <code>true</code> if existing Vhosts.conf
     *            contents are to be preserved.
     * @throws Exception
     */
    public void addVirtualHost( VirtualHostConfiguration config, boolean preserveExisting )
        throws Exception;

    /**
     * Removes a Virtual Host given its name
     * 
     * @param vhost name of the vhost to remove
     * @throws Exception
     */
    public void removeVirtualHost( String vhost )
        throws Exception;

    /**
     * Creates a {@link VirtualHostConfiguration} instance from the reader.
     * 
     * @param reader
     * @return
     * @throws Exception
     */
    public List loadVirtualHostConfigurations( Reader reader )
        throws Exception;

}
