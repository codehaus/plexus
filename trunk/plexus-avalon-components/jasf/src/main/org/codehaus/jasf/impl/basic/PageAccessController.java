package org.codehaus.jasf.impl.basic;

import org.codehaus.jasf.impl.AbstractPageAccessController;

/**
 * PageAccessController.java
 * 
 * @author <a href="dan@envoisolutions.com">Dan Diephouse</a>
 * @since Jan 22, 2003
 */
public class PageAccessController extends AbstractPageAccessController
{
    public static String ROLE = PageAccessController.class.getName();
    
     
    /**
     * Check to see if the entity (ie, the <code>XmlUser</code>) has the
     * credential in any of their <code>Role</code>s.
     *
     * @see org.apache.fulcrum.jasf.impl.AbstractXmlPageAccessController#hasCredential(Object, String)
     */
    public boolean hasCredential( Object entity, String credential )
    {
        getLogger().debug("Checking to see if the entity has the " +
            credential + " credential.");
        return ((BasicEntity) entity).hasCredential(credential);
    }
}
