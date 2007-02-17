package org.codehaus.plexus.spe.model;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * @version $Id$
 */
public class StepInstance
    implements Serializable
{
    private String id;

    private String processInstanceId;

    private String executorId;

    private long startTime;

    private long endTime;

    private String exceptionStackTrace;

    private List<LogMessage> logMessages = new ArrayList<LogMessage>();

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getProcessInstanceId()
    {
        return processInstanceId;
    }

    public void setProcessInstanceId( String processInstanceId )
    {
        this.processInstanceId = processInstanceId;
    }

    public String getExecutorId()
    {
        return executorId;
    }

    public void setExecutorId( String executorId )
    {
        this.executorId = executorId;
    }

    public long getStartTime()
    {
        return startTime;
    }

    public void setStartTime( long startTime )
    {
        this.startTime = startTime;
    }

    public long getEndTime()
    {
        return endTime;
    }

    public void setEndTime( long endTime )
    {
        this.endTime = endTime;
    }

    public String getExceptionStackTrace()
    {
        return exceptionStackTrace;
    }

    public void setExceptionStackTrace( String exceptionStackTrace )
    {
        this.exceptionStackTrace = exceptionStackTrace;
    }

    public List<LogMessage> getLogMessages()
    {
        return Collections.unmodifiableList( logMessages );
    }

    public void setLogMessages( List<LogMessage> logMessages )
    {
        if ( logMessages == null )
        {
            throw new IllegalArgumentException( "Argument cannot be null." );
        }

        this.logMessages = logMessages;
    }

    public void addLogMessage( LogMessage logMessage )
    {
        logMessages.add( logMessage );
    }
}
