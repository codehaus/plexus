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

import java.io.IOException;
import net.java.dev.openim.session.SessionsManager;
import net.java.dev.openim.session.IMServerSession;

/**
 * @version 1.0
 * @author AlAg
 */
public interface S2SConnector extends Runnable {
    
    public void setToHostname( String toHostname );    
    public void setRouter( IMRouter router );    
    public void setIMConnectionHandler( IMConnectionHandler connectionHandler );
    public void setSessionsManager( SessionsManager sessionManager );
    
    public IMServerSession getSession() throws Exception ;
    public void sendResult() throws IOException;
    public void sendVerify( String dialbackValue, String id ) throws IOException;
    
    public void run() ;
    public boolean isAlive() ;
    
} // class
