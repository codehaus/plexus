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

import net.java.dev.openim.tools.XStreamStore;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;


/**
 * @avalon.component version="1.0" name="PrivateRepositoryHolder" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.data.storage.PrivateRepositoryHolder"
 *
 * @version 1.0
 * @author AlAg
 */
public class PrivateRepositoryHolderImpl extends AbstractLogEnabled 
implements PrivateRepositoryHolder, Contextualizable, Initializable
{


    private XStreamStore      m_repository;
    private File                    m_home;
    private File                    m_storeFile;



    public void initialize() throws java.lang.Exception {

        if( !m_home.exists() ){
            m_home.mkdirs();
        }
        if( !m_home.isDirectory() ){
            getLogger().error( "Abnormal Home Path (should be directory): " + m_home );
        }
        
        File storeFile = new File( m_home, "private.xml");
        m_repository = new XStreamStore( storeFile, getLogger() );
        m_repository.load();
    }    

    //--------------------------------------------------------------------------
    /**
     * @avalon.entry key="urn:avalon:home" type="java.io.File"
     */
    public void contextualize( Context context ) throws ContextException {
        m_home = (File) context.get( "urn:avalon:home" );
    }

    
    
    //--------------------------------------------------------------------------
    public String getData( String username, String key ) {
        String data = null;
        try{
            String repKey = username+"::"+key;
            data = (String)m_repository.get( repKey );
            getLogger().debug( "Get key: " + repKey +" => " + data );
        } catch( Exception e ){
            getLogger().debug( "Username " + username + " dont have private for element " + key );
        }
        return data;
    }
    
    public void setData(String username, String key, String data) {
        String repKey = username+"::"+key;
        getLogger().debug( "Put key: " + repKey +" => " + data );
        m_repository.put( repKey , data );
    }
    
    

    
}

