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
package org.codehaus.plexus.server.jabber.jabber.iq.auth;


import org.codehaus.plexus.server.jabber.DefaultSessionProcessor;
import org.codehaus.plexus.server.jabber.session.IMClientSession;
import org.codehaus.plexus.server.jabber.session.IMSession;


/**
 * @author AlAg
 * @version 1.0
 * @avalon.component version="1.0" name="iq.auth.Resource" lifestyle="singleton"
 * @avalon.service type="org.codehaus.plexus.server.jabber.jabber.iq.auth.Resource"
 */
public class ResourceImpl
    extends DefaultSessionProcessor
    implements Resource
{

    public void processText( final IMSession session,
                             final Object context )
        throws Exception
    {
        ( (IMClientSession) session ).getUser().setResource( session.getXmlPullParser().getText().trim() );
    }


}


