package org.codehaus.plexus.collections;

import org.codehaus.plexus.PlexusTestCase;

public abstract class ProtoCollectionTCK
    extends PlexusTestCase
{
    
    private ProtoCollection protoCollection;

    public void setUp() throws Exception
    {
        super.setUp();
        
        this.protoCollection = getProtoCollection();
    }

    protected abstract ProtoCollection getProtoCollection();
    
    public void testGetValidActiveMap_ByClass()
    {
        ActiveMap map = protoCollection.getActiveMap();
        
        assertEquals( 3, map.size() );
    }

    public void testGetValidActiveList_ByClass()
    {
        ActiveList list = protoCollection.getActiveList();
        
        assertEquals( 3, list.size() );
    }

    public void testGetValidActiveSet_ByClass()
    {
        ActiveSet set = protoCollection.getActiveSet();
        
        assertEquals( 2, set.size() );
    }

    public void testGetValidActiveMap_ByString()
    {
        ActiveMap map = protoCollection.getActiveMap();
        
        assertEquals( 3, map.size() );
    }

    public void testGetValidActiveList_ByString()
    {
        ActiveList list = protoCollection.getActiveList();
        
        assertEquals( 3, list.size() );
    }

    public void testGetValidActiveSet_ByString()
    {
        ActiveSet set = protoCollection.getActiveSet();
        
        assertEquals( 2, set.size() );
    }

}
