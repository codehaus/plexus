package org.codehaus.plexus.collections;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Collection with {@link List} semantics, which is backed by a {@link PlexusContainer} instance, 
 * and retrieves a fresh batch of elements for each method call.
 * <br/>
 * <b>NOTE: All active collections are immutable, and will throw an 
 * {@link UnsupportedOperationException} if a mutator method is called.</b>
 *  
 * @author jdcasey
 *
 */
public interface ActiveList
    extends ActiveCollection, List
{

    String ROLE = ActiveList.class.getName();

    /**
     * Same as {@link List#contains(Object)}, except this method will throw a 
     * {@link ComponentLookupException} if one or more elements in the set fail during lookup.
     * 
     * @throws ComponentLookupException if one or more elements of the set fail during lookup.
     */
    boolean checkedContains( Object value )
        throws ComponentLookupException;

    /**
     * Same as {@link List#containsAll(Collection)}, except this method will throw a 
     * {@link ComponentLookupException} if one or more elements in the set fail during lookup.
     * 
     * @throws ComponentLookupException if one or more elements of the set fail during lookup.
     */
    boolean checkedContainsAll( Collection collection )
        throws ComponentLookupException;

    /**
     * Same as {@link List#get(int)}, except this method will throw a 
     * {@link ComponentLookupException} if one or more elements in the set fail during lookup.
     * 
     * @throws ComponentLookupException if one or more elements of the set fail during lookup.
     */
    Object checkedGet( int index )
        throws ComponentLookupException;

    /**
     * Same as {@link List#indexOf(Object)}, except this method will throw a 
     * {@link ComponentLookupException} if one or more elements in the set fail during lookup.
     * 
     * @throws ComponentLookupException if one or more elements of the set fail during lookup.
     */
    int checkedIndexOf( Object value )
        throws ComponentLookupException;

    /**
     * Same as {@link List#iterator()}, except this method will throw a 
     * {@link ComponentLookupException} if one or more elements in the set fail during lookup.
     * 
     * @throws ComponentLookupException if one or more elements of the set fail during lookup.
     */
    Iterator checkedIterator()
        throws ComponentLookupException;

    /**
     * Same as {@link List#lastIndexOf(Object)}, except this method will throw a 
     * {@link ComponentLookupException} if one or more elements in the set fail during lookup.
     * 
     * @throws ComponentLookupException if one or more elements of the set fail during lookup.
     */
    int checkedLastIndexOf( Object value )
        throws ComponentLookupException;

    /**
     * Same as {@link List#listIterator()}, except this method will throw a 
     * {@link ComponentLookupException} if one or more elements in the set fail during lookup.
     * 
     * @throws ComponentLookupException if one or more elements of the set fail during lookup.
     */
    ListIterator checkedListIterator()
        throws ComponentLookupException;

    /**
     * Same as {@link List#listIterator(int)}, except this method will throw a 
     * {@link ComponentLookupException} if one or more elements in the set fail during lookup.
     * 
     * @throws ComponentLookupException if one or more elements of the set fail during lookup.
     */
    ListIterator checkedListIterator( int index )
        throws ComponentLookupException;

    /**
     * Same as {@link List#subList(int, int)}, except this method will throw a 
     * {@link ComponentLookupException} if one or more elements in the set fail during lookup.
     * 
     * @throws ComponentLookupException if one or more elements of the set fail during lookup.
     */
    List checkedSubList( int fromIndex, int toIndex )
        throws ComponentLookupException;

    /**
     * Same as {@link List#toArray()}, except this method will throw a 
     * {@link ComponentLookupException} if one or more elements in the set fail during lookup.
     * 
     * @throws ComponentLookupException if one or more elements of the set fail during lookup.
     */
    Object[] checkedToArray()
        throws ComponentLookupException;

    /**
     * Same as {@link List#toArray(Object[])}, except this method will throw a 
     * {@link ComponentLookupException} if one or more elements in the set fail during lookup.
     * 
     * @throws ComponentLookupException if one or more elements of the set fail during lookup.
     */
    Object[] checkedToArray( Object[] array )
        throws ComponentLookupException;

}
