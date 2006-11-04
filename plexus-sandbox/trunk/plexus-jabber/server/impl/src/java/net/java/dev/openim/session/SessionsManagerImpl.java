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
package net.java.dev.openim.session;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;


/**
 * @avalon.component version="1.0" name="SessionsManager" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.session.SessionsManager"
 *
 * @version 1.0
 * @author AlAg
 */
public class SessionsManagerImpl extends AbstractLogEnabled implements SessionsManager, Serviceable, Initializable {
    
    
    private Map m_sessionSetByHostName;

    private ServiceManager  m_serviceManager;

    // We really need this to be able to also shutdown non registered sessions
    private HashMap m_activeSessions;
    /**
     * @avalon.dependency type="net.java.dev.openim.session.IMClientSession:1.0" key="IMClientSession"
     * @avalon.dependency type="net.java.dev.openim.session.IMServerSession:1.0" key="IMServerSession"
     */
    public void service( ServiceManager serviceManager ) throws org.apache.avalon.framework.service.ServiceException {
        m_serviceManager = serviceManager;
    }

   public void initialize() throws java.lang.Exception {
      m_activeSessions = new HashMap();
   }
    
    //-------------------------------------------------------------------------
    public IMServerSession getNewServerSession() throws Exception {
		IMServerSession session = (IMServerSession)m_serviceManager.lookup( "IMServerSession" );
                // Are server session even unregistered?
         return session;
    }
	//-------------------------------------------------------------------------
	public IMClientSession getNewClientSession() throws Exception {
		IMClientSession session = (IMClientSession)m_serviceManager.lookup( "IMClientSession" );
                synchronized(m_activeSessions) {
                   m_activeSessions.put(new Long(session.getId()), session); 
                }
		 return session;
	}
    //-------------------------------------------------------------------------
    public void release( IMSession session ){
        if( session != null ){
            try{
                if( !session.isClosed() ){
                    session.close();
                }
                else{
                    getLogger().warn( "Session "+ session.getId() +" already diposed" );
                }
            } catch( Exception e ){
                getLogger().warn( "Session "+ session.getId() +" release failure " + e.getMessage(), e );
            } 
            // Remove from sessionsMap
            synchronized(m_activeSessions) {
               m_activeSessions.remove( new Long( session.getId()) );
            }
        } // if
    }

    
    //-------------------------------------------------------------------------
    public void  releaseSessions() {
       getLogger().debug( "Releasing sessions " );
       // Avoid concurrent mod
       Map clonedSessions = (Map)m_activeSessions.clone();
       Iterator it = clonedSessions.values().iterator();
       while ( it.hasNext()) {
          IMSession sess = (IMSession)it.next();
          release( sess );
       } // end of while ()
    }
    
}


