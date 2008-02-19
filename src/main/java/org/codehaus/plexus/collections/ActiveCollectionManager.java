package org.codehaus.plexus.collections;

/**
 * Manager interface provided for backward compatibility to apps using plexus-container-default with
 * versions under 1.0-alpha-22. This is necessary because the active collections in this project
 * implement Map, List, and Set...three interfaces which trigger the container in older versions to
 * attempt to find multiple components of the specified role (in this case, it'd be ActiveMap|Set|List),
 * and assign all of them as a single requirement on the dependent component. To work around these
 * cases, code in older applications can use a requirement on this manager, then ask the manager for
 * the active collection of choice. It's a little less dynamic, but should still work well.
 * 
 * @author jdcasey
 *
 */
public interface ActiveCollectionManager
{
    
    String ROLE = ActiveCollectionManager.class.getName();
    
    /**
     * Retrieve an {@link ActiveMap} instance that contains the components of the given role.
     */
    ActiveMap getActiveMap( String role );
    
    /**
     * Retrieve an {@link ActiveList} instance that contains the components of the given role.
     */
    ActiveList getActiveList( String role );
    
    /**
     * Retrieve an {@link ActiveSet} instance that contains the components of the given role.
     */
    ActiveSet getActiveSet( String role );

    /**
     * Retrieve an {@link ActiveMap} instance that contains the components of the given role. NOTE:
     * the role which is used is actually the class-name, not the class...so, here ROLE = role.getName().
     * 
     * @param role the class whose name we will use as the role for the components to retrieve.
     */
    ActiveMap getActiveMap( Class role );
    
    /**
     * Retrieve an {@link ActiveList} instance that contains the components of the given role. NOTE:
     * the role which is used is actually the class-name, not the class...so, here ROLE = role.getName().
     * 
     * @param role the class whose name we will use as the role for the components to retrieve.
     */
    ActiveList getActiveList( Class role );
    
    /**
     * Retrieve an {@link ActiveSet} instance that contains the components of the given role. NOTE:
     * the role which is used is actually the class-name, not the class...so, here ROLE = role.getName().
     * 
     * @param role the class whose name we will use as the role for the components to retrieve.
     */
    ActiveSet getActiveSet( Class role );

}
