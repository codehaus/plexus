package org.codehaus.plexus.jcs;

import org.codehaus.plexus.PlexusTestCase;
import org.apache.jcs.access.CacheAccess;

public class DefaultJCSComponentTest extends PlexusTestCase
{
    private static final String REGION = "testCache1";
    private static final int ITEMS = 50;

    public void testBasic() throws Exception
    {
        JCSComponent jcs = ( JCSComponent ) lookup ( JCSComponent.ROLE );

        CacheAccess access = jcs.getAccess( REGION );

        // Add items to cache

        for ( int i = 0; i <= ITEMS; i++ )
        {
            access.put( i + ":key", REGION + " data " + i );
        }

        // Test that all items are in cache

        for ( int i = 0; i <= ITEMS; i++ )
        {
            String value = ( String ) access.get( i + ":key" );

            assertEquals( REGION + " data " + i, value );
        }

        // Remove all the items

        for ( int i = 0; i <= ITEMS; i++ )
        {
            access.remove( i + ":key" );
        }

        // Verify removal

        for ( int i = 0; i <= ITEMS; i++ )
        {
            assertNull( "Removed key should be null: " + i + ":key",
                        access.get( i + ":key" ) );
        }
    }
}
