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
package net.java.dev.openim.log;

import java.util.Date;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;


import net.java.dev.openim.data.jabber.MessagePacket;

import net.java.dev.openim.data.Transitable;


/**
 * @avalon.component version="1.0" name="MessageLogger" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.log.MessageLogger"
 *
 * @version 1.0
 * @author AlAg
 */

public class MessageLoggerImpl
extends AbstractLogEnabled implements MessageLogger, Configurable {

    
    //-------------------------------------------------------------------------
    public void configure(Configuration configuration) 
    throws org.apache.avalon.framework.configuration.ConfigurationException {
        
    }
    
    //-------------------------------------------------------------------------
    public void log( Transitable message ){
        if( getLogger().isInfoEnabled() ){
            if( message instanceof MessagePacket ){
                MessagePacket m = (MessagePacket)message;
                getLogger().info( new Date() + " "+m.getFrom() + " " + m.getTo() + " " + m.toString().length() );
            }
        }
    }
}


