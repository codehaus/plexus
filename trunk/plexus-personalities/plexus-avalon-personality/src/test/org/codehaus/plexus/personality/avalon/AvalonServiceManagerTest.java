package org.codehaus.plexus.personality.avalon;

import org.codehaus.plexus.personality.avalon.AvalonServiceManager;
import junit.framework.TestCase;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since May 10, 2003
 */
public class AvalonServiceManagerTest
    extends TestCase
{

    /**
     * @param testName
     */
    public AvalonServiceManagerTest( String testName )
    {
        super( testName );
    }

    public void testAvalonServiceManagerWithNullComponentRepository()
        throws Exception
    {
        try
        {
            AvalonServiceManager asm = new AvalonServiceManager( null );
            fail( "null cannot be passed as the plexus container." );
        }
        catch ( IllegalStateException e )
        {
        }
    }
}
