package org.codehaus.plexus.component.manager;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
  * 
  * <p>Created on 22/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class DefaultServiceA implements ServiceA, Contextualizable, Serviceable, Configurable, Initializable, Startable,LogEnabled,Disposable
{

    /**
     * 
     */
    public DefaultServiceA()
    {
        super();
    }
	boolean enableLogging;
	boolean contextualize;
	boolean service;
	boolean configure;
	boolean initialize;
	boolean start;
	boolean stop;
	boolean disposed;
	
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
    /**
     * @see org.codehaus.plexus.component.manager.ServiceA#doSomething()
     */
    public void doSomething()
    {
        //

    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        disposed = true;
    }

}
