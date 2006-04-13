package org.codehaus.plexus.personality.pico.lifecycle;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.personality.pico.composition.basic.Kisser;
import org.codehaus.plexus.personality.pico.composition.array.DefaultBaa;
import org.codehaus.plexus.personality.pico.composition.array.Baa;


public class LifecycleTest extends PlexusTestCase
{
    public LifecycleTest( String testname )
    {
        super( testname );
    }


    protected void setUp() throws Exception
    {
        super.setUp();
    }

    public void testPicoLifecycle() throws Exception
    {

        PicoComponentWithLifecyle component = ( PicoComponentWithLifecyle ) lookup( PicoComponentWithLifecyle.class.getName() );


        assertTrue( component.isStarted() );

        assertFalse( component.isStopped() );

        assertFalse( component.isDisposed() );

        getContainer().release( component );
        //release( component );

        assertTrue( component.isStopped() );

        assertTrue( component.isDisposed() );

    }

}
