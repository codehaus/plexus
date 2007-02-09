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
package net.java.dev.openim;

import java.util.List;

import net.java.dev.openim.data.Transitable;
import net.java.dev.openim.session.IMClientSession;
import net.java.dev.openim.session.IMSession;

/**
 * @author AlAg
 * @version 1.0
 */
public interface IMRouter
{
    public void setS2SConnectorManager( S2SConnectorManager s2sConnectorManager );

    public S2SConnectorManager getS2SConnectorManager();

    // client session related
    public void registerSession( IMClientSession session );

    public void unregisterSession( IMClientSession session );

    public List getAllRegisteredSession( String username );

    public void releaseSessions();

    public void route( IMSession session,
                       Transitable message )
        throws java.io.IOException;


}
