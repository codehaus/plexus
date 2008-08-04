package org.codehaus.plexus.collections;

import org.codehaus.plexus.PlexusTestCase;

public abstract class ActiveCollectionManagerTCK
    extends PlexusTestCase
{
    
    private ActiveCollectionManager collectionManager;
    
    public void setUp() throws Exception
    {
        super.setUp();
        
        this.collectionManager = (ActiveCollectionManager) lookup( ActiveCollectionManager.ROLE, getRoleHint() );
    }

    protected abstract String getRoleHint();
    
    public void testGetValidActiveMap_ByClass()
    {
        ActiveMap map = collectionManager.getActiveMap( TestComponent.class );
        
        assertEquals( 3, map.size() );
    }

    public void testGetValidActiveList_ByClass()
    {
        ActiveList list = collectionManager.getActiveList( TestComponent.class );
        
        assertEquals( 3, list.size() );
    }

    public void testGetValidActiveSet_ByClass()
    {
        ActiveSet set = collectionManager.getActiveSet( TestComponent.class );
        
        assertEquals( 2, set.size() );
    }

    public void testGetValidActiveMap_ByString()
    {
        ActiveMap map = collectionManager.getActiveMap( TestComponent.class.getName() );
        
        assertEquals( 3, map.size() );
    }

    public void testGetValidActiveList_ByString()
    {
        ActiveList list = collectionManager.getActiveList( TestComponent.class.getName() );
        
        assertEquals( 3, list.size() );
    }

    public void testGetValidActiveSet_ByString()
    {
        ActiveSet set = collectionManager.getActiveSet( TestComponent.class.getName() );
        
        assertEquals( 2, set.size() );
    }

}
