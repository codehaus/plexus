package org.codehaus.plexus.collections;

/**
 * Represents an undifferentiated ActiveCollection instance, with the same configuration (role).
 * This is another workaround for applications that use plexus-container-default versions less than
 * 1.0-alpha-22, since this proto-collection can be injected without causing conflict with the existing
 * collection-detection code in the container. Dependent components can then call getActiveMap|List|Set
 * and retrieve the instance, which has been preconfigured.
 * 
 * @author jdcasey
 *
 */
public interface ProtoCollection
{
    
    String ROLE = ProtoCollection.class.getName();
    
    /**
     * Retrieve the role of components to be collected by any of the active collections created here.
     */
    String getCollectedRole();
    
    /**
     * Retrieve an {@link ActiveMap} instance that contains components matching the role given by 
     * {@link ProtoCollection#getCollectedRole()}.
     */
    ActiveMap getActiveMap();
    
    /**
     * Retrieve an {@link ActiveList} instance that contains components matching the role given by 
     * {@link ProtoCollection#getCollectedRole()}.
     */
    ActiveList getActiveList();
    
    /**
     * Retrieve an {@link ActiveSet} instance that contains components matching the role given by 
     * {@link ProtoCollection#getCollectedRole()}.
     */
    ActiveSet getActiveSet();

}
