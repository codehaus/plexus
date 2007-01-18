package org.codehaus.plexus.pipeline;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PipelineRequest
{
    private String pipelineId;

    private Map context;

    private List listeners;

    private ExceptionHandler exceptionHandler;

    private Boolean traceExecution;

    public PipelineRequest()
    {
    }

    public PipelineRequest( String pipeline, Map context )
    {
        this.pipelineId = pipeline;
        this.context = context;
    }

    public PipelineRequest( String pipeline, Map context, List listeners, ExceptionHandler exceptionHandler )
    {
        this.pipelineId = pipeline;
        this.context = context;
        this.listeners = listeners;
        this.exceptionHandler = exceptionHandler;
    }

    public String getPipelineId()
    {
        return pipelineId;
    }

    public void setPipelineId( String pipelineId )
    {
        this.pipelineId = pipelineId;
    }

    public Map getContext()
    {
        return context;
    }

    public void setContext( Map context )
    {
        this.context = context;
    }

    public List getListeners()
    {
        if ( listeners == null )
        {
            listeners = new ArrayList();
        }

        return listeners;
    }

    public void setListeners( List listeners )
    {
        this.listeners = listeners;
    }

    public ExceptionHandler getExceptionHandler()
    {
        return exceptionHandler;
    }

    public void setExceptionHandler( ExceptionHandler exceptionHandler )
    {
        this.exceptionHandler = exceptionHandler;
    }

    public Boolean getTraceExecution()
    {
        return traceExecution;
    }

    public void setTraceExecution( Boolean traceExecution )
    {
        this.traceExecution = traceExecution;
    }
}
