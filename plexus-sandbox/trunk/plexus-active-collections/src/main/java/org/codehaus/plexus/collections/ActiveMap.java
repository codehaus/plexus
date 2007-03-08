package org.codehaus.plexus.collections;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.Collection;
import java.util.Set;

public interface ActiveMap
    extends ActiveCollection
{

    boolean containsKey( Object key );

    boolean containsValue( Object value );

    Set entrySet();

    Object get( Object key );

    Set keySet();

    Collection values();

    boolean checkedContainsKey( Object key )
        throws ComponentLookupException;

    boolean checkedContainsValue( Object value )
        throws ComponentLookupException;

    Set checkedEntrySet()
        throws ComponentLookupException;

    Object checkedGet( Object key )
        throws ComponentLookupException;

    Set checkedKeySet()
        throws ComponentLookupException;

    Collection checkedValues()
        throws ComponentLookupException;

}
