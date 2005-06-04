package org.codehaus.plexus.summit.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Configurable;
import org.codehaus.plexus.summit.AbstractSummitComponent;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.pipeline.valve.Valve;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 * Flexible implementation of a {@link org.codehaus.plexus.summit.pipeline.Pipeline}.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:pete-apache-dev@kazmier.com">Pete Kazmier</a>
 */
public class SummitPipeline
    extends AbstractSummitComponent
    implements Pipeline, Configurable
{
    protected String name;

    protected List valves = new ArrayList();

    public String getName()
    {
        return name;
    }

    public void invoke( RunData data )
        throws SummitException, IOException
    {
        for ( Iterator iterator = valves.iterator(); iterator.hasNext(); )
        {
            Valve phase = (Valve) iterator.next();

            try
            {
                phase.invoke( data );
            }
            catch ( Exception e )
            {
                throw new SummitException( "Cannot invoke valve:", e );
            }
        }
    }

    public void configure( PlexusConfiguration config )
        throws PlexusConfigurationException
    {
        PlexusConfiguration[] valves = config.getChild( "valves" ).getChildren( "valve" );

        for ( int i = 0; i < valves.length; i++ )
        {
            configureValve( valves[i] );
        }
    }

    protected void configureValve( PlexusConfiguration config )
        throws PlexusConfigurationException
    {
        String name = config.getValue();

        Valve valve = null;

        try
        {
            valve = (Valve) getContainer().lookup( Valve.ROLE, name );
        }
        catch ( ComponentLookupException e )
        {
            try
            {
                valve = (Valve) getClass().getClassLoader().loadClass( name ).newInstance();
            }
            catch ( Exception e1 )
            {
                getLogger().error( "Couldn't create valve!", e1 );
                throw new PlexusConfigurationException( "Couldn't create valve!", e1 );
            }
        }

        valves.add( valve );
    }
}
