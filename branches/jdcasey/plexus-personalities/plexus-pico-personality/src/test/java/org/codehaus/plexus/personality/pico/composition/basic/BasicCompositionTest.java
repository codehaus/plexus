package org.codehaus.plexus.personality.pico.composition.basic;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.personality.pico.composition.basic.Kisser;


public class BasicCompositionTest extends PlexusTestCase
{
    public BasicCompositionTest( String testname )
    {
        super( testname );
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
