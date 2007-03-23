package org.codehaus.plexus.collections;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Type of {@link Map}, which is backed by a {@link PlexusContainer} instance, and retrieves a
 * fresh batch of elements for each method call. Entries consist of 
 * component-role-hint -&gt; component-instance.
 * <br/>
 * <b>NOTE: All active collections are immutable, and will throw an 
 * {@link UnsupportedOperationException} if a mutator method is called.</b>
 *  
 * @author jdcasey
 *
 */
public interface ActiveMap
    extends ActiveCollection, Map
{

    String ROLE = ActiveMap.class.getName();

    /**
     * Same as {@link Map#containsKey(Object)}, except this method will throw a 
     * {@link ComponentLookupException} if one or more elements in the set fail during lookup.
     * 
     * @throws ComponentLookupException if one or more elements of the set fail during lookup.
     */
    boolean checkedContainsKey( Object key )
        throws ComponentLookupException;

    /**
     * Same as {@link Map#containsValue(Object)}, except this method will throw a 
     * {@link ComponentLookupException} if one or more elements in the set fail during lookup.
     * 
     * @throws ComponentLookupException if one or more elements of the set fail during lookup.
     */
    boolean checkedContainsValue( Object value )
        throws ComponentLookupException;

    /**
     * Same as {@link Map#entrySet()}, except this method will throw a 
     * {@link ComponentLookupException} if one or more elements in the set fail during lookup.
     * 
     * @throws ComponentLookupException if one or more elements of the set fail during lookup.
     */
    Set checkedEntrySet()
        throws ComponentLookupException;

    /**
     * Same as {@link Map#get(Object)}, except this method will throw a 
     * {@link ComponentLookupException} if one or more elements in the set fail during lookup.
     * 
     * @throws ComponentLookupException if one or more elements of the set fail during lookup.
     */
    Object checkedGet( Object key )
        throws ComponentLookupException;

    /**
     * Same as {@link Map#keySet(Object)}, except this method will throw a 
     * {@link ComponentLookupException} if one or more elements in the set fail during lookup.
     * 
     * @throws ComponentLookupException if one or more elements of the set fail during lookup.
     */
    Set checkedKeySet()
        throws ComponentLookupException;

    /**
     * Same as {@link Map#values(Object)}, except this method will throw a 
     * {@link ComponentLookupException} if one or more elements in the set fail during lookup.
     * 
     * @throws ComponentLookupException if one or more elements of the set fail during lookup.
     */
    Collection checkedValues()
        throws ComponentLookupException;

}
