package org.codehaus.plexus.collections;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Collection with {@link Set} semantics, which is backed by a {@link PlexusContainer} instance, 
 * and retrieves a fresh batch of elements for each method call.
 * <br/>
 * <b>NOTE: All active collections are immutable, and will throw an 
 * {@link UnsupportedOperationException} if a mutator method is called.</b>
 *  
 * @author jdcasey
 *
 */
public interface ActiveSet
    extends ActiveCollection, Set
{

    String ROLE = ActiveSet.class.getName();

    /**
     * Same as {@link Set#contains(Object)}, except this method will throw a 
     * {@link ComponentLookupException} if one or more elements in the set fail during lookup.
     * 
     * @throws ComponentLookupException if one or more elements of the set fail during lookup.
     */
    public boolean checkedContains( Object value )
        throws ComponentLookupException;

    /**
     * Same as {@link Set#containsAll(Collection)}, except this method will throw a 
     * {@link ComponentLookupException} if one or more elements in the set fail during lookup.
     * 
     * @throws ComponentLookupException if one or more elements of the set fail during lookup.
     */
    public boolean checkedContainsAll( Collection collection )
        throws ComponentLookupException;

    /**
     * Same as {@link Set#iterator()}, except this method will throw a 
     * {@link ComponentLookupException} if one or more elements in the set fail during lookup.
     * 
     * @throws ComponentLookupException if one or more elements of the set fail during lookup.
     */
    public Iterator checkedIterator()
        throws ComponentLookupException;

    /**
     * Same as {@link Set#toArray()}, except this method will throw a 
     * {@link ComponentLookupException} if one or more elements in the set fail during lookup.
     * 
     * @throws ComponentLookupException if one or more elements of the set fail during lookup.
     */
    public Object[] checkedToArray()
        throws ComponentLookupException;

    /**
     * Same as {@link Set#toArray(Object[])}, except this method will throw a 
     * {@link ComponentLookupException} if one or more elements in the set fail during lookup.
     * 
     * @throws ComponentLookupException if one or more elements of the set fail during lookup.
     */
    public Object[] checkedToArray( Object[] array )
        throws ComponentLookupException;

}
