package org.codehaus.plexus.security.request;

import java.util.List;
import java.util.Vector;

/**
  * Convenience class to register and notify RequestListeners.
  * 
  * <p>Created on 20/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class RequestListenerNotifier
{
    private List listeners = new Vector();

    /**
     * 
     */
    public RequestListenerNotifier()
    {
        super();
    }

    public synchronized void registerListener(RequestListener listener)
    {
        if (listener == null)
        {
            return;
        }
        listeners.add(listener);
    }

    public synchronized void unRegisterListener(RequestListener listener)
    {
        if (listener == null)
        {
            return;
        }
        listeners.remove(listener);
    }

    public void sendRequestBegun(RequestEvent event)
    {
        Object[] list = listeners.toArray();
        for (int i = 0; i < list.length; i++)
        {
            ((RequestListener) list[i]).requestBegun(event);
        }
    }

    public void sendRequestEnded(RequestEvent event)
    {
        Object[] list = listeners.toArray();
        for (int i = 0; i < list.length; i++)
        {
            ((RequestListener) list[i]).requestEnded(event);
        }
    }
}
