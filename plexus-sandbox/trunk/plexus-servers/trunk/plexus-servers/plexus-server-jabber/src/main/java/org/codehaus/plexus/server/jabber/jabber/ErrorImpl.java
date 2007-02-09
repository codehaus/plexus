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
package org.codehaus.plexus.server.jabber.jabber;

import org.codehaus.plexus.server.jabber.DefaultSessionProcessor;
import org.codehaus.plexus.server.jabber.session.IMSession;


/**
 * @author AlAg
 * @version 1.0
 * @avalon.component version="1.0" name="Error" lifestyle="singleton"
 * @avalon.service type="org.codehaus.plexus.server.jabber.jabber.Error"
 */
public class ErrorImpl
    extends DefaultSessionProcessor
    implements Error
{


    public void processText( final IMSession session,
                             final Object context )
        throws Exception
    {
        String msg = session.getXmlPullParser().getText().trim();
        getLogger().warn( session.getId() + " / " + msg );
        throw new java.io.EOFException( msg );

    }

}


