/*
 * BSD License http://open-im.org/bsd-license.html
 * Copyright (c) 2003, OpenIM Project http://open-im.org
 * All rights reserved.
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the OpenIM project. For more
 * information on the OpenIM project, please see
 * http://open-im.org/
 */
package org.codehaus.plexus.server.jabber.data.storage;

import java.io.File;
import java.util.List;


import org.codehaus.plexus.server.jabber.tools.XStreamStore;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;


/**
 * @author AlAg
 * @version 1.0
 * @avalon.component version="1.0" name="DeferrableListRepositoryHolder" lifestyle="singleton"
 * @avalon.service type="org.codehaus.plexus.server.jabber.data.storage.DeferrableListRepositoryHolder"
 */
public class DeferrableListRepositoryHolderImpl
    extends AbstractLogEnabled
    implements DeferrableListRepositoryHolder, Contextualizable, Initializable
{

    private XStreamStore m_repository;
    private File m_home;


    public void initialize()
        throws java.lang.Exception
    {

        if ( !m_home.exists() )
        {
            m_home.mkdirs();
        }
        if ( !m_home.isDirectory() )
        {
            getLogger().error( "Abnormal Home Path (should be directory): " + m_home );
        }

        File storeFile = new File( m_home, "deferrable-list.xml" );
        m_repository = new XStreamStore( storeFile, getLogger() );
        m_repository.substitute( "org.codehaus.plexus.server.jabber.data.jabber.IMMessage", "message" );
        m_repository.load();

    }

    //--------------------------------------------------------------------------
    /** @avalon.entry key="urn:avalon:home" type="java.io.File" */
    public void contextualize( Context context )
        throws ContextException
    {
        m_home = (File) context.get( "urn:avalon:home" );
    }


    //--------------------------------------------------------------------------     
    public List getDeferrableList( String username )
    {
        List list = null;
        try
        {
            list = (List) m_repository.get( username );
        }
        catch ( Exception e )
        {
            getLogger().warn( "User " + username + " message list not found" );
        }
        return list;
    }

    //--------------------------------------------------------------------------    
    public void setDeferrableList( String username,
                                   List deferrableList )
    {
        if ( username != null && deferrableList != null )
        {
            m_repository.put( username, deferrableList );
        }
    }

}

