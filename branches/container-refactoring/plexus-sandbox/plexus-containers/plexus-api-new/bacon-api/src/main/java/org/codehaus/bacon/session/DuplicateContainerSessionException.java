package org.codehaus.bacon.session;

import org.codehaus.bacon.ContainerException;

public class DuplicateContainerSessionException
    extends ContainerException
{

    private static final long serialVersionUID = 1L;
    
    private final SessionKey sessionKey;
    private final String containerId;

    public DuplicateContainerSessionException( SessionKey sessionKey, String containerId )
    {
        super( "Duplicate container session for key: " + sessionKey.getSessionId() + "; container: " + containerId );
        
        this.sessionKey = sessionKey;
        this.containerId = containerId;
    }

    public String getContainerId()
    {
        return containerId;
    }

    public SessionKey getSessionKey()
    {
        return sessionKey;
    }

}
