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
package net.java.dev.openim.jabber;

import net.java.dev.openim.DefaultSessionProcessor;
import net.java.dev.openim.session.IMSession;


/**
 * @avalon.component version="1.0" name="Error" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.jabber.Error"
 *
 * @version 1.0
 * @author AlAg
 */
public class ErrorImpl extends DefaultSessionProcessor implements Error {


    
    public void processText( final IMSession session, final Object context ) throws Exception {
        String msg = session.getXmlPullParser().getText().trim();
        getLogger().warn( session.getId() +" / "+ msg );
        throw new java.io.EOFException( msg );
        
    }

}


