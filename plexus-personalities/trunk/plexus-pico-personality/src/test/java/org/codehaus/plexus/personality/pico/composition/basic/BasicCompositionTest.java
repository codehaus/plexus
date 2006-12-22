package org.codehaus.plexus.personality.pico.composition.basic;

import org.codehaus.plexus.PlexusTestCase;


public class BasicCompositionTest extends PlexusTestCase
{
    public BasicCompositionTest( )
    {
        super( );
    }


    protected void setUp() throws Exception
    {
        super.setUp();
    }

    public void testPicoPersonality() throws Exception
    {

        Kisser kisser = ( Kisser ) lookup( Kisser.class.getName() );

        kisser.kissSomeone();

    }

}
