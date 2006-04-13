package org.codehaus.plexus.security.session;

import java.util.List;
import java.util.Vector;

/**
  * Convenience class to register and notify SessionLifecycleListeners.
  * 
  * <p>Created on 20/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class SessionListenerNotifier
{
    private List listeners = new Vector();

    /**
     * 
     */
    public SessionListenerNotifier()
    {
        super();
    }

    public synchronized void registerListener(SessionLifecycleListener listener)
    {
        if (listener == null)
        {
            return;
        }
        listeners.add(listener);
    }

    public synchronized void unRegisterListener(SessionLifecycleListener listener)
    {
        if (listener == null)
        {
            return;
        }
        listeners.remove(listener);
    }

    public void sessionCreated(SessionLifecycleEvent event)
    {
        Object[] list = listeners.toArray();
        for (int i = 0; i < list.length; i++)
        {
            ((SessionLifecycleListener) list[i]).sessionCreated(event);
        }
    }

    public void sessionDestroyed(SessionLifecycleEvent event)
    {
        Object[] list = listeners.toArray();
        for (int i = 0; i < list.length; i++)
        {
            ((SessionLifecycleListener) list[i]).sessionDestroyed(event);
        }
    }
}
