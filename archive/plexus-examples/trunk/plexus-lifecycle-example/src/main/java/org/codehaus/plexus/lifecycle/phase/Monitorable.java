package org.codehaus.plexus.lifecycle.phase;

import org.codehaus.plexus.PlexusContainer;

/**
 * @author Jason van Zyl
 */
public interface Monitorable
{
    void monitor( PlexusContainer container );
}
