package org.codehaus.plexus.collections;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.Collection;
import java.util.Iterator;

public interface ActiveSet
    extends ActiveCollection
{

    public boolean contains( Object value );

    public boolean containsAll( Collection collection );

    public Iterator iterator();

    public Object[] toArray();

    public Object[] toArray( Object[] array );

    public boolean checkedContains( Object value )
        throws ComponentLookupException;

    public boolean checkedContainsAll( Collection collection )
        throws ComponentLookupException;

    public Iterator checkedIterator()
        throws ComponentLookupException;

    public Object[] checkedToArray()
        throws ComponentLookupException;

    public Object[] checkedToArray( Object[] array )
        throws ComponentLookupException;

}
