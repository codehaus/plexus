package org.codehaus.plexus.pipeline;

import java.util.Map;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ValveRequest
{
    private PipelineRequest pipelineRequest;

    public ValveRequest( PipelineRequest pipelineRequest, String valveId )
    {
        this.pipelineRequest = pipelineRequest;

        this.valveId = valveId;
    }

    /**
     * The id of the currently executing valve.
     */
    private String valveId;

    public String getValveId()
    {
        return valveId;
    }

    public void setValveId( String valveId )
    {
        this.valveId = valveId;
    }

    public PipelineRequest getPipelineRequest()
    {
        return pipelineRequest;
    }

    public Map getContext()
    {
        return pipelineRequest.getContext();
    }
}
