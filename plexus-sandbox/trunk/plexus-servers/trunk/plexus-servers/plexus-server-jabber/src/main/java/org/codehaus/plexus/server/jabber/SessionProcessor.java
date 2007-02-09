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
package org.codehaus.plexus.server.jabber;

import org.codehaus.plexus.server.jabber.session.IMSession;


/** @author AlAg */
public interface SessionProcessor
{
    public void process( IMSession session )
        throws Exception;

    public void process( IMSession session,
                         Object context )
        throws Exception;
}


