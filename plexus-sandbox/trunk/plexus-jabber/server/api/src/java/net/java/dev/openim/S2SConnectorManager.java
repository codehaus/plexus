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

import net.java.dev.openim.session.IMServerSession;

/**
 * @version 1.0
 * @author AlAg
 */
public interface S2SConnectorManager {
    
    public void setConnectionHandler( IMConnectionHandler connectionHandler );
    
    public IMServerSession getCurrentRemoteSession( String hostname ) throws Exception;
    public IMServerSession getRemoteSessionWaitForValidation( String hostname, long timeout ) throws Exception;
    
    public void verifyRemoteHost( String hostname, String dialbackValue, String id, IMServerSession session ) throws Exception;
} // class
