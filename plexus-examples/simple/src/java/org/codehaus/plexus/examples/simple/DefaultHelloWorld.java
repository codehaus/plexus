package org.codehaus.plexus.examples.simple;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;

/** 
 * Concrete implementation of a <tt>HelloWorld</tt> component.  The
 * component is configured via the Plexus container.
 * 
 * @author Pete Kazmier
 * @version $Revision$
 */
public class DefaultHelloWorld 
    implements HelloWorld, LogEnabled, Configurable
{
    /** The logger supplied by the Plexus container. */
    private Logger logger;

    /** The greeting that was specified in the configuration. */
    private String greeting;

    public void enableLogging(Logger logger)
    {
        this.logger = logger;
    }

    public void configure(Configuration conf) throws ConfigurationException
    {
        greeting = conf.getChild("greeting").getValue();
        logger.info("Greeting has been configured to: " + greeting);
    }
    
    /** 
     * Says hello by returning a greeting to the caller.
     * 
     * @return A greeting.
     */
    public String sayHello()
    {
        return greeting;
    }
}
