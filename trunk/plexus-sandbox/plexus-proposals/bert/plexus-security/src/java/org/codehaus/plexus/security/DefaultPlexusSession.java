package org.codehaus.plexus.security;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.codehaus.plexus.security.session.InvalidSessionException;
import org.codehaus.plexus.security.session.SessionLifecycleEvent;
import org.codehaus.plexus.security.session.SessionLifecycleListener;
import org.codehaus.plexus.util.ThreadSafeMap;

/**
  * Used by the system to hold a agent session components and other session related stuff.
  * 
  * May need to look at being able to serialize this so it can betaken out of memory
  * when not being used. Possibly also log all non serializeable values put into the 
  * values map as these won't be saved across server restarts.
  * 
  * <p>Created on 15/08/2003</p>
  *
  * @author <a href="mailto:bertvanbrakel@sourceforge.net">Bert van Brakel</a>
  * @revision $Revision$
  */
public class DefaultPlexusSession implements PlexusSession
{
    /** The agent who owns this session */
    private Agent agent;

	private List lifecycleListeners = new Vector();
    /** Holds the session based components by role */
    //private Map sessionComponents = new ThreadSafeMap();

    /** The unique session id */
    private String id;
    /** Delegate session. Wraps this session to only expose those defined in the ApplicationSession
     * interface */
    private PlexusSession delegate;

    /** Last time this session was acessed */
    private long lastAccessTime;

    /** Holds user defined variables */
    private Map values = new ThreadSafeMap();

    /** The current threads being serviced under this session . Each thread is a unique request */
    private List currentRequests = new Vector();

	/** Length of time between requests before a session is automatically invalidated due 
	 * to timeout. Negative values indicate no timeout*/
	private int timeout;
	
	/** Default session timeout */
	public static final int DEFAULT_TIMEOUT = 30*60;
	
	private long created = System.currentTimeMillis();
	
	private boolean isValid = true;
	
	private SecurityService securityService;
    /**
    	* 
    	*/
    public DefaultPlexusSession(String id, Agent agent, int timeout)
    {
        super();
        if (id == null)
        {
            throw new IllegalArgumentException("The session id cannot be null");
        }
        this.id = id;
        if (agent == null)
        {
            throw new IllegalArgumentException("The agent cannot be null");
        }
        this.timeout = timeout;
        this.agent = agent;
    }
    
    public long getCreationTime()
    {
    	return created;
    }
    
    public long getTimeout()
    {
    	return timeout;
    }
    
    public void registerLifecycleListener(SessionLifecycleListener listener)
    {
		checkValidSession();
    	if( lifecycleListeners.contains(listener) == false )    	
    	{
    		lifecycleListeners.add( listener );
    	} 
    }
    
    public void deregisterLifecycleListener(SessionLifecycleListener listener)
    {
		checkValidSession();
    	lifecycleListeners.remove(listener);
    }
    
    public PlexusSession getDelegate()
    {
		checkValidSession();
    	if( delegate == null )
    	{
    		delegate = new PlexusSessionDelegate( this ); 
    	}
    	return delegate;
    }
    
    public synchronized void inValidate()
    {
    	if( isValid )
    	{
    		isValid = false;
    		//notify listeners..
    		SessionLifecycleEvent event = new SessionLifecycleEvent( this.getDelegate() );
    		Object[] listeners = lifecycleListeners.toArray();    		
    		for (int i = 0; i < listeners.length; i++)
            {
                ((SessionLifecycleListener)listeners[ i ]).sessionDestroyed(event);
            }    		
    	}    	    	
    }
    
    public String getId()
    {
    	return id;
    }
    
    private void checkValidSession() throws InvalidSessionException
    {
    	if( isValid == false )
    	{
    		throw new InvalidSessionException("the session '" + getId() + "' is invalid'");
    	}
    }

	public Agent getAgent()
	{
		return agent;		
	}
    public void setLastAccessTimeNow()
    {
		checkValidSession();
        lastAccessTime = System.currentTimeMillis();
    }

    public long getLastAccessTime()
    {
        return lastAccessTime;
    }

    public void put(String key, Object value)
    {
		checkValidSession();
        values.put(key, value);
    }

    public Object get(String key)
    {
		checkValidSession();
        return values.get(key);
    }

    public Object remove(String key)
    {
        return values.remove(key);
    }

    public boolean contains(String key)
    {
        return values.containsKey(key);
    }

    public Set keys()
    {
		checkValidSession();
        return values.keySet();
    }

    public boolean isServicingRequests()
    {
        return !currentRequests.isEmpty();
    }

    public void beginRequest()
    {
		checkValidSession();
        setLastAccessTimeNow();
        currentRequests.add(Thread.currentThread());
        //bind this sess to the current thread.
    }

    public void endRequest()
    {
        currentRequests.remove(Thread.currentThread());
        //unbind this sess from the current thread
    }

    /**
     * @see org.codehaus.plexus.security.ApplicationSession#invalidate()
     */
    public void invalidate()
    {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.codehaus.plexus.security.ApplicationSession#isValid()
     */
    public boolean isValid()
    {
        // TODO Auto-generated method stub
        return false;
    }

}
