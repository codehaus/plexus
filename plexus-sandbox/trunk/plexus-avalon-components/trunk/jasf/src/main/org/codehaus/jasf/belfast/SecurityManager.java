package org.codehaus.jasf.belfast;

import java.util.List;

import org.codehaus.jasf.belfast.exception.BackendException;
import org.codehaus.jasf.belfast.exception.EntityExistsException;

/**
 * A standard interface to manage various security aspects of a system.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Mar 1, 2003
 */
public interface SecurityManager
{
    public static final String ROLE = SecurityManager.class.getName();
    
    public static final String SELECTOR = SecurityManager.class.getName() + "Selector";
    
    void removeAllPermissions( Role role )
        throws BackendException;
    
    void grant( Role role, Permission perm )
        throws BackendException;
    
    Permission getPermission(String id) 
        throws BackendException, EntityExistsException;
    
    boolean hasPermission( Object entity, String permission )
        throws BackendException;
    
    boolean hasRole( Object entity, String role)
        throws BackendException;
    
    Role getRole(String id)
        throws BackendException, EntityExistsException;
        
    List getRoles()
        throws BackendException;
    
    List getPermissions()
        throws BackendException;
}
