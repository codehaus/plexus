package org.codehaus.plexus.security;

import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.plexus.PlexusException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.security.authentication.AuthenticationException;
import org.codehaus.plexus.security.authentication.AuthenticationService;
import org.codehaus.plexus.security.authentication.RuntimeAuthenticationException;
import org.codehaus.plexus.security.credentials.acl.ACL;
import org.codehaus.plexus.security.credentials.acl.DefaultACL;
import org.codehaus.plexus.security.request.RequestEvent;
import org.codehaus.plexus.security.request.RequestListener;
import org.codehaus.plexus.security.request.RequestListenerNotifier;
import org.codehaus.plexus.security.session.SessionIdGenerator;
import org.codehaus.plexus.security.session.SessionLifecycleListener;
import org.codehaus.plexus.security.session.SessionListenerNotifier;
import org.codehaus.plexus.util.ThreadSafeMap;

/**
  *
 * <p>Requires:
 * <ul>
 * 	<li>AuthorisationService</li>
 * 	<li>AuthenticationService</li>
 * </ul>
 * </p>
  * <p>Created on 16/08/2003</p>
  *
  * @author <a href="mailto:bertvanbrakel@sourceforge.net">Bert van Brakel</a>
  * @revision $Revision$
  */
public abstract class AbstractSecurityService extends AbstractLogEnabled implements Serviceable,SecurityService,Disposable
{
    private ServiceManager service;

    /** System sessions by thread */
    private Map sessionsByThread = new ThreadSafeMap();

    /** System sessions keyed by agent id */
    private Map sessionsByAgentId = new ThreadSafeMap();

    /** System sessions keyed by session id */
    private Map sessionsBySessiontId = new ThreadSafeMap();

    /** Holds all the agents in the system by id */
    private Map agentsById = new ThreadSafeMap();

    /** Used to generate uique session ids */
    private SessionIdGenerator sessIdGenerator = new SessionIdGenerator();

    /** ACL used if none bound to thread. By default everything is allowed */
    private ACL defaultACL = new DefaultACL(true);

    /** RequestListeners registered to be notified of request demarcation */
    private RequestListenerNotifier requestListeners;

    /** SessionLifecycleListeners registered to be notified of session lifecycle
     * events*/
    private SessionListenerNotifier sessionListeners;

    /** */
    private AuthenticationService authentication;


	private int defaultSessionTimeout = DefaultPlexusSession.DEFAULT_TIMEOUT;
    /**
     * 
     */
    public AbstractSecurityService()
    {
        super();
    }

    /**
     * Obtain a session for the agent identified by the given authentication token. If the
     * agent has already been authenticated and has a valid session, then return this session,
     * else create a new one.
     * 
     * The session is not bound to the current thread
     * 
     * @param token
     * @return
     * @throws AuthenticationException if the token was invalid or there was some error while
     * attempting to validate the token.
     * 
     */
    public PlexusSession authenticate(Object token) throws AuthenticationException
    {
        if (token == null)
        {
            throw new AuthenticationException("Null token. Authentication failed");
        }
        try
        {
            //obtain the agent this token identifies
            String agentId = getAuthenticationService().authenticate(token);
            Agent agent = (Agent) agentsById.get(agentId);
            //create an agent with credentials if none exists
            if (agent == null)
            {
                agent = createAgent(agentId);
                agentsById.put(agentId, agent);
            }
            //lookup if the agent has any current sessions, if not
            //create a new session
            PlexusSession sess = lookupSessionByAgentId(agentId);

            if (sess == null)
            {
                sess = createSession(agent);
                sessionsByAgentId.put(agentId, sess);
                sessionsBySessiontId.put(sess.getId(), sess);
            }

            sess.setLastAccessTimeNow();

            //sessionsByThread.put( Thread.currentThread(), sess );
            return sess.getDelegate();
        }
        catch (AuthenticationException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new AuthenticationException("System exception", e);
        }
    }

    private AuthenticationService getAuthenticationService() throws ServiceException
    {
        if (authentication == null)
        {
            authentication = (AuthenticationService) service.lookup(AuthenticationService.ROLE);
        }
        return authentication;
    }

    /**
     * Begin a request within the session identified by the given id. The
     * current thread has the Session, ACL  and agent bound to it.
     *
     * @todo what todo if the thread already has something bound to it?? do
     * we need to notify anything??
     *
     * @param sessionId
     */
    public void beginRequest(String sessionId)
    {
        if (sessionId == null)
        {
            throw new RuntimeAuthenticationException("No sessions with a null id exist ");
        }

        DefaultPlexusSession sess = (DefaultPlexusSession) sessionsBySessiontId.get(sessionId);

        if (sess == null)
        {
            throw new RuntimeAuthenticationException(
                "No Session with the given id '" + sessionId + "' exists");
        }

        Thread ct = Thread.currentThread();
        sessionsByThread.put(ct, sess);
        requestListeners.sendRequestBegun(new RequestEvent(sess));
    }

    public PlexusSession getSession()
    {
        DefaultPlexusSession sess =
            (DefaultPlexusSession) sessionsByThread.get(Thread.currentThread());
        if (sess == null)
        {
            return null; //hmm, add a default one??
        }
        return sess.getDelegate();
    }
    /**
     * End a request associated with the session with the given id
     *
     * @param sessionId
     */
    public void endRequest()
    {
        Thread ct = Thread.currentThread();
        PlexusSession sess =(PlexusSession)sessionsByThread.remove(ct);
        if( sess != null )
        {
        	requestListeners.sendRequestEnded(new RequestEvent(sess));
        }
    }



    protected PlexusSession lookupSessionByAgentId(String agentId)
    {
        return (DefaultPlexusSession) sessionsByAgentId.get(agentId);
    }

    protected Agent lookupAgent(String agentId)
    {
        return (Agent) agentsById.get(agentId);
    }

    //protected abstract ACL buildACL(String agentId);

    protected abstract Agent createAgent(String id) throws PlexusException;

    protected PlexusSession createSession(Agent agent)
    {
        DefaultPlexusSession sess =
            new DefaultPlexusSession(
                sessIdGenerator.generateUniqueId(),
                agent,
                defaultSessionTimeout);

        return sess;
    }
    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager service) throws ServiceException
    {
        this.service = service;
    }

    public void registerRequestListener(RequestListener listener)
    {
        requestListeners.registerListener(listener);
    }

    public void unRegisterRequestListener(RequestListener listener)
    {
        requestListeners.unRegisterListener(listener);
    }

    public void registerSessionLifecycleListener(SessionLifecycleListener listener)
    {
        sessionListeners.registerListener(listener);
    }

    public void unRegisterSessionLifecycleListener(SessionLifecycleListener listener)
    {
        sessionListeners.unRegisterListener(listener);
    }

    /**
     * @return
     */
    protected int getDefaultSessionTimeout()
    {
        return defaultSessionTimeout;
    }

    /**
     * @param i
     */
    protected void setDefaultSessionTimeout(int seconds)
    {
    	if( seconds <0  )
    	{
			defaultSessionTimeout  = 0;
    	}
        else
        {
        	defaultSessionTimeout = seconds;
        }
    }
    
    protected ServiceManager getServiceManager()
    {
    	return service;
    }

    /**
     * @see org.codehaus.plexus.security.SecurityService#getAgent()
     */
    public Agent getAgent()
    {
        PlexusSession sess = getSession();
        if( sess == null)
        {
        	return null;
        }
        else
        {
        	return sess.getAgent();
        }
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
    	if( authentication!= null)
        {
        	service.release(authentication);
        } 
    }

}
