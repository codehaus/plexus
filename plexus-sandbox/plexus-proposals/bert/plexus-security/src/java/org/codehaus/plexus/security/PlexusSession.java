package org.codehaus.plexus.security;

import java.util.Set;


/**
  * The session object used internally by plexus. Clients should not be able 
  * to directly access this object
  * 
  * <p>Created on 15/08/2003</p>
  *
  * @author <a href="mailto:bertvanbrakel@sourceforge.net">Bert van Brakel</a>
  * @revision $Revision$
  */
public interface  PlexusSession
{	
	public void put( String key, Object value );
	
	public Object get(String key);
	
	public boolean contains(String key);
	
	public Object remove(String key);
	
	public Set keys();
	
	public Agent getAgent();
		
	public void invalidate();
	
	public boolean isValid();
	
	public long getTimeout();
	
	public long getCreationTime();
	
	public long getLastAccessTime();
		
	public String getId();
	
	void setLastAccessTimeNow();
	
	PlexusSession getDelegate();
}
