package org.codehaus.plexus.component.manager;

import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.codehaus.plexus.security.PlexusSession;
import org.codehaus.plexus.security.SessionManager;
import org.codehaus.plexus.security.session.SessionLifecycleEvent;
import org.codehaus.plexus.security.session.SessionLifecycleListener;
import org.codehaus.plexus.util.ThreadSafeMap;

/**
  * 
  * <p>Created on 22/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class SessionComponentManager
    extends AbstractComponentManager
    implements SessionLifecycleListener, Serviceable, Disposable, Initializable
{
    /** Component instances keyed by session id*/
    private Map sessionToComponent = new ThreadSafeMap();

    private ServiceManager service;

    private SessionManager security;
    /**
     * 
     */
    public SessionComponentManager()
    {
        super();
    }

    /**
     * @see org.codehaus.plexus.security.session.SessionLifecycleListener#sessionCreated(org.codehaus.plexus.security.session.SessionLifecycleEvent)
     */
    public void sessionCreated(SessionLifecycleEvent event)
    {
        //do nothing
    }

    /**
     * @see org.codehaus.plexus.security.session.SessionLifecycleListener#sessionDestroyed(org.codehaus.plexus.security.session.SessionLifecycleEvent)
     */
    public void sessionDestroyed(SessionLifecycleEvent event)
    {
        //end component lifecycle
        Object component = sessionToComponent.remove(event.getSession().getId());
        if (component != null)
        {
            endComponentLifecycle(component);
        }
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
    	//unregister from security service as we don't want to
    	//be notified of session death as we are already 
    	//disposing of all components
    	security.unRegisterSessionLifecycleListener(this);
        //end component lifecycles       
        Object[] components = sessionToComponent.values().toArray();
        for (int i = 0; i < components.length; i++)
        {
            endComponentLifecycle(components[i]);
        }
        sessionToComponent.clear();
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
        PlexusSession sess = getSession();
        if (sess == null)
        {
			throw new IllegalStateException("Cannot obtain a session based component outside of a session scope. There is no session for the current thread");
        }
        Object component = sessionToComponent.get(sess.getId());
        if (component == null)
        {
            component = createComponentInstance();
            sessionToComponent.put(sess.getId(), component);
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
        //register ourself so we can end a components lifecycles on session
		//death (for the component bound to this particular session)
        security.registerSessionLifecycleListener(this);
    }

    /**
     * @see org.codehaus.plexus.component.manager.ComponentManager#release(java.lang.Object)
     */
    public void release(Object component)
    {
        //nothing todo. Only kill components when a session ends
    }

}
