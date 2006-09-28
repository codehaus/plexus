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
import java.util.List;



import net.java.dev.openim.tools.XStreamStore;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.Configurable;


/**
 * @avalon.component version="1.0" name="RosterListRepositoryHolder" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.data.storage.RosterListRepositoryHolder"
 *
 * @version 1.0
 * @author AlAg
 */
public class RosterListRepositoryHolderImpl extends AbstractLogEnabled 
   implements RosterListRepositoryHolder, Contextualizable, Initializable,Configurable
{

 
   private XStreamStore      m_repository;
   private File                    m_home;
   private File                    m_storeFile;
   private String m_encoding;

    //-------------------------------------------------------------------------
    public void configure(org.apache.avalon.framework.configuration.Configuration configuration) throws org.apache.avalon.framework.configuration.ConfigurationException {
        m_encoding = configuration.getChild( "encoding" ).getValue( "UTF-8" );
    }

    public void initialize() throws java.lang.Exception {

        if( !m_home.exists() ){
            m_home.mkdirs();
        }
        if( !m_home.isDirectory() ){
            getLogger().error( "Abnormal Home Path (should be directory): " + m_home );
        }
        
        File storeFile = new File( m_home, "roster-list.xml");
        m_repository = new XStreamStore( storeFile, getLogger(),m_encoding );
        m_repository.substitute( "net.java.dev.openim.data.jabber.IMRosterItem", "item" );
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
     
    public List getRosterList( String username ) {
        List list = null;
        try{
            list = (List)m_repository.get( username );
        } catch( Exception e ){
            getLogger().debug( "User " + username + " roster list not found");
        }
        return list;
    }
    
    
    public void setRosterList(String username, List rosterList ) {
        if( username != null && rosterList != null ){
            m_repository.put( username, rosterList );
        }
    }
    
}

