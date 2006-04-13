package org.codehaus.plexus.security.request;

import java.util.EventObject;

import org.codehaus.plexus.security.PlexusSession;

/**
  * 
  * <p>Created on 16/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class RequestEvent extends EventObject
{	
    /**
     * @param source
     */
    public RequestEvent(PlexusSession sess)
    {
        super(sess);
    }

	/**
	 * Return the session associated with this request
	 * 
	 * @return
	 */
	public PlexusSession getSession()
	{
		return (PlexusSession)super.getSource();
	}
}
