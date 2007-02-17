package org.codehaus.plexus.spe.model;

/**
 * @version $Id$
 */
public class LogMessage
    implements java.io.Serializable
{
    private long timestamp;

    private String message;

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( long timestamp )
    {
        this.timestamp = timestamp;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }
}
