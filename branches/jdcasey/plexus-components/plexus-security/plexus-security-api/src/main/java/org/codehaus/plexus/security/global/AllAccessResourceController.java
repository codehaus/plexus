package org.codehaus.plexus.security.global;

import org.codehaus.plexus.security.ResourceController;

/**
 * AllAccessResourceController is an implementation of 
 * <code>ResourceController</code> that gives access to any resource
 * for any credential for any Entity. 
 * 
 * @author <a href="dan@envoisolutions.com">Dan Diephouse</a>
 * @since Jan 10, 2003
 */
public class AllAccessResourceController implements ResourceController
{
    public static String ROLE = AllAccessResourceController.class.getName();
    
    /** 
     * A pseudo implementation that returns true for every instance, granting
     * <code>Entity</code>s all access.
     */
    public boolean isAuthorized( Object entity, Object resource )
    {
        return true;
    }
}
