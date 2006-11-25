package org.codehaus.plexus.summit.pipeline;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Configurable;
import org.codehaus.plexus.summit.AbstractSummitComponent;
import org.codehaus.plexus.summit.pipeline.valve.Valve;
import org.codehaus.plexus.summit.pipeline.valve.ValveInvocationException;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 * Flexible implementation of a {@link Pipeline}.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:pete-apache-dev@kazmier.com">Pete Kazmier</a>
 */
public abstract class AbstractPipeline
    extends AbstractSummitComponent
    implements Pipeline, Configurable
{
    protected String name;

    protected List valves = new ArrayList();

    protected boolean nocache;

    public String getName()
    {
        return name;
    }

    public void invoke( RunData data )
        throws IOException, ValveInvocationException
    {
        if ( nocache )
        {
            data.getResponse().setHeader( "Pragma", "No-cache" );

            data.getResponse().setHeader( "Cache-Control", "no-cache" );

            data.getResponse().setDateHeader( "Exprires", 1 );
        }

        for ( Iterator iterator = valves.iterator(); iterator.hasNext(); )
        {
            Valve valve = (Valve) iterator.next();

            valve.invoke( data );
        }
    }

    public List getValves()
    {
        return valves;
    }

    // ----------------------------------------------------------------------
    // TODO: this should be changed to use requirements but we need to be able
    // to select a list of requirements. For example there may be lots of
    // valves available in the system but the user may only want a subset
    // of those valves run. Using the old plexus configurable method we
    // can do that. But we need to be able to do this with a requirement.
    // ----------------------------------------------------------------------

    public void configure( PlexusConfiguration config )
        throws PlexusConfigurationException
    {
        String nocacheValue = config.getChild( "nocache" ).getValue();

        if ( nocacheValue != null )
        {
            nocache = new Boolean( nocacheValue ).booleanValue();
        }

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
            throw new PlexusConfigurationException( "Couldn't create valve with role-hint = " + name, e );
        }

        valves.add( valve );
    }
}
