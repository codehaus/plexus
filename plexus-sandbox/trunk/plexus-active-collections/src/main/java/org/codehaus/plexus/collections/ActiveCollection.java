package org.codehaus.plexus.collections;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a collection-style view of a {@link PlexusContainer} instance, which retrieves a fresh
 * batch of component elements for each method call. This interface DOES NOT implement {@link Collection},
 * since it is used as a common parent for active {@link List}, {@link Set}, and {@link Map} 
 * implementations.
 * <br/>
 * <b>NOTE: All active collections are immutable, and will throw an 
 * {@link UnsupportedOperationException} if a mutator method is called.</b>
 * 
 * @author jdcasey
 *
 */
public interface ActiveCollection
{
    
    /**
     * Retrive the role, or type of component, which is collected in the current instance. The current
     * collection will only "contain" elements that specify this role in their component definitions.
     */
    String getRole();

    /**
     * Same semantics as {@link Collection#isEmpty()} or {@link Map#isEmpty()}.
     */
    boolean isEmpty();

    /**
     * Same semantics as {@link Collection#isEmpty()} or {@link Map#isEmpty()}, except this method
     * will throw a {@link ComponentLookupException} if one or more of the elements collected here
     * fails during lookup.
     */
    boolean checkedIsEmpty()
        throws ComponentLookupException;

    /**
     * Same semantics as {@link Collection#size()} or {@link Map#size()}.
     */
    int size();
    
    /**
     * Same semantics as {@link Collection#size()} or {@link Map#size()}, except this method
     * will throw a {@link ComponentLookupException} if one or more of the elements collected here
     * fails during lookup.
     */
    int checkedSize()
        throws ComponentLookupException;

}
