package org.codehaus.bacon.session;

import java.util.Map;
import java.util.WeakHashMap;

public final class ContainerSessionManager
{
    
    private static final ContainerSessionManager INSTANCE = new ContainerSessionManager(); 
    
    private Map sessions = new WeakHashMap();
    
    private ContainerSessionManager()
    {
    }
    
    public static final ContainerSessionManager instance()
    {
        return INSTANCE;
    }
    
    public ContainerSession get( SessionKey sessionKey, String containerId )
    {
        return (ContainerSession) sessions.get( createLookupKey( sessionKey, containerId ) );
    }
    
    public void register( ContainerSession containerSession )
        throws DuplicateContainerSessionException
    {
        String key = createLookupKey( containerSession.getSessionKey(), containerSession.getContainerId() );
        
        if ( sessions.containsKey( key ) )
        {
            throw new DuplicateContainerSessionException( containerSession.getSessionKey(), containerSession.getContainerId() );
        }
        else
        {
            sessions.put( key, containerSession );
        }
    }
    
    public ContainerSession deregister( ContainerSession containerSession )
    {
        String key = createLookupKey( containerSession.getSessionKey(), containerSession.getContainerId() );
        
        if ( sessions.containsKey( key ) )
        {
            return (ContainerSession) sessions.remove( key );
        }
        
        return null;
    }
    
    private String createLookupKey( SessionKey sessionKey, String containerId )
    {
        return containerId + ":" + sessionKey.getSessionId();
    }
}
