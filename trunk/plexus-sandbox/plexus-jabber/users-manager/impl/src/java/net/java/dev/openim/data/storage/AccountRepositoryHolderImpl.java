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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;


import org.apache.commons.io.IOUtils;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


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
implements AccountRepositoryHolder, AccountRepositoryHolderMBean, Serviceable, Contextualizable, 
Configurable, Initializable, ThreadSafe
{

    
//    private ServiceManager      m_serviceManager;

    private Map m_repository;
    
    private XStream         m_xstream; 
    private boolean         m_regexpSearch;

    private File m_home; 
    private File m_storeFile;

    //--------------------------------------------------------------------------
    public void configure( Configuration configuration ) throws ConfigurationException {
        m_regexpSearch = configuration.getChild( "regexp-search").getValueAsBoolean( false );
        
        if( !m_home.exists() ){
            m_home.mkdirs();
        }
        if( !m_home.isDirectory() ){
            getLogger().error( "Abnormal Home Path (should be directory): " + m_home );
        }
        
        m_storeFile = new File( m_home, "accounts.xml");
    }    

    //--------------------------------------------------------------------------
    /**
     * @avalon.entry key="urn:avalon:home" type="java.io.File"
     */
    public void contextualize( Context context ) throws ContextException {
        m_home = (File) context.get( "urn:avalon:home" );
    }

    //--------------------------------------------------------------------------
    public void initialize() throws java.lang.Exception {
        m_xstream = new XStream(new DomDriver());
        m_xstream.alias( "account", AccountImpl.class );
        m_repository = loadMap( m_storeFile );
    }
    
    
    /**
     * @avalon.dependency type="net.java.dev.openim.data.Account:1.0" key="Account"
     */
    public void service(ServiceManager serviceManager) throws ServiceException {
//        m_serviceManager = serviceManager;
    }
    
    //--------------------------------------------------------------------------
    public Account getAccount( String username ) {
        Account account = null;
        try{
            if( username != null && username.length() > 0 ){
                synchronized( m_repository ){
                    if( m_repository.containsKey( username ) ){
                        account = (AccountImpl)m_repository.get( username );
                    }
                }
            }
        } 
        
        catch( Exception e ){
            getLogger().error( e.getMessage(), e );
            account = null;
        }

        if( account == null ){
            getLogger().warn( "User " + username + " not found");
        }        
        return account;
    }
    //--------------------------------------------------------------------------
    public List getAccountList( String searchPattern ){
        List list = new ArrayList();
        if( !m_regexpSearch ){
            searchPattern = searchPattern.replaceAll( "\\*", ".*" );
        }

        try{
            synchronized( m_repository ){
                Iterator iter = m_repository.values().iterator();            
                while ( iter.hasNext() ){
                    String name = iter.next().toString();
                    if( name.matches( searchPattern ) ){
                        Account account = (Account)m_repository.get( name );
                        list.add( account );
                    }
                }
            }
        } catch( Exception e ){
            getLogger().warn( e.getMessage(), e );
        }
        
        return list;
    }
    //--------------------------------------------------------------------------
    public Account removeAccount( String username ) {
        Account account = null;
        synchronized( m_repository  ){
            account = (Account)m_repository.remove( username  );
            if( account != null ){
                saveMap( m_storeFile, m_repository );
            }
            else{
                getLogger().warn( "User " + username + " not found");
            }
        }
            
        return account;
    }
    
    //--------------------------------------------------------------------------
    public void setAccount(Account userAccount) {
        AccountImpl account = new AccountImpl();
        
        account.setName( userAccount.getName() );
        account.setPassword( userAccount.getPassword() );
        
        getLogger().debug( "Setting account in repository " + account );
        synchronized( m_repository ){
            m_repository.put( account.getName(), account );
            saveMap( m_storeFile, m_repository );
        }
    }
    
    //--------------------------------------------------------------------------
    public void setAccount(String accountStr) {
        try{
            Account account = new AccountImpl();
            int index = accountStr.indexOf( '/' );
            if( index < 0 ){
                account.setName( accountStr );
                account.setPassword( accountStr );
            }
            else{
                account.setName( accountStr.substring( 0, index ) );
                account.setPassword( accountStr.substring( index+1 ) );
            }
            setAccount( account );
        } catch( Exception e ){
            getLogger().warn( e.getMessage(), e );
        }
    }
    
    //--------------------------------------------------------------------------
    public List getAccountList() {
        List list = new ArrayList();
        synchronized( m_repository ){
            Iterator iter = m_repository.values().iterator();
            while( iter.hasNext() ){
                Object o = iter.next();
                getLogger().debug( "Item " + o + " account " + getAccount( o.toString() ) );
                list.add( o );
            }
        }
        return list;
    }
    
    
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    private void saveMap( File file, Map map ){
        String xstreamData = m_xstream.toXML( map );
        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream( file );
            fos.write( xstreamData.getBytes() );
        }
        catch( IOException e ){
            getLogger().error( e.getMessage(), e );
        }
        finally{
            if( fos != null ){
                try{
                    fos.close();
                }
                catch( IOException e ){
                    getLogger().error( e.getMessage() );
                }
            }
        }
        
    }
    //--------------------------------------------------------------------------
    private Map loadMap( File file ){
        Map map = null;
        
        if( file.exists() ){
            try{
                FileInputStream fis = new FileInputStream( file );
                String xmlData = IOUtils.toString( fis );
                fis.close();
                map = (Map)m_xstream.fromXML( xmlData );
            }
            catch( Exception e ){
                getLogger().error( e.getMessage(), e );
            }
            
        }
        else{
            getLogger().info( "No " + file + " => starting with void user list" );
            map = new HashMap();
        }
        
        return map;
    }    
    

}

