package org.codehaus.plexus.collections;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public interface ActiveList
    extends ActiveCollection, List
{

    String ROLE = ActiveList.class.getName();

    boolean checkedContains( Object value )
        throws ComponentLookupException;

    boolean checkedContainsAll( Collection collection )
        throws ComponentLookupException;

    Object checkedGet( int index )
        throws ComponentLookupException;

    int checkedIndexOf( Object value )
        throws ComponentLookupException;

    Iterator checkedIterator()
        throws ComponentLookupException;

    int checkedLastIndexOf( Object value )
        throws ComponentLookupException;

    ListIterator checkedListIterator()
        throws ComponentLookupException;

    ListIterator checkedListIterator( int index )
        throws ComponentLookupException;

    List checkedSubList( int fromIndex, int toIndex )
        throws ComponentLookupException;

    Object[] checkedToArray()
        throws ComponentLookupException;

    Object[] checkedToArray( Object[] array )
        throws ComponentLookupException;

    boolean contains( Object value );

    boolean containsAll( Collection collection );

    Object get( int index );

    int indexOf( Object value );

    Iterator iterator();

    int lastIndexOf( Object value );

    ListIterator listIterator();

    ListIterator listIterator( int index );

    List subList( int fromIndex, int toIndex );

    Object[] toArray();

    Object[] toArray( Object[] array );

}
