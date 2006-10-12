package org.codehaus.plexus.security.ui.web.filter.authentication.digest;

import junit.framework.TestCase;

public class HexTest
    extends TestCase
{
    public void testEncoding()
    {
        String raw = "Lenore\nLenore";
        String lenoreHex = "4c656e6f7265";
        String expected = lenoreHex + "0a" + lenoreHex;

        assertEquals( expected, Hex.encode( raw ) );
    }

    public void testTheRaven()
    {
        String raw = "Quoth the Raven, \"Nevermore.\"";
        String expected = "51756f74682074686520526176656e2c20224e657665726d6f72652e22";
        
        assertEquals( expected, Hex.encode( raw.getBytes() ) );
    }
}
