package org.codehaus.plexus.security.mock;

import java.util.Map;
import java.util.Set;

import org.codehaus.plexus.security.Agent;
import org.codehaus.plexus.security.PlexusSession;
import org.codehaus.plexus.util.ThreadSafeMap;

/**
  * Mock PlexusSession. Usefult for testing where classes under test require
  * a session
  * 
  * <p>Created on 20/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class MockPlexusSession implements PlexusSession
{
    private Map values = new ThreadSafeMap();
    private String id;
    private Agent agent;
    private long lastAccessTime = System.currentTimeMillis();
    private long creationTime = System.currentTimeMillis();
    private int timeout;
    private boolean isValid;

    /**
     * 
     */
    public MockPlexusSession()
    {
        super();
    }

    /**
     * 
     */
    public MockPlexusSession(String id,Agent agent)
    {
        super();
        this.id = id;
        this.agent = agent;
    }

    /**
     * @see org.codehaus.plexus.security.PlexusSession#contains(java.lang.String)
     */
    public boolean contains(String key)
    {
        return values.containsKey(key);
    }

    /**
     * @see org.codehaus.plexus.security.PlexusSession#get(java.lang.String)
     */
    public Object get(String key)
    {
        return values.get(key);
    }

    /**
     * @see org.codehaus.plexus.security.PlexusSession#getAgent()
     */
    public Agent getAgent()
    {
        return agent;
    }

    /**
     * @see org.codehaus.plexus.security.PlexusSession#getCreationTime()
     */
    public long getCreationTime()
    {
        return creationTime;
    }

    /**
     * @see org.codehaus.plexus.security.PlexusSession#getDelegate()
     */
    public PlexusSession getDelegate()
    {
        return null;
    }

    /**
     * @see org.codehaus.plexus.security.PlexusSession#getId()
     */
    public String getId()
    {
        return id;
    }

    /**
     * @see org.codehaus.plexus.security.PlexusSession#getLastAccessTime()
     */
    public long getLastAccessTime()
    {
        return lastAccessTime;
    }

    /**
     * @see org.codehaus.plexus.security.PlexusSession#getTimeout()
     */
    public long getTimeout()
    {
        return timeout;
    }

    /**
     * @see org.codehaus.plexus.security.PlexusSession#invalidate()
     */
    public void invalidate()
    {
        isValid = false;
    }

    /**
     * @see org.codehaus.plexus.security.PlexusSession#isValid()
     */
    public boolean isValid()
    {
        return isValid;
    }

    /**
     * @see org.codehaus.plexus.security.PlexusSession#keys()
     */
    public Set keys()
    {
        return values.keySet();
    }

    /**
     * @see org.codehaus.plexus.security.PlexusSession#put(java.lang.String, java.lang.Object)
     */
    public void put(String key, Object value)
    {
        values.put(key, value);
    }

    /**
     * @see org.codehaus.plexus.security.PlexusSession#remove(java.lang.String)
     */
    public Object remove(String key)
    {
        return values.remove(key);
    }

    /**
     * @see org.codehaus.plexus.security.PlexusSession#setLastAccessTimeNow()
     */
    public void setLastAccessTimeNow()
    {
        lastAccessTime = System.currentTimeMillis();
    }

    public void mockSetAgent(Agent agent)
    {
        this.agent = agent;
    }

    public void mockSetLastAccessTime(long time)
    {
        this.lastAccessTime = time;
    }

    public void mockSetValid(boolean isValid)
    {
        this.isValid = isValid;
    }
    
    public void mockSetTimeout(int timeout)
    {
    	this.timeout = timeout;
    }
}
