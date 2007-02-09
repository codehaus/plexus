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
package org.codehaus.plexus.server.jabber.jabber.server;


import org.codehaus.plexus.server.jabber.DefaultSessionProcessor;
import org.codehaus.plexus.server.jabber.data.jabber.IMPresence;
import org.codehaus.plexus.server.jabber.session.IMSession;

/**
 * @author AlAg
 * @version 1.0
 * @avalon.component version="1.0" name="server.Error" lifestyle="singleton"
 * @avalon.service type="org.codehaus.plexus.server.jabber.jabber.server.Error"
 */
public class ErrorImpl
    extends DefaultSessionProcessor
    implements Error
{

    public void processText( final IMSession session,
                             final Object context )
        throws Exception
    {
        String errorCodeStr = session.getXmlPullParser().getAttributeValue( "", "code" );
        if ( errorCodeStr != null && errorCodeStr.length() > 0 )
        {
            ( (IMPresence) context ).setErrorCode( Integer.parseInt( errorCodeStr ) );
        }
        ( (IMPresence) context ).setError( session.getXmlPullParser().getText().trim() );
    }

}


