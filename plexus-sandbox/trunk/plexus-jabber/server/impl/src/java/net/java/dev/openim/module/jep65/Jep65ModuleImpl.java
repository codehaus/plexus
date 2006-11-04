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
package net.java.dev.openim.module.jep65;


import net.java.dev.openim.module.ServerModule;
import net.java.dev.openim.module.ServerModuleIdentity;
import net.java.dev.openim.module.ServerModuleFeatures;
import net.java.dev.openim.SessionProcessor;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

import java.util.List;



/**
 * @author AlAg
 * @author PV
 */
public class Jep65ModuleImpl extends AbstractLogEnabled 
implements ServerModule, Serviceable, Configurable {
    
    
    
    //-------------------------------------------------------------------------
    public void configure(Configuration configuration) throws ConfigurationException {
    }
    
    //-------------------------------------------------------------------------
    public void service( ServiceManager serviceManager) throws ServiceException {
    }
     
    //-------------------------------------------------------------------------
    public ServerModuleIdentity getId() {
        return null;
    }

    public ServerModuleFeatures getFeatures() {
        return null;
    }

    public List getHostNameList() {
        return null;
    }

    public SessionProcessor getProcessor(String eventName) {
        return null; 
    }


}


