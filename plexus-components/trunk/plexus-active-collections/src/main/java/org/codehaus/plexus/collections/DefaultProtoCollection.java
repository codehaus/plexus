package org.codehaus.plexus.collections;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

public class DefaultProtoCollection
    implements ProtoCollection, Contextualizable
{
    
    private String role;
    private PlexusContainer container;
    
    public DefaultProtoCollection( PlexusContainer container, String role )
    {
        this.container = container;
        this.role = role;
    }
    
    public DefaultProtoCollection()
    {
        // used for plexus init.
    }

    public ActiveList getActiveList()
    {
        return new DefaultActiveList( container, role );
    }

    public ActiveMap getActiveMap()
    {
        return new DefaultActiveMap( container, role );
    }

    public ActiveSet getActiveSet()
    {
        return new DefaultActiveSet( container, role );
    }

    public String getCollectedRole()
    {
        return role;
    }

    public void contextualize( Context context )
        throws ContextException
    {
        this.container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

}
