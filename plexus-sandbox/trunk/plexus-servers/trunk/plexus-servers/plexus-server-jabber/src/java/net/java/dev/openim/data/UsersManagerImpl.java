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
package net.java.dev.openim.data;


import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;

import org.apache.avalon.framework.logger.AbstractLogEnabled;

import net.java.dev.openim.data.jabber.User;

/**
 * @avalon.component version="1.0" name="UsersManager" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.data.UsersManager"
 *
 * @version 1.0
 * @author AlAg
 */
public class UsersManagerImpl extends AbstractLogEnabled
implements UsersManager, Serviceable {
    
    

    private ServiceManager  m_serviceManager;
    /**
     * @avalon.dependency type="net.java.dev.openim.data.jabber.User:1.0" key="User"
     */
    public void service( ServiceManager serviceManager ) throws org.apache.avalon.framework.service.ServiceException {
        m_serviceManager = serviceManager;
    }

 
    
    //-------------------------------------------------------------------------
    public User getNewUser() throws Exception {
        return (User)m_serviceManager.lookup( "User" );
    }
 
    
}


