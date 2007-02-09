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

import java.util.*;


import org.apache.avalon.framework.logger.AbstractLogEnabled;

import net.java.dev.openim.data.jabber.IMPresence;
import net.java.dev.openim.tools.JIDParser;




/**
 *
 * @avalon.component version="1.0" name="IMPresenceHolder" lifestyle="singleton"
 * @avalon.service type="net.java.dev.openim.IMPresenceHolder"
 *
 * @version 1.0
 * @author AlAg
 * @author PV
 */
public class IMPresenceHolderImpl extends AbstractLogEnabled implements IMPresenceHolder {

    Map m_presenceMap = new HashMap();
    Set m_presenceListeners = new HashSet();

    public void setPresence( String jid, IMPresence presence ){
        synchronized( m_presenceMap ){
            String name = JIDParser.getName( jid );
            Map map = (Map)m_presenceMap.get( name );
            if( map == null ){
                map = new HashMap();
            }
            map.put( jid, presence );
            m_presenceMap.put( name, map );
        }
        synchronized(m_presenceListeners) {
            Iterator iListeners = m_presenceListeners.iterator();
            while (iListeners.hasNext()) {
                IMPresenceListener listener = (IMPresenceListener) iListeners.next();
                listener.onSetPresence(jid, presence);
            }
        }
    }
    public Collection getPresence( String jid ){
        Collection col = null;
        synchronized( m_presenceMap ){
            String name = JIDParser.getName( jid );
            Map map = (Map)m_presenceMap.get( name );
            if( map != null ){
                col = map.values();
            }
        }
        return col;
    }
    
    public IMPresence removePresence( String jid ) {
        IMPresence presence = null;
        synchronized( m_presenceMap ){
            String name = JIDParser.getName( jid );
            Map map = (Map)m_presenceMap.get( name );
            if( map != null ){
                presence = (IMPresence)map.remove( jid );
                if( map.isEmpty() ){
                    m_presenceMap.remove( map );
                }
            }
        }
        synchronized(m_presenceListeners) {
            Iterator iListeners = m_presenceListeners.iterator();
            while (iListeners.hasNext()) {
                IMPresenceListener listener = (IMPresenceListener) iListeners.next();
                listener.onRemovePresence(jid);
            }
        }
        return presence;
    }

    public void registerListener(IMPresenceListener listener) {
        synchronized (m_presenceListeners) {
            m_presenceListeners.add(listener);
        }
    }

    public void unregisterListener(IMPresenceListener listener) {
        synchronized(m_presenceListeners) {
            m_presenceListeners.remove(listener);
        }
    }
}


