package org.codehaus.plexus.collections;

public interface ActiveCollectionManager
{
    
    String ROLE = ActiveCollectionManager.class.getName();

    ActiveMap getActiveMap( String role );
    
    ActiveMap getActiveMap( Class role );

    ActiveMap getCheckedActiveMap( String role );

    ActiveMap getCheckedActiveMap( Class role );

    ActiveSet getActiveSet( String role );

    ActiveSet getActiveSet( Class role );

    ActiveSet getCheckedActiveSet( String role );

    ActiveSet getCheckedActiveSet( Class role );

    ActiveList getActiveList( String role );

    ActiveList getActiveList( Class role );

    ActiveList getCheckedActiveList( String role );

    ActiveList getCheckedActiveList( Class role );

}
