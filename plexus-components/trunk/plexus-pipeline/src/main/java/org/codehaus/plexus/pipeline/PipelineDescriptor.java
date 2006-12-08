package org.codehaus.plexus.pipeline;

import java.util.List;
import java.util.ArrayList;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PipelineDescriptor
{
    private String id;

    private List valveInstances = new ArrayList();

    private List valveRoleHints;

    public PipelineDescriptor( String id )
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public List getValveInstances()
    {
        return valveInstances;
    }

    public void setValveInstances( List valveInstances )
    {
        this.valveInstances = valveInstances;
    }

    public List getValveRoleHints()
    {
        return valveRoleHints;
    }

    public void setValveRoleHints( List valveRoleHints )
    {
        this.valveRoleHints = valveRoleHints;
    }
}
