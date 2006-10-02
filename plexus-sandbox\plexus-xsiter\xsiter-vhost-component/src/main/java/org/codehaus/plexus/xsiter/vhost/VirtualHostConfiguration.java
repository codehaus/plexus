/**
 * 
 */
package org.codehaus.plexus.xsiter.vhost;

/**
 * Configuration POJO that wraps up the parameters required to create a Virtual
 * Host.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @dated: 14/09/2006, 12:12:00 PM
 */
public class VirtualHostConfiguration
{

    /**
     * Identifier for this {@link VirtualHostConfiguration} instance.
     */
    private String id;

    /**
     * Path the velocity template to use for Apache Virtual Host Config
     * generation.
     * 
     * @parameter
     * @required
     */
    private String vhostTemplate;

    /**
     * Path where the resulting vhost configuration will be output.
     * 
     * @parameter alias="vhostDir" default-value="deploy/apache/"
     * @required
     */
    private String vhostDirectory;

    /**
     * IP to user for setting up IP based vhosts.
     * 
     * @parameter default-value="127.0.0.1"
     * @required
     */
    private String vhostIP;

    /**
     * Name for the virtual host.
     * 
     * @parameter default-name="${artifactId}"
     * @required
     */
    private String vhostName;

    /**
     * Location to the base directory where virtual host logs are generated.
     * 
     * @parameter default-value="${basedir}/deploy/apache/logs"
     * @required
     */
    private String vhostLogDirectory;

    /**
     * Protocol to be used by the Apache-[appserver] connector.
     * <p>
     * Defaults to Tomcat AJP Connector.
     * 
     * @parameter default-value="ajp"
     * @required
     */
    private String vhostConnectorProtocol;

    /**
     * Port number to used by Apache to talk to an Appserver's connector.
     * 
     * @parameter
     * @required
     */
    private String vhostConnectorPort;

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId( String id )
    {
        this.id = id;
    }

    /**
     * @return the vhostConnectorPort
     */
    public String getVhostConnectorPort()
    {
        return vhostConnectorPort;
    }

    /**
     * @param vhostConnectorPort the vhostConnectorPort to set
     */
    public void setVhostConnectorPort( String vhostConnectorPort )
    {
        this.vhostConnectorPort = vhostConnectorPort;
    }

    /**
     * @return the vhostConnectorProtocol
     */
    public String getVhostConnectorProtocol()
    {
        return vhostConnectorProtocol;
    }

    /**
     * @param vhostConnectorProtocol the vhostConnectorProtocol to set
     */
    public void setVhostConnectorProtocol( String vhostConnectorProtocol )
    {
        this.vhostConnectorProtocol = vhostConnectorProtocol;
    }

    /**
     * @return the vhostDirectory
     */
    public String getVhostDirectory()
    {
        return vhostDirectory;
    }

    /**
     * @param vhostDirectory the vhostDirectory to set
     */
    public void setVhostDirectory( String vhostDirectory )
    {
        this.vhostDirectory = vhostDirectory;
    }

    /**
     * @return the vhostIP
     */
    public String getVhostIP()
    {
        return vhostIP;
    }

    /**
     * @param vhostIP the vhostIP to set
     */
    public void setVhostIP( String vhostIP )
    {
        this.vhostIP = vhostIP;
    }

    /**
     * @return the vhostLogDirectory
     */
    public String getVhostLogDirectory()
    {
        return vhostLogDirectory;
    }

    /**
     * @param vhostLogDirectory the vhostLogDirectory to set
     */
    public void setVhostLogDirectory( String vhostLogDirectory )
    {
        this.vhostLogDirectory = vhostLogDirectory;
    }

    /**
     * @return the vhostName
     */
    public String getVhostName()
    {
        return vhostName;
    }

    /**
     * @param vhostName the vhostName to set
     */
    public void setVhostName( String vhostName )
    {
        this.vhostName = vhostName;
    }

    /**
     * @return the vhostTemplate
     */
    public String getVhostTemplate()
    {
        return vhostTemplate;
    }

    /**
     * @param vhostTemplate the vhostTemplate to set
     */
    public void setVhostTemplate( String vhostTemplate )
    {
        this.vhostTemplate = vhostTemplate;
    }

}
