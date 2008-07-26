package org.codehaus.plexus.collections;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * @plexus.component role="org.codehaus.plexus.collections.ActiveCollectionManager" role-hint="default"
 * 
 * @author jdcasey
 *
 */
public class DefaultActiveCollectionManager
    implements ActiveCollectionManager, Contextualizable
{

    private PlexusContainer container;

    public ActiveList getActiveList( String role )
    {
        return new DefaultActiveList( container, role );
    }

    public ActiveMap getActiveMap( String role )
    {
        return new DefaultActiveMap( container, role );
    }

    public ActiveSet getActiveSet( String role )
    {
        return new DefaultActiveSet( container, role );
    }

    public void contextualize( Context context )
        throws ContextException
    {
        this.container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    public ActiveList getActiveList( Class role )
    {
        return getActiveList( role.getName() );
    }

    public ActiveMap getActiveMap( Class role )
    {
        return getActiveMap( role.getName() );
    }

    public ActiveSet getActiveSet( Class role )
    {
        return getActiveSet( role.getName() );
    }

}
