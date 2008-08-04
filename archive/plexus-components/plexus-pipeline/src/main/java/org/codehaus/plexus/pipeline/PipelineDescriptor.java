package org.codehaus.plexus.pipeline;

import java.util.List;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PipelineDescriptor
{
    private String id;

    private List valveRoleHints;

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
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
