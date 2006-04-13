package org.codehaus.jasf.impl.global;

import org.codehaus.jasf.ResourceController;

/**
 * NoAccessResourceController is an implementation of
 * <code>ResourceController</code> that does not give access to any
 * resource for any Entity.
 * 
 * @author <a href="dan@envoisolutions.com">Dan Diephouse</a>
 * @since Jan 10, 2003
 */
public class NoAccessResourceController implements ResourceController
{

    /**
     * A pseudo implementation that returns false for every instance, denying
     * any <code>Entity</code> access to any resource.
     * 
     * @see org.apache.fulcrum.jasf.ResourceAccessController#isAuthorized(Object, Object)
     */
    public boolean isAuthorized( Object entity, Object resource )
    {
        return false;
    }

}
