package org.codehaus.plexus.personality.avalon.lifecycle.phase;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.personality.avalon.AvalonConfiguration;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;

public class ReconfigurePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager manager )
        throws PhaseExecutionException
    {
        Configuration configuration = new AvalonConfiguration( manager.getComponentDescriptor().getConfiguration() );

        if ( object instanceof Reconfigurable )
        {
            if ( null == configuration )
            {
                final String message = "configuration is null";
                throw new IllegalArgumentException( message );
            }
            try
            {
                ( (Reconfigurable) object ).reconfigure( configuration );
            }
            catch ( ConfigurationException e )
            {
                throw new PhaseExecutionException( "reconfigure threw ConfigurationException", e );                
            }
        }
    }
}
