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
	/**
	 * Bind an object to this session with the given key. Replaces any object
	 * already bound with this key 
	 * 
	 * @param key
	 * @param value
	 * @throws IllegalStateException if this session is invalid
	 */
	public void put( String key, Object value ) throws IllegalStateException;
	
	/**
	 * Return the object bound to this session with the given key.
	 * 
	 * @param key
	 * @return the object or null if there is no object with the specified key
	 * @throws IllegalStateException if this session is invalid
	 */
	public Object get(String key) throws IllegalStateException;
	
	/**
	 * Test if there is an object bound to this session with the given key
	 * 
	 * @param key
	 * @return
	 * @throws IllegalStateException if this session is invalid
	 */
	public boolean contains(String key) throws IllegalStateException;
	
	/**
	 * Remove an object bound to this session with the gven key
	 * 
	 * @param key
	 * @return
	 * @throws IllegalStateException if this session is invalid
	 */
	public Object remove(String key) throws IllegalStateException;
	
	/**
	 * Return the keys of all the object bound to this session
	 * 
	 * @return
	 * @throws IllegalStateException if this session is invalid
	 */
	public Set keys() throws IllegalStateException;
	
	/**
	 * Return the agent who this session belongs to
	 * @return
	 */
	public Agent getAgent();
	
	/**
	 * Invalidate this session.
	 * 
	 *@throws IllegalStateException if the session is already invalid
	 */
	public void invalidate() throws IllegalStateException;
	
	/**
	 * Test if this session is valid
	 * 
	 * @return true if the session is valid, false otherwise
	 */
	public boolean isValid();
	
	public long getTimeout();
	
	/**
	 * Return the time when this session was created. 
	 * 
	 * @return long representing the time this session was created, 
	 * expressed in milliseconds since 1/1/1970 GMT
	 */
	public long getCreationTime();
	
	/**
	 * Return the time of the last request associated with this session.
	 * 
	 * @return long  representing the last time the client sent a request associated with 
	 * this session, expressed in milliseconds since 1/1/1970 GMT
	 */
	public long getLastAccessTime();
	
	/**
	 * Return the unique id of this session. This id uniquely identifies this session
	 * from all other sessions within the container providing this session. The id
	 * is implementation dependent
	 * 
	 * @return
	 */
	public String getId();
	
	void setLastAccessTimeNow() throws IllegalStateException;
	
	PlexusSession getDelegate();
}
