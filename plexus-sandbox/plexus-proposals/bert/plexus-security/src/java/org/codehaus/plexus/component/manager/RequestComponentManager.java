package org.codehaus.plexus.component.manager;

import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.codehaus.plexus.security.PlexusSession;
import org.codehaus.plexus.security.SessionManager;
import org.codehaus.plexus.security.request.RequestEvent;
import org.codehaus.plexus.security.request.RequestListener;
import org.codehaus.plexus.util.ThreadSafeMap;

/**

  * 
  * <p>Created on 22/08/2003</p>
  *
   * @todo need to determine if we are within request scope on getting a component.
    * 
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class RequestComponentManager
	extends AbstractComponentManager
	implements RequestListener, Serviceable, Disposable, Initializable
{
	/** Component instances keyed by thread. A request is per thread */
	private Map threadToComponent = new ThreadSafeMap();

	private ServiceManager service;

	private SessionManager security;
    /**
     * 
     */
    public RequestComponentManager()
    {
        super();
    }


	protected PlexusSession getSession()
	{
		return security.getSession();
	}

	/**
	 * @see org.codehaus.plexus.component.manager.ComponentManager#dispose()
	 */
	public void dispose()
	{
		security.unRegisterRequestListener(this);
		//end component lifecycles       
		Object[] components = threadToComponent.values().toArray();
		for (int i = 0; i < components.length; i++)
		{
			endComponentLifecycle(components[i]);
		}
		if (security != null)
		{
			service.release(security);
		}
	}

	/**
	 * @see org.codehaus.plexus.component.manager.ComponentManager#getComponent()
	 */
	public Object getComponent() throws Exception
	{
		if( false == security.isThreadWithinRequestScope() )
		{
			throw new IllegalStateException("Cannot lookup a request based component outside of a request scope");
		}
		Thread t = Thread.currentThread();
		Object component = threadToComponent.get(t);
		if (component == null)
		{
			component = createComponentInstance();
			threadToComponent.put(t, component);
		}
		return component;
	}

	/**
	 * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
	 */
	public void service(ServiceManager service) throws ServiceException
	{
		this.service = service;
	}

	/**
	 * @see org.codehaus.plexus.component.manager.ComponentManager#initialize()
	 */
	public void initialize() throws Exception
	{
		super.initialize();
		security = (SessionManager) service.lookup(SessionManager.ROLE);
		//register ourself so we can end a components lifecycle
		//once the request has ended
		security.registerRequestListener(this);
	}

	/**
	 * @see org.codehaus.plexus.component.manager.ComponentManager#release(java.lang.Object)
	 */
	public void release(Object component)
	{
		//nothing todo. Only kill components when a request ends
	}

    /**
     * @see org.codehaus.plexus.security.request.RequestListener#requestBegun(org.codehaus.plexus.security.request.RequestEvent)
     */
    public void requestBegun(RequestEvent event)
    {
       //do nothing. Components are killed when the request ends
    }

    /**
     * @see org.codehaus.plexus.security.request.RequestListener#requestEnded(org.codehaus.plexus.security.request.RequestEvent)
     */
    public void requestEnded(RequestEvent event)
    {
		Object component = threadToComponent.get(Thread.currentThread());
		if (component != null)
		{
			endComponentLifecycle(component);
		}
    }

}
