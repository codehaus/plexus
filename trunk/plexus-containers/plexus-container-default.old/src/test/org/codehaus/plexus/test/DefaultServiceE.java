package org.codehaus.plexus.test;

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Configurable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.ServiceLocator;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Serviceable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

/**
 * A simple native plexus component implementing the manual configuration phase.
 */
public class DefaultServiceE
    extends AbstractLogEnabled
    implements ServiceE, Contextualizable, Initializable, Startable, Configurable, Serviceable
{
    public boolean enableLogging;
    public boolean configured;
    public boolean contextualize;
    public boolean initialize;
    public boolean start;
    public boolean stop;
    public boolean serviced;

    public void enableLogging( Logger logger )
    {
        enableLogging = true;
    }

    public void contextualize( Context context )
        throws ContextException
    {
        contextualize = true;
    }

    public void initialize()
        throws Exception
    {
        initialize = true;
    }

    public void start()
        throws Exception
    {
        start = true;
    }

    public void stop()
        throws Exception
    {
        stop = true;
    }

	public void configure(PlexusConfiguration configuration) throws PlexusConfigurationException
	{
		configured = true;
	}

	public void service(ServiceLocator locator)
	{
		serviced = true;
	}
}
