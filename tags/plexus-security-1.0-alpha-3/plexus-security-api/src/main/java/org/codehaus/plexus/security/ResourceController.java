package org.codehaus.plexus.security;

/**
 * Determines if entities are authorized to access specific resources.
 *
 * @author Dan Diephouse
 * @since Nov 21, 2002
 */
public interface ResourceController
{
    String ROLE = ResourceController.class.getName();

    String SELECTOR_ROLE = ROLE + "Selector";

    /**
     * Check to see if the entity is authorized to access the
     * resource.  The relation between the entity and resource is entirely up to
     * the implementation.
     *
     * @param entity
     * @param resource
     * @return boolean
     */
    boolean isAuthorized( Object entity, Object resource );
}
