/**
 * 
 */
package org.codehaus.plexus.xsiter.vhost;

import java.io.File;
import java.io.FileWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 * @plexus.component role-hint="default"
 */
public class DefaultApacheVirtualHostManager
    extends AbstractLogEnabled
    implements VirtualHostManager
{

    /**
     * @see org.codehaus.plexus.xsiter.vhost.VirtualHostManager#addVirtualHost(org.codehaus.plexus.xsiter.vhost.VirtualHostConfiguration,
     *      boolean)
     */
    public void addVirtualHost( VirtualHostConfiguration config, boolean preserveExisting )
        throws Exception
    {
        Velocity.setProperty( Velocity.RUNTIME_LOG_LOGSYSTEM, this );
        try
        {
            Velocity.init();
        }
        catch ( Exception e1 )
        {
            if ( getLogger().isErrorEnabled() )
                getLogger().error( "Error initializing Velocity engine." );
            throw new Exception( "Error initializing Velocity engine." );
        }

        String vhostIP = config.getVhostIP();
        String vhostName = config.getVhostName();
        String vhostLogDirectory = config.getVhostLogDirectory();
        String vhostConnectorProtocol = config.getVhostConnectorProtocol();
        String vhostConnectorPort = config.getVhostConnectorPort();

        VelocityContext context = new VelocityContext();
        // setup the Properties in Velocity Context.
        context.put( PROP_VHOST_IP, vhostIP );
        context.put( PROP_VHOST_NAME, vhostName );
        context.put( PROP_VHOST_LOG_DIRECTORY, vhostLogDirectory );
        context.put( PROP_VHOST_CONNECTOR_PROTOCOL, vhostConnectorProtocol );
        context.put( PROP_VHOST_CONNECTOR_PORT, vhostConnectorPort );

        // merge and write the output
        Template template = null;

        template = Velocity.getTemplate( config.getVhostTemplate() );
        StringWriter sw = new StringWriter();
        String vhostDirectory = config.getVhostDirectory();
        // check if the vhost directory exists
        if ( !new File( vhostDirectory ).exists() )
        {
            getLogger().info( "Creating new vhost directory '" + vhostDirectory + "';" );
            FileUtils.mkdir( vhostDirectory );
        }

        FileWriter fw = new FileWriter( new File( vhostDirectory, "vhosts.conf" ), preserveExisting );
        // All went well if we are here.
        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( PROP_VHOST_IP + " : " + vhostIP );
            getLogger().debug( PROP_VHOST_NAME + " : " + vhostName );
            getLogger().debug( PROP_VHOST_LOG_DIRECTORY + " : " + vhostDirectory );
            getLogger().debug( PROP_VHOST_CONNECTOR_PROTOCOL + " : " + vhostConnectorProtocol );
            getLogger().debug( PROP_VHOST_CONNECTOR_PORT + " : " + vhostConnectorPort );
        }

        template.merge( context, sw );

        StringBuffer sb = new StringBuffer();
        sb.append( "# Virtual Host :: " + vhostName + "\n" );
        sb.append( sw.getBuffer() );
        sb.append( "\n" );
        if ( getLogger().isInfoEnabled() )
            getLogger().info( sb.toString() );

        // write out to the file
        fw.write( sb.toString() );
        fw.close();
        sw.close();
        // create the vhost log directory if it does not exist
        if ( !new File( vhostLogDirectory ).exists() )
        {
            if ( getLogger().isInfoEnabled() )
                getLogger().info( "Creating new vhost log directory '" + vhostLogDirectory + "'" );
            FileUtils.mkdir( vhostLogDirectory );
        }

    }

    /**
     * @see org.codehaus.plexus.xsiter.vhost.VirtualHostManager#removeVirtualHost(java.lang.String)
     */
    public void removeVirtualHost( String vhost )
        throws Exception
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException( "Not yet implemented" );
    }

    /**
     * @see org.codehaus.plexus.xsiter.vhost.VirtualHostManager#loadVirtualHostConfigurations(java.io.Reader)
     */
    public List loadVirtualHostConfigurations( Reader reader )
        throws Exception
    {
        Xpp3Dom xmlConfig = Xpp3DomBuilder.build( reader );
        Xpp3Dom[] children = xmlConfig.getChildren( "vhost" );
        List list = new ArrayList();
        for ( int i = 0; i < children.length; i++ )
        {
            Xpp3Dom vhostXml = children[i];

            // verify all required properties exist
            StringBuffer sb = new StringBuffer();
            if ( null == vhostXml.getChild( PROP_VHOST_ID ) )
                sb.append( "\nMissing Virtual Host configuration element: " + PROP_VHOST_ID );
            if ( null == vhostXml.getChild( PROP_VHOST_NAME ) )
                sb.append( "\nMissing Virtual Host configuration element: " + PROP_VHOST_NAME );
            if ( null == vhostXml.getChild( PROP_VHOST_CONNECTOR_PORT ) )
                sb.append( "\nMissing Virtual Host configuration element: " + PROP_VHOST_CONNECTOR_PORT );
            if ( null == vhostXml.getChild( PROP_VHOST_CONNECTOR_PROTOCOL ) )
                sb.append( "\nMissing Virtual Host configuration element: " + PROP_VHOST_CONNECTOR_PROTOCOL );
            if ( null == vhostXml.getChild( PROP_VHOST_DIRECTORY ) )
                sb.append( "\nMissing Virtual Host configuration element: " + PROP_VHOST_DIRECTORY );
            if ( null == vhostXml.getChild( PROP_VHOST_LOG_DIRECTORY ) )
                sb.append( "\nMissing Virtual Host configuration element: " + PROP_VHOST_LOG_DIRECTORY );
            if ( null == vhostXml.getChild( PROP_VHOST_IP ) )
                sb.append( "\nMissing Virtual Host configuration element: " + PROP_VHOST_IP );
            if ( null == vhostXml.getChild( PROP_VHOST_TEMPLATE ) )
                sb.append( "\nMissing Virtual Host configuration element: " + PROP_VHOST_TEMPLATE );

            if ( sb.length() > 0 )
                throw new Exception( sb.toString() );

            VirtualHostConfiguration config = new VirtualHostConfiguration();
            config.setId( vhostXml.getChild( PROP_VHOST_ID ).getValue() );
            config.setVhostName( vhostXml.getChild( PROP_VHOST_NAME ).getValue() );
            config.setVhostConnectorPort( vhostXml.getChild( PROP_VHOST_CONNECTOR_PORT ).getValue() );
            config.setVhostConnectorProtocol( vhostXml.getChild( PROP_VHOST_CONNECTOR_PROTOCOL ).getValue() );
            config.setVhostDirectory( vhostXml.getChild( PROP_VHOST_DIRECTORY ).getValue() );
            config.setVhostLogDirectory( vhostXml.getChild( PROP_VHOST_LOG_DIRECTORY ).getValue() );
            config.setVhostIP( vhostXml.getChild( PROP_VHOST_IP ).getValue() );
            config.setVhostTemplate( vhostXml.getChild( PROP_VHOST_TEMPLATE ).getValue() );

            list.add( config );
        }

        return list;
    }

}
