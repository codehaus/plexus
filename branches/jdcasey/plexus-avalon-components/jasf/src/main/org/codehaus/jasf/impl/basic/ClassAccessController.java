package org.codehaus.jasf.impl.basic;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.attributes.Attributes;
import org.codehaus.jasf.impl.AbstractClassAccessController;
import org.codehaus.jasf.resources.Credential;

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
        System.out.println("isAuthd?");
        if (Attributes.hasAttributeType((Method) resource, Credential.class))
        {
            Collection attributes =
                Attributes.getAttributes((Method) resource);
                        
            for ( Iterator itr = attributes.iterator(); itr.hasNext(); )
            {
                // Since I couldn't get AttributesUtil to filter out only
                // Credentials, I'm doing it myself.
                Object credObj = itr.next();
                if ( credObj instanceof Credential )
                {
                    Credential cred = (Credential) credObj;
                    System.out.println("checking for " + cred.getName());
                
                    if ( !((BasicEntity) entity).hasCredential(cred.getName()) )
                        return false;
                }
            }
            
            return true;
        }
        
        return getDefaultAuthorization();
    }
    
}
