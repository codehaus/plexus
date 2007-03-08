package org.codehaus.plexus.collections;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

public interface ActiveCollection
{
    
    String getRole();

    boolean isEmpty();

    boolean checkedIsEmpty()
        throws ComponentLookupException;

    int size();
    
    int checkedSize()
        throws ComponentLookupException;

}
