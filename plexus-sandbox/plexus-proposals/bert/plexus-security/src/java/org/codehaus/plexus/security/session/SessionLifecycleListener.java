package org.codehaus.plexus.security.session;

import java.util.EventListener;

/**
  * Objects wishing to be notified of session lifecycle events should implement
  * this interface
  * 
  * <p>Created on 16/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public interface SessionLifecycleListener extends EventListener
{
	public void sessionCreated(SessionLifecycleEvent event);
	
	public void sessionDestroyed(SessionLifecycleEvent event);
}
