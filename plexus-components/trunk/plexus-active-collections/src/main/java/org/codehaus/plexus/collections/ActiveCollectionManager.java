package org.codehaus.plexus.collections;

public interface ActiveCollectionManager
{
    
    String ROLE = ActiveCollectionManager.class.getName();
    
    ActiveMap getActiveMap( String role );
    
    ActiveList getActiveList( String role );
    
    ActiveSet getActiveSet( String role );

    ActiveMap getActiveMap( Class role );
    
    ActiveList getActiveList( Class role );
    
    ActiveSet getActiveSet( Class role );

}
