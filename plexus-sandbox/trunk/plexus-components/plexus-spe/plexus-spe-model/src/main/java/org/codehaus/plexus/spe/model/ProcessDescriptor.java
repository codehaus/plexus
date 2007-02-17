package org.codehaus.plexus.spe.model;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

/**
 * @version $Id$
 */
public class ProcessDescriptor
    implements java.io.Serializable
{
    private String id;

    private String defaultExecutorId;

    private List<StepDescriptor> steps = new ArrayList<StepDescriptor>();

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getDefaultExecutorId()
    {
        return defaultExecutorId;
    }

    public void setDefaultExecutorId( String defaultExecutorId )
    {
        this.defaultExecutorId = defaultExecutorId;
    }

    public List<StepDescriptor> getSteps()
    {
        return Collections.unmodifiableList( steps );
    }

    public void setSteps( List<StepDescriptor> steps )
    {
        if ( steps == null )
        {
            throw new IllegalArgumentException( "Argument cannot be null." );
        }

        this.steps = steps;
    }

    public void addStep( StepDescriptor stepDescriptor )
    {
        steps.add( stepDescriptor );
    }
}
