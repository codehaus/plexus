package $package;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version \$Id$
 */
public class DefaultTestApp
    implements TestApp, Initializable
{
    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        System.out.println( "Hello world!" );
    }
}
