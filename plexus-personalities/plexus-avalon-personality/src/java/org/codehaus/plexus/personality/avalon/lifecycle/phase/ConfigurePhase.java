package org.codehaus.plexus.personality.avalon.lifecycle.phase;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.configuration.DefaultConfiguration;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.personality.avalon.AvalonConfiguration;

public class ConfigurePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws Exception
    {
        Configuration configuration = new AvalonConfiguration( manager.getComponentDescriptor().getConfiguration() );

        if ( object instanceof Configurable )
        {
            if ( null == configuration )
            {
                configuration = new AvalonConfiguration( DefaultConfiguration.EMPTY_CONFIGURATION );
            }
            ( (Configurable) object ).configure( configuration );
        }
    }
}
