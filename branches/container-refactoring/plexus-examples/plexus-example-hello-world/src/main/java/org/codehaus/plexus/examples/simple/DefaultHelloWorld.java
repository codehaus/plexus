package org.codehaus.plexus.examples.simple;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;

/** 
 * Concrete implementation of a <tt>HelloWorld</tt> component.  The
 * component is configured via the Plexus container.
 * 
 * @author Pete Kazmier
 * @version $Revision$
 */
public class DefaultHelloWorld
    implements HelloWorld, LogEnabled
{
    /** The logger supplied by the Plexus container. */
    private Logger logger;

    /** The greeting that was specified in the configuration. */
    private String greeting;

    public void enableLogging( Logger logger )
    {
        this.logger = logger;
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
