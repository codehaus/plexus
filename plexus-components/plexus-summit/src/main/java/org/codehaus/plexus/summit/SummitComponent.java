package org.codehaus.plexus.summit;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

public interface SummitComponent
{
    PlexusContainer getContainer();

    Object lookup( String role )
        throws ComponentLookupException;

    Object lookup( String role, String id )
        throws ComponentLookupException;
}
