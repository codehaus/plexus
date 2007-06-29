package org.codehaus.cargo.container.plexus;

import org.codehaus.cargo.container.EmbeddedLocalContainer;
import org.codehaus.cargo.container.spi.deployer.AbstractEmbeddedLocalDeployer;

/**
 * 
 * @author eredmond
 */
public class PlexusEmbeddedLocalDeployer
    extends AbstractEmbeddedLocalDeployer
{
    public PlexusEmbeddedLocalDeployer( EmbeddedLocalContainer container )
    {
        super( container );
    }
}
