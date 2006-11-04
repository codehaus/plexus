package $package;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

/**
 * Concrete implementation of a <tt>HelloWorld</tt> component.  The
 * component is configured via the Plexus container.
 *
 * @author
 * @version $$Id$$
 */
public class DefaultHelloWorld
    extends AbstractLogEnabled
    implements HelloWorld, Startable
{
    /** The greeting that was specified in the configuration. */
    private String greeting;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void start()
    {
        getLogger().info( "Starting Hello component." );
    }

    public void stop()
    {
        getLogger().info( "Stopping Hello component." );
    }

    // ----------------------------------------------------------------------
    // HelloWorld Implementation
    // ----------------------------------------------------------------------

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

