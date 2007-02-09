// implementing changes made to modulemanager
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
package org.codehaus.plexus.server.jabber;

import java.util.HashSet;
import java.util.Iterator;

import org.codehaus.plexus.server.jabber.module.ServerModule;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * @author AlAg
 * @author PV
 * @version 1.0
 * @avalon.component version="1.0" name="ModuleManager" lifestyle="singleton"
 * @avalon.service type="org.codehaus.plexus.server.jabber.ModuleManager"
 */
public class ModuleManagerImpl
    extends AbstractLogEnabled
    implements ModuleManager
{
    private HashSet m_moduleList = new HashSet();

    public void registerModule( ServerModule module )
    {
        if ( module == null )
        {
            return;
        }
        synchronized ( m_moduleList )
        {
            m_moduleList.add( module );
        }
    }

    public void unregisterModule( ServerModule module )
    {
        if ( module == null )
        {
            return;
        }
        synchronized ( m_moduleList )
        {
            m_moduleList.remove( module );
        }
    }

    public ServerModule[] getModules()
    {
        synchronized ( m_moduleList )
        {
            return (ServerModule[]) m_moduleList.toArray();
        }
    }

    public ServerModule getModule( String discoveryName )
    {
        ServerModule result = null;
        synchronized ( m_moduleList )
        {
            Iterator iter = m_moduleList.iterator();
            while ( iter.hasNext() )
            {
                ServerModule next = (ServerModule) iter.next();
                if ( next.getId().getName() == discoveryName )
                {
                    result = next;
                    break;
                }
            }
        }
        return result;
    }

    public ServerModule getModuleByHostname( String hostname )
    {
        if ( hostname == null )
        {
            return null;
        }
        synchronized ( m_moduleList )
        {
            Iterator iter = m_moduleList.iterator();
            while ( iter.hasNext() )
            {
                ServerModule next = (ServerModule) iter.next();
                if ( next.getHostNameList().contains( hostname ) )
                {
                    return next;
                }
            }
        }
        return null;
    }
}


