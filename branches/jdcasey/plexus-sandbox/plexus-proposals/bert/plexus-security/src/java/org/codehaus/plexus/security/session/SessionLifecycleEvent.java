package org.codehaus.plexus.security.session;

import java.util.EventObject;

import org.codehaus.plexus.security.PlexusSession;

/**
  * 
  * <p>Created on 16/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class SessionLifecycleEvent extends EventObject
{	
    /**
     * @param source
     */
    public SessionLifecycleEvent(PlexusSession sess)
    {
        super(sess);
    }

	public PlexusSession getSession()
	{
		return (PlexusSession)super.getSource();
	}
}
