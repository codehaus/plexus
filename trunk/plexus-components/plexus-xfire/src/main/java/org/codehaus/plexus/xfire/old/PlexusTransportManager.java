package org.codehaus.plexus.xfire.old;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.xfire.transport.DefaultTransportManager;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class PlexusTransportManager
    extends DefaultTransportManager
    implements Initializable
{
    public PlexusTransportManager()
    {
        super();
    }
}
