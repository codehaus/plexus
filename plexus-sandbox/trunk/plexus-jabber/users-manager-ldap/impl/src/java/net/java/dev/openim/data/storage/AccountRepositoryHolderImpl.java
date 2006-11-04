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
package net.java.dev.openim.data.storage;


import java.util.Hashtable;
import java.util.List;


import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;


import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;


import net.java.dev.openim.data.Account;
import net.java.dev.openim.data.AccountImpl;

/**
 * @avalon.component version="1.0" name="AccountRepositoryHolder" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.data.storage.AccountRepositoryHolder"
 *
 * @version 1.0
 * @author AlAg
 */
public class AccountRepositoryHolderImpl extends AbstractLogEnabled 
implements AccountRepositoryHolder, AccountRepositoryHolderMBean, Serviceable, 
Configurable, Initializable, ThreadSafe
{

    private ServiceManager      m_serviceManager;

    private DirContext m_dirContext;     
    private Hashtable m_confEnv; 
    private String m_lookupDir;
    private String m_attribSearch;
    private String m_ldapPasswordAttribute;
    
    //--------------------------------------------------------------------------
    public void configure( Configuration configuration ) throws ConfigurationException {
        m_confEnv = new Hashtable();
        
        String initCtxFactory = configuration.getChild( "initial-context-factory" ).getValue();
        if( initCtxFactory != null ){
            m_confEnv.put( Context.INITIAL_CONTEXT_FACTORY,  initCtxFactory );
        }
        
        String securityAuthentication = configuration.getChild( "security-authentication" ).getValue("simple");
        m_confEnv.put( Context.SECURITY_AUTHENTICATION,  securityAuthentication );
        
        String providerUrl = configuration.getChild( "provider-url" ).getValue( null );
        if( providerUrl != null ){
            m_confEnv.put( Context.PROVIDER_URL, providerUrl );
        }
        
        
        String securityPrincipal = configuration.getChild( "security-principal" ).getValue( null );
        String securityCredentials = configuration.getChild( "security-credentials" ).getValue( null );
        if( securityPrincipal != null && securityCredentials != null ){
            m_confEnv.put( Context.SECURITY_PRINCIPAL, securityPrincipal);
            m_confEnv.put( Context.SECURITY_CREDENTIALS, securityCredentials );
        }
        
        m_lookupDir = configuration.getChild( "ldap-lookup-directory" ).getValue();
        m_attribSearch = configuration.getChild( "ldap-attribute-search" ).getValue();
        m_ldapPasswordAttribute = configuration.getChild( "ldap-password-attribute" ).getValue();
    }    

    //--------------------------------------------------------------------------
    public void initialize() throws java.lang.Exception {
        m_dirContext = new InitialDirContext( m_confEnv );
    }
    
    //--------------------------------------------------------------------------    
    /**
     * @avalon.dependency type="net.java.dev.openim.data.Account:1.0" key="Account"
     * @avalon.dependency type="org.apache.avalon.cornerstone.services.store.Store:1.0" key="Store"
     */
    public void service(ServiceManager serviceManager) throws ServiceException {
        m_serviceManager = serviceManager;
    }
    
    //--------------------------------------------------------------------------
    public Account getAccount( String username ) {
        getLogger().debug( "Get username "+username+" account" );
        Account account = null;
        try{
            DirContext context = (DirContext)m_dirContext.lookup( m_lookupDir );
            NamingEnumeration ne = context.search( "", new BasicAttributes( m_attribSearch, username ) );
            SearchResult sr = (SearchResult)ne.nextElement();
            Attributes attr = sr.getAttributes(); 
            Attribute passwordAttrib = attr.get( m_ldapPasswordAttribute );      
            account = new AccountImpl();
            account.setName( username );
            
            Object value = passwordAttrib.get();
            if (value instanceof byte[]){
                account.setPassword( new String((byte[]) value) );
            }
            else{
                account.setPassword( value.toString() );
            }
            
        } catch( Exception e ){
            getLogger().debug( e.getMessage(), e );
            getLogger().warn( "User " + username + " not found");
        }
        return account;
    }
    
    //--------------------------------------------------------------------------
    public List getAccountList( String searchPattern ){
        throw new UnsupportedOperationException("Not implemented");    
    }
    //--------------------------------------------------------------------------
    public Account removeAccount( String username ) {
        throw new UnsupportedOperationException("Not implemented (LDAP read only access)");
    }
    
    //--------------------------------------------------------------------------
    public void setAccount(Account account) {
        throw new UnsupportedOperationException("Not implemented (LDAP read only access)");
    }
    
    //--------------------------------------------------------------------------
    public void setAccount(String accountStr) {
        throw new UnsupportedOperationException("Not implemented (LDAP read only access)");
    }
    
    //--------------------------------------------------------------------------
    public List getAccountList() {
        throw new UnsupportedOperationException("Not implemented");
    }
    
}

