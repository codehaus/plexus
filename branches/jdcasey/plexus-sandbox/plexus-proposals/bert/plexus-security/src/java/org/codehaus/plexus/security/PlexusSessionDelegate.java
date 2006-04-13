package org.codehaus.plexus.security;

import java.util.Set;


/**
  * Wraps a PlexusSession implementation to only expose those methods defined in 
  * the PlexusSession interface 
  * 
  * <p>Created on 16/08/2003</p>
  *
  * @author <a href="mailto:bertvanbrakel@sourceforge.net">Bert van Brakel</a>
  * @revision $Revision$
  */
public final class PlexusSessionDelegate implements PlexusSession
{
	private final PlexusSession realSession;

    /**
     * 
     */
    public PlexusSessionDelegate(PlexusSession session)
    {
        super();
        this.realSession = session;
    }

    /**
     * @param key
     * @return
     */
    public boolean contains(String key)
    {
        return realSession.contains(key);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        return realSession.equals(obj);
    }

    /**
     * @param key
     * @return
     */
    public Object get(String key)
    {
        return realSession.get(key);
    }

    /**
     * @return
     */
    public Agent getAgent()
    {
        return realSession.getAgent();
    }

    /**
     * @return
     */
    public long getCreationTime()
    {
        return realSession.getCreationTime();
    }

    /**
     * @return
     */
    public String getId()
    {
        return realSession.getId();
    }

    /**
     * @return
     */
    public long getLastAccessTime()
    {
        return realSession.getLastAccessTime();
    }

    /**
     * @return
     */
    public long getTimeout()
    {
        return realSession.getTimeout();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return realSession.hashCode();
    }

    /**
     * 
     */
    public void invalidate()
    {
        realSession.invalidate();
    }

    /**
     * @return
     */
    public boolean isValid()
    {
        return realSession.isValid();
    }

    /**
     * @return
     */
    public Set keys()
    {
        return realSession.keys();
    }

    /**
     * @param key
     * @param value
     */
    public void put(String key, Object value)
    {
        realSession.put(key, value);
    }

    /**
     * @param key
     * @return
     */
    public Object remove(String key)
    {
        return realSession.remove(key);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return realSession.toString();
    }

    /**
     * This is the delegate, so just returns null
     * 
     * @return null
     */
    public PlexusSession getDelegate()
    {
        return null;
    }

    /**
     * Does nothing
     */
    public void setLastAccessTimeNow()
    {
        //do nothing;
    }

}
