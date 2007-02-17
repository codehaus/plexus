package org.codehaus.plexus.spe.model;

import org.w3c.dom.Document;

/**
 * @version $Id$
 */
public class StepDescriptor
    implements java.io.Serializable
{
    private String executorId;

    private Document executorConfiguration;

    private Document configuration;

    public StepDescriptor( String executorId )
    {
        this.executorId = executorId;
    }

    public StepDescriptor( String executorId, Document executorConfiguration, Document configuration )
    {
        this.executorId = executorId;
        this.executorConfiguration = executorConfiguration;
        this.configuration = configuration;
    }

    public String getExecutorId()
    {
        return executorId;
    }

    public Document getExecutorConfiguration()
    {
        return executorConfiguration;
    }

    public Document getConfiguration()
    {
        return configuration;
    }

    public void setConfiguration( Document configuration )
    {
        if ( configuration == null )
        {
            throw new IllegalArgumentException( "Argument cannot be null" );
        }

        this.configuration = configuration;
    }

    public void setExecutorId( String executorId )
    {
        this.executorId = executorId;
    }

    public void setExecutorConfiguration( Document executorConfiguration )
    {
        this.executorConfiguration = executorConfiguration;
    }
}
