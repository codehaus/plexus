package org.codehaus.plexus.security.request;

import java.util.EventListener;

/**
  * Objects wishing to be notified of request events should implement this interface
  * and register with an appropriate request producer.
  * 
  * <p>Created on 16/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public interface RequestListener extends EventListener
{
	public void requestBegun(RequestEvent event);
	
	public void requestEnded(RequestEvent event);
}
