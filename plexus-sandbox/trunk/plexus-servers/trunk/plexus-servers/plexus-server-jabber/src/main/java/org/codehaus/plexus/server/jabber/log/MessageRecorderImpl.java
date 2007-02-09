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
package org.codehaus.plexus.server.jabber.log;


import java.util.Date;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;

import org.codehaus.plexus.server.jabber.data.jabber.MessagePacket;
import org.codehaus.plexus.server.jabber.data.Transitable;

/**
 * @author AlAg
 * @version 1.0
 * @avalon.component version="1.0" name="MessageRecorder" lifestyle="singleton"
 * @avalon.service type="org.codehaus.plexus.server.jabber.log.MessageRecorder"
 */
public class MessageRecorderImpl
    extends AbstractLogEnabled
    implements MessageRecorder, Configurable
{

    //-------------------------------------------------------------------------
    public void configure( Configuration configuration )
        throws org.apache.avalon.framework.configuration.ConfigurationException
    {

    }


    //-------------------------------------------------------------------------
    public void record( Transitable message )
    {
        if ( getLogger().isInfoEnabled() )
        {
            if ( message instanceof MessagePacket )
            {
                getLogger().info( new Date() + " " + message.toString() );
            }
        }
    }
}


