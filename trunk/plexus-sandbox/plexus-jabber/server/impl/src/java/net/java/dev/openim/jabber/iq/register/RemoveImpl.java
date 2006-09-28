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
package net.java.dev.openim.jabber.iq.register;

import java.util.Map;

import net.java.dev.openim.DefaultSessionProcessor;
import net.java.dev.openim.session.IMSession;


/**
 * @avalon.component version="1.0" name="iq.register.Remove" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.jabber.iq.register.Remove"
 *
 * @version 1.0
 * @author AlAg
 */
public class RemoveImpl extends DefaultSessionProcessor implements Remove {

    //-------------------------------------------------------------------------
    public void process( final IMSession session, final Object context ) throws Exception{
        ((Map)context).put( Query.CTX_SHOULD_REMOVE, Boolean.FALSE );
    }
    
}


