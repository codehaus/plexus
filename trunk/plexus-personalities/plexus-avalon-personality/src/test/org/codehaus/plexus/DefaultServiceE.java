package org.codehaus.plexus;

import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;

/** This component implements all the start phases:
 *
 *  LogEnabled
 *  Contexualize
 *  Serviceable
 *  Configurable
 *  Initializable
 *  Startable
 *
 */
public class DefaultServiceE
    extends AbstractLogEnabled
    implements ServiceE, Contextualizable, Serviceable, Configurable, Initializable, Startable
{
    boolean enableLogging;
    boolean contextualize;
    boolean service;
    boolean configure;
    boolean initialize;
    boolean start;
    boolean stop;

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void enableLogging( Logger logger )
    {
        enableLogging = true;
    }

    public void contextualize( Context context )
    {
        contextualize = true;
    }

    public void service( ServiceManager serviceManager )
    {
        service = true;
    }

    public void configure( Configuration configuration )
    {
        // Check that we can actually do something with the configuration
        configuration.getChild( "bogus" );

        configure = true;
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
}
