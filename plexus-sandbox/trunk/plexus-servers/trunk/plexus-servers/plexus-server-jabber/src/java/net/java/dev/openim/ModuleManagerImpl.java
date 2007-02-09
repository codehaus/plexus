// implementing changes made to modulemanager
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

import java.util.HashSet;
import java.util.Iterator;

import net.java.dev.openim.module.ServerModule;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * @avalon.component version="1.0" name="ModuleManager" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.ModuleManager"
 *
 * @version 1.0
 * @author AlAg
 * @author PV
 */
public class ModuleManagerImpl extends AbstractLogEnabled 
    implements ModuleManager, Serviceable, Configurable {

    private HashSet m_moduleList = new HashSet();

    //-------------------------------------------------------------------------
    public void configure(Configuration configuration) throws ConfigurationException {
    }
    
    //-------------------------------------------------------------------------
    public void service( ServiceManager serviceManager) throws ServiceException {
    }
     
    public void registerModule(ServerModule module) {
        if (module == null) return;
        synchronized(m_moduleList) {
            m_moduleList.add(module);
        }
    }

    public void unregisterModule(ServerModule module) {
        if (module == null) return;
        synchronized(m_moduleList) {
            m_moduleList.remove(module);
        }
    }

    public ServerModule[] getModules() {
        synchronized(m_moduleList) {
            return (ServerModule[]) m_moduleList.toArray();
        }
    }
    
    public ServerModule getModule(String discoveryName) {
        ServerModule result = null;
        synchronized(m_moduleList) {
            Iterator iter = m_moduleList.iterator();
            while (iter.hasNext()) {
                ServerModule next = (ServerModule) iter.next();
                if (next.getId().getName() == discoveryName) {
                    result = next; break;
                }
            }
        }
        return result;
    }

    public ServerModule getModuleByHostname(String hostname) {
        if (hostname == null) return null;
        synchronized(m_moduleList) {
            Iterator iter = m_moduleList.iterator();
            while (iter.hasNext()) {
                ServerModule next = (ServerModule) iter.next();
                if (next.getHostNameList().contains(hostname)) {
                    return next;
                }
            }
        }
        return null;
    }
}


