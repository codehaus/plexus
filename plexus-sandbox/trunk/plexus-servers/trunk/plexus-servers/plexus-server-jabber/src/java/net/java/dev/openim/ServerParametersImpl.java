/*
 * BSD License http://open-im.net/bsd-license.html
 * Copyright (c) 2003, OpenIM Project http://open-im.net
 * All rights reserved.
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the OpenIM project. For more
 * information on the OpenIM project, please see
 * http://open-im.net/
 */
package net.java.dev.openim;

import java.util.List;
import java.util.ArrayList;
import java.net.InetAddress;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Configuration;


/**
 * @avalon.component version="1.0" name="ServerParameters" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.ServerParameters"
 *
 * @version 1.0
 * @author AlAg
 */
public class ServerParametersImpl extends AbstractLogEnabled 
implements ServerParameters, Configurable {
    
    
    private List m_hostnameList;
    private int  m_localClientPort;
    private int  m_localSSLClientPort;
    private int  m_localServerPort;
    private int  m_localSSLServerPort;
    private int  m_remoteServerPort;
    
    
    //-------------------------------------------------------------------------
    public void configure( final Configuration configuration ) throws org.apache.avalon.framework.configuration.ConfigurationException {

        m_localClientPort = configuration.getChild( "local-client-port" ).getValueAsInteger( 5222 );
        m_localSSLClientPort = configuration.getChild( "local-ssl-client-port" ).getValueAsInteger( 5223 );
        
        m_localServerPort = configuration.getChild( "local-server-port" ).getValueAsInteger( 5269 );
        m_localSSLServerPort = configuration.getChild( "local-ssl-server-port" ).getValueAsInteger( 5270 );
        
        m_remoteServerPort = configuration.getChild( "remote-server-port" ).getValueAsInteger( 5269 );

        Configuration[] confs = configuration.getChildren( "hostname" );
        m_hostnameList = new ArrayList( confs.length );
        
        for( int i=0, l=confs.length; i<l; i++ ){
            m_hostnameList.add( confs[i].getValue() );
        }
        
        if( m_hostnameList.size() == 0 ){
            try{
                m_hostnameList.add( InetAddress.getLocalHost().getHostName() );
            } catch( java.net.UnknownHostException e ){
                throw new ConfigurationException( e.getMessage(), e );
            }
        }
        
        getLogger().info( "Host list set to: " + m_hostnameList );
    }
    
    
    
    //-------------------------------------------------------------------------
    public final int getLocalClientPort(){
        return m_localClientPort;
    }
    //-------------------------------------------------------------------------
    public final int getLocalSSLClientPort(){
        return m_localSSLClientPort;
    }
    //-------------------------------------------------------------------------
    public final int getLocalServerPort(){
        return m_localServerPort;
    }
    //-------------------------------------------------------------------------
    public final int getLocalSSLServerPort(){
        return m_localSSLServerPort;
    }
    //-------------------------------------------------------------------------
    public final List getHostNameList(){
        return m_hostnameList;
    }
    //-------------------------------------------------------------------------
    public final String getHostName(){
        return (String)m_hostnameList.get( 0 );
    }    
    //-------------------------------------------------------------------------
    public final int getRemoteServerPort(){
        return m_remoteServerPort;
    }
    //-------------------------------------------------------------------------
    
}


