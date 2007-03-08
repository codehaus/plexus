package org.codehaus.plexus.collections;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

public class DefaultActiveCollectionManager
    implements ActiveCollectionManager, Contextualizable
{
    
    private PlexusContainer container;
    
    // in case this manager cannot be looked up as a component for some reason.
    public DefaultActiveCollectionManager( PlexusContainer container )
    {
        this.container = container;
    }
    
    public DefaultActiveCollectionManager()
    {
    }

    public ActiveMap getActiveMap( String role )
    {
        return new DefaultActiveMap( container, role );
    }

    public ActiveMap getActiveMap( Class role )
    {
        return new DefaultActiveMap( container, role );
    }

    public ActiveMap getCheckedActiveMap( String role )
    {
        return new DefaultActiveMap( container, role );
    }

    public ActiveMap getCheckedActiveMap( Class role )
    {
        return new DefaultActiveMap( container, role );
    }

    public ActiveSet getActiveSet( String role )
    {
        return new DefaultActiveSet( container, role );
    }

    public ActiveSet getActiveSet( Class role )
    {
        return new DefaultActiveSet( container, role );
    }

    public ActiveSet getCheckedActiveSet( String role )
    {
        return new DefaultActiveSet( container, role );
    }

    public ActiveSet getCheckedActiveSet( Class role )
    {
        return new DefaultActiveSet( container, role );
    }

    public ActiveList getActiveList( String role )
    {
        return new DefaultActiveList( container, role );
    }

    public ActiveList getActiveList( Class role )
    {
        return new DefaultActiveList( container, role );
    }

    public ActiveList getCheckedActiveList( String role )
    {
        return new DefaultActiveList( container, role );
    }

    public ActiveList getCheckedActiveList( Class role )
    {
        return new DefaultActiveList( container, role );
    }

    public void contextualize( Context context )
        throws ContextException
    {
        this.container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

}
