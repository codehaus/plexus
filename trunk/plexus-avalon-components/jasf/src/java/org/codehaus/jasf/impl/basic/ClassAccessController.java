package org.codehaus.jasf.impl.basic;

import java.lang.reflect.Method;

import org.apache.commons.attributes.Attributes;
import org.codehaus.jasf.impl.AbstractClassAccessController;

/**
 * ClassAccessController
 * 
 * @author <a href="dan@envoisolutions.com">Dan Diephouse</a>
 * @since Jan 24, 2003
 */
public class ClassAccessController
    extends AbstractClassAccessController 
{   
    /**
     * @see org.apache.fulcrum.jasf.ResourceAccessController#isAuthorized(java.lang.Object, java.lang.Object)
     */
    public boolean isAuthorized(Object entity, Object resource)
    {
        if (Attributes.hasAttribute((Method) resource, "credential"))
        {
            String credential = Attributes.getString((Method) resource, "credential");
            return ((BasicEntity) entity).hasCredential(credential);
        }
        
        return getDefaultAuthorization();
    }
    
}
