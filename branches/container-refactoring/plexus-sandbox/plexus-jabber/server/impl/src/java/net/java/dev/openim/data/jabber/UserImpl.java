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
package net.java.dev.openim.data.jabber;

import java.util.List;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;

import net.java.dev.openim.data.Account;
import net.java.dev.openim.data.storage.AccountRepositoryHolder;
import net.java.dev.openim.data.storage.RosterListRepositoryHolder;
import net.java.dev.openim.ServerParameters;





/**
 *
 * @avalon.component version="1.0" name="User" lifestyle="transient"
 * @avalon.service type="net.java.dev.openim.data.jabber.User"
 *
 * @version 1.0
 * @author AlAg
 */
public class UserImpl extends AbstractLogEnabled implements User, Serviceable
{
    
    private String m_name;
    private String m_hostname;
    private String m_password;
    private String m_digest;
    private String m_resource;
    
    private ServiceManager m_serviceManager;
    private AccountRepositoryHolder m_accountHolder;
    private RosterListRepositoryHolder m_rosterListHolder;
    
    private ServerParameters    m_serverParameters;
    
     /**
      * @avalon.dependency type="net.java.dev.openim.ServerParameters:1.0"  key="ServerParameters"
      * @avalon.dependency type="net.java.dev.openim.data.storage.AccountRepositoryHolder:1.0" key="AccountRepositoryHolder"
      * @avalon.dependency type="net.java.dev.openim.data.storage.RosterListRepositoryHolder:1.0" key="RosterListRepositoryHolder"
      */
    //-------------------------------------------------------------------------
    public void service( ServiceManager serviceManager) throws ServiceException {
        m_serviceManager = serviceManager;
        m_serverParameters = (ServerParameters)serviceManager.lookup( "ServerParameters" );
        m_accountHolder = (AccountRepositoryHolder)serviceManager.lookup( "AccountRepositoryHolder" );
        m_rosterListHolder = (RosterListRepositoryHolder)serviceManager.lookup( "RosterListRepositoryHolder" );
        
    }
    
    //-------------------------------------------------------------------------
    public final String getName() {
        return m_name;
    }
    //-------------------------------------------------------------------------
    public final void setName(final String name) {
        m_name = name;
    }
    //-------------------------------------------------------------------------
    public final String getHostname() {
        return m_hostname;
    }
    //-------------------------------------------------------------------------
    public final void setHostname(final String hostname) {
        m_hostname = hostname;
    }
    //-------------------------------------------------------------------------
    public final void setPassword( final String password) {
        m_password = password;
    }    
    //-------------------------------------------------------------------------
    public final String getPassword() {
        return m_password;
    }
    //-------------------------------------------------------------------------
    public final String getResource() {
        return m_resource;
    }
    //-------------------------------------------------------------------------
    public final String getDigest() {
        return m_digest;
    }
    //-------------------------------------------------------------------------
    public final void setDigest(final String digest) {
        m_digest = digest;
    }
    //-------------------------------------------------------------------------
    public final void setResource(final String resource) {
        m_resource = resource;
    }
    //-------------------------------------------------------------------------
    public boolean isAuthenticationTypeSupported( final int type ) {
        Account account = m_accountHolder.getAccount( m_name );
        boolean b = false;
        if( account == null ){
            getLogger().warn( "Account " + m_name + " does not exist" );
        }
        else{
            b = account.isAuthenticationTypeSupported( type );
        }
        return b;
    }    
    //-------------------------------------------------------------------------
    public void authenticate( String sessionId ) throws Exception {
        getLogger().info( "Authenticating " + getJID() + " digest " + m_digest );
        
        Account account = m_accountHolder.getAccount( m_name );
        if( account == null ){
            throw new Exception( "Unknow JID " + getJIDAndRessource() );
        }
        
        // no password assuming digest
        if( m_password == null ){
            account.authenticate( Account.AUTH_TYPE_DIGEST, m_digest, sessionId );
        }
        
        else { // password available: plain authentification
            account.authenticate( Account.AUTH_TYPE_PLAIN, m_password, sessionId );
        }
    }
    //-------------------------------------------------------------------------
    public final String getJID() {
        String s = m_name;
        if( m_hostname != null ){
            s += "@"+m_hostname;
        }
        return s;
    }
    //-------------------------------------------------------------------------
    public final String getNameAndRessource() {
        return m_name+"/"+m_resource;
    }
    //-------------------------------------------------------------------------
    public final String getJIDAndRessource() {
        return getJID()+"/"+m_resource;
    }

    //-------------------------------------------------------------------------
    public List getRosterItemList() {
        List rosterList = m_rosterListHolder.getRosterList( m_name );
        
        // correcting hostname hack? (maybe should be removed)
        /*
        if( rosterList != null ){
            for( int i=0, l=rosterList.size(); i<l; i++ ){
                IMRosterItem item = (IMRosterItem)rosterList.get( i );
                String hostname = JIDParser.getHostname( item.getJID() );
                if( hostname == null ){
                    String name = JIDParser.getName( item.getJID() );
                    item.setJID( name+'@'+m_serverParameters.getHostName() );
                }
            }
        }
    */
        return rosterList;
    }
    //-------------------------------------------------------------------------
    public void setRosterItemList(List rosterlist) {
        m_rosterListHolder.setRosterList( m_name, rosterlist );
    }
    
    //-------------------------------------------------------------------------
    public String toString(){
        return getJIDAndRessource();
    }
    
    
    
    //-------------------------------------------------------------------------
    // should be removed when using another component container 
    // (this is a workaround due to Phoenix(?) strange behaviour
    public User newInstance(){
        UserImpl user = new UserImpl();
        user.enableLogging( getLogger() );
        try{
            user.service( m_serviceManager );
        } catch( Exception e ){
            getLogger().error( e.getMessage(), e );
        }
        return user;
    }
    
    
}

