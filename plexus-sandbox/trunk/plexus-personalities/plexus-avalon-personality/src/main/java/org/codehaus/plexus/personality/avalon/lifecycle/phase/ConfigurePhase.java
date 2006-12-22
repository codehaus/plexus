package org.codehaus.plexus.personality.avalon.lifecycle.phase;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.personality.avalon.AvalonConfiguration;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;

public class ConfigurePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws PhaseExecutionException
    {
        if ( object instanceof Configurable )
        {
            PlexusConfiguration plexusConfiguration = manager.getComponentDescriptor().getConfiguration();

            if ( plexusConfiguration == null )
            {
                plexusConfiguration = new XmlPlexusConfiguration( "" );
            }

            Configuration configuration = new AvalonConfiguration( plexusConfiguration );

            try
            {
                ( (Configurable) object ).configure( configuration );
            }
            catch ( ConfigurationException e )
            {
                throw new PhaseExecutionException( "configure threw ConfigurationException", e );
            }
        }
    }
}
