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

import java.io.IOException;

import org.codehaus.plexus.server.jabber.session.SessionsManager;
import org.codehaus.plexus.server.jabber.session.IMServerSession;

/**
 * @author AlAg
 * @version 1.0
 */
public interface S2SConnector
    extends Runnable
{

    public void setToHostname( String toHostname );

    public void setRouter( IMRouter router );

    public void setIMConnectionHandler( IMConnectionHandler connectionHandler );

    public void setSessionsManager( SessionsManager sessionManager );

    public IMServerSession getSession()
        throws Exception;

    public void sendResult()
        throws IOException;

    public void sendVerify( String dialbackValue,
                            String id )
        throws IOException;

    public void run();

    public boolean isAlive();

} // class
