package org.codehaus.plexus.spe.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * @version $Id$
 */
public class ProcessInstance
    implements Serializable
{
    private String id;

    private String processId;

    private long createdTime;

    private long endTime;

    private String errorMessage;

    private List<StepInstance> steps = new ArrayList<StepInstance>();

    private Map<String, Serializable> context = new HashMap<String, Serializable>();

    public ProcessInstance()
    {
    }

    public ProcessInstance( ProcessInstance other )
    {
        this.id = other.getId();
        this.processId = other.getProcessId();
        this.createdTime = other.getCreatedTime();
        this.endTime = other.getEndTime();
        this.errorMessage = other.getErrorMessage();
        this.steps = other.getSteps();

        if ( other.isContextSet() )
        {
            this.context = other.getContext();
        }
    }

    public ProcessInstance( String id, String processId )
    {
        this.id = id;
        this.processId = processId;
    }

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getProcessId()
    {
        return processId;
    }

    public void setProcessId( String processId )
    {
        this.processId = processId;
    }

    public long getCreatedTime()
    {
        return createdTime;
    }

    public void setCreatedTime( long createdTime )
    {
        this.createdTime = createdTime;
    }

    public long getEndTime()
    {
        return endTime;
    }

    public void setEndTime( long endTime )
    {
        this.endTime = endTime;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage( String errorMessage )
    {
        this.errorMessage = errorMessage;
    }

    public boolean isCompleted()
    {
        return endTime > 0;
    }

    public List<StepInstance> getSteps()
    {
        return Collections.unmodifiableList( steps );
    }

    public void addStep( StepInstance step )
    {
        steps.add( step );
    }

    public void setSteps( List<StepInstance> steps )
    {
        if ( steps == null )
        {
            throw new IllegalArgumentException( "Argument cannot be null." );
        }

        this.steps = steps;
    }

    public boolean isContextSet()
    {
        return context != null;
    }

    public Map<String, Serializable> getContext()
    {
        return Collections.unmodifiableMap( context );
    }

    public void setContext( Map<String, Serializable> context )
    {
        if ( context == null )
        {
            throw new IllegalArgumentException( "Argument cannot be null." );
        }

        this.context = new HashMap<String, Serializable>( context );
    }

    public void putContext( String key, Serializable serializable )
    {
        context.put( key, serializable );
    }
}
