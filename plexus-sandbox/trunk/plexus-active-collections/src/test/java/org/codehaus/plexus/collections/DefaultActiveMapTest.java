package org.codehaus.plexus.collections;

import org.codehaus.plexus.PlexusTestCase;

public class DefaultActiveMapTest
    extends PlexusTestCase
{
    
    private ActiveMap map;
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        map = (ActiveMap) lookup( ActiveMap.ROLE, "test-map" );
    }
    
    public void testGetMappedComponents()
    {
        TestComponent comp1 = (TestComponent) map.get( "one" );
        assertEquals( "first", comp1.getValue() );
        
        TestComponent comp2 = (TestComponent) map.get( "two" );
        assertEquals( "second", comp2.getValue() );
    }

}
