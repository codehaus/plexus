package org.codehaus.plexus.personality.avalon.lifecycle.phase;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.personality.avalon.AvalonConfiguration;

public class ConfigurePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        if ( object instanceof Configurable )
        {
            PlexusConfiguration plexusConfiguration = manager.getComponentDescriptor().getConfiguration();

            if ( plexusConfiguration == null )
            {
                plexusConfiguration = new XmlPlexusConfiguration( "" );
            }

            Configuration configuration = new AvalonConfiguration( plexusConfiguration );

            ( (Configurable) object ).configure( configuration );
        }
    }
}
