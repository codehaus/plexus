package org.codehaus.bacon.session;

import java.util.Date;

public final class SessionKey
{
    
    private final String sessionId;
    
    private final Date created = new Date();
    
    private Date lastAccessed = new Date();
    
    private boolean open = true;

    SessionKey( String sessionId )
    {
        this.sessionId = sessionId;
    }

    public String getSessionId()
    {
        return sessionId;
    }
    
    public Date getLastAccessed()
    {
        return lastAccessed;
    }
    
    public void touch()
    {
        lastAccessed = new Date();
    }
    
    public Date getCreationDate()
    {
        return created;
    }
    
    public boolean isOpen()
    {
        return open;
    }
    
    public void close()
    {
        open = false;
    }
    
}
