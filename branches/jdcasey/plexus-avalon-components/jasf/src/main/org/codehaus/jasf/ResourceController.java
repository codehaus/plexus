package org.codehaus.jasf;

/**
 * Determines if entities are authorized to access specific resources.
 * 
 * @author Dan Diephouse
 * @since Nov 21, 2002
 */
public interface ResourceController
{
    public final static String ROLE = ResourceController.class.getName();

    public final static String SELECTOR_ROLE = ROLE + "Selector";
   
    /**
     * Check to see if the entity is authorized to access the
     * resource.  The relation between the entity and resource is entirely up to
     * the implementation.
     * 
     * @param entity
     * @param resource
     * @return boolean
     */
    public boolean isAuthorized( Object entity, Object resource );
    
}
