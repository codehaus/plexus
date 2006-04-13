package org.codehaus.plexus.security.mock;

import java.util.Map;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.codehaus.plexus.util.ThreadSafeMap;

/**
  * Mock ServiceManager useful for testing.
  * 
  * <p>Created on 20/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class MockServiceManager implements ServiceManager
{
	/**
	 * Holds all the components keyed by role
	 */
	private Map components = new ThreadSafeMap();
		
	/**
	 * Maps roles by component class. Used to enable lookup of role when
	 * component is released so connection counts can be kept. 
	 */
	private Map roleByComponentClass = new ThreadSafeMap();
	/**
	 * Tracks number of connections to components. ComponentCounts
	 * keyed by component role.
	 */
	private Map connectionCounts = new ThreadSafeMap();
	
    /**
     * @see org.apache.avalon.framework.service.ServiceManager#hasService(java.lang.String)
     */
    public boolean hasService(String role)
    {
        return components.containsKey(role);
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#lookup(java.lang.String)
     */
    public Object lookup(String role) throws ServiceException
    {
        Object obj = components.get(role);
        if( obj == null )
        {
        	throw new ServiceException(role,"No component for this role found");
        }
		ConnectionCount count = (ConnectionCount)connectionCounts.get( role );
        if( count == null )
        {
        	count = new ConnectionCount();
			connectionCounts.put( role, count );        	
        }
        count.increment();
        roleByComponentClass.put( obj.getClass().getName(), role );
        return obj;
    }

    /**
     * @see org.apache.avalon.framework.service.ServiceManager#release(java.lang.Object)
     */
    public void release(Object obj)
    {
        if( obj == null )
        {
        	return;
        }
        String role = (String) roleByComponentClass.get( obj.getClass().getName() );
		ConnectionCount count = (ConnectionCount)connectionCounts.get( role );
		if( count != null )
		{
			count.decrement();			
		}
    }
    
    /**
     * Return the number of connections to the component of this role
     * 
     * @param role
     * @return
     */
    public int mockGetConnectionCounts(String role)
    {
		ConnectionCount count = (ConnectionCount)connectionCounts.get( role );
		if( count != null )
		{
			return count.getCount();
		}
		else
		{
			return 0;
		}
    }
    
    /**
     * Remove the component with the given role. Also removes the 
     * connection count information for it.
     * 
     * 
     * @param role
     * @return the component or null if no component with the given role exists
     */
    public Object mockRemove(String role)
    {
    	if( role == null )
    	{
    			return null;
    	}
    	Object obj = components.get( role );
    	if( obj != null)
    	{
			components.remove(role);
    		connectionCounts.remove( role );
    		roleByComponentClass.remove(obj.getClass().getName());    		
    	}
    	return obj;
    }
        
    /**
     * Return the component with the given role without affecting the connection
     * count
     * 
     * @param role
     * @return
     */
    public Object mockGetComponent(String role)
    {
    	return components.get( role );	
    }
        
        /**
         * Add a component with the given role.
         * 
         * @param role
         * @param component
         */
    public void mockAddComponents(String role,Object component)
    {
    	components.put( role, component );    	
    }

}

class ConnectionCount
 {
	 private int count = 0;
    	    	
	 /**
	  * 
	  */
	 public ConnectionCount()
	 {
		 super();
	 }

	 public void increment()
	 {
		 count ++;
	 }
    	
	 public void decrement()
	 {
		 count --;
	 }
    	
	 public int getCount()
	 {
		 return count;
	 }
 }
