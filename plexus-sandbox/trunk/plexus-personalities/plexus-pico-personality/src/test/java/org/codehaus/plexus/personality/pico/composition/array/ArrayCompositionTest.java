package org.codehaus.plexus.personality.pico.composition.array;

import org.codehaus.plexus.PlexusTestCase;


public class ArrayCompositionTest extends PlexusTestCase
{
    public ArrayCompositionTest( )
    {
        super( );
    }


    protected void setUp() throws Exception
    {
        super.setUp();
    }

    public void testPicoPersonality() throws Exception
    {

        DefaultBaa baa = ( DefaultBaa ) lookup( Baa.class.getName() );

        baa.baa();

        assertEquals( 2, baa.getFoos().length );

    }

}
