package org.codehaus.plexus.security.authorization.memory;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.security.authorization.rbac.memory.MemoryRbacModel;
import org.codehaus.plexus.security.authorization.rbac.memory.MemoryResource;
import org.codehaus.plexus.security.authorization.rbac.memory.MemoryRole;
import org.codehaus.plexus.security.authorization.rbac.memory.MemoryRoles;
import org.codehaus.plexus.security.authorization.rbac.memory.io.xpp3.RBACMemoryModelXpp3Reader;
import org.codehaus.plexus.security.authorization.rbac.memory.io.xpp3.RBACMemoryModelXpp3Writer;
import org.codehaus.plexus.security.rbac.Operation;
import org.codehaus.plexus.security.rbac.Permission;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.security.rbac.RbacStoreException;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.rbac.Role;
import org.codehaus.plexus.security.rbac.Roles;
import org.codehaus.plexus.security.rbac.UserAssignment;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * MemoryRbacManager: a very quick and dirty implementation of a rbac store
 *
 * WARNING: not for actual usage, its not sound - jesse
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.security.rbac.RBACManager"
 *   role-hint="memory"
 */
public class MemoryRbacManager
    implements RBACManager, Initializable
{
    private MemoryRbacModel model = null;

    private File rbacStoreFile = null;

    public File getRbacStoreFile()
    {
        if ( rbacStoreFile == null )
        {
            rbacStoreFile = new File( getBasedir() + File.separator + "rbac-store-model.xml" );
        }
        return rbacStoreFile;
    }

    public void initialize()
        throws InitializationException
    {
        RBACMemoryModelXpp3Reader reader = new RBACMemoryModelXpp3Reader();

        if ( getRbacStoreFile().exists() )
        {
            try
            {
                model = reader.read( new FileReader( getRbacStoreFile() ) );
            }
            catch ( Exception e )
            {
                throw new InitializationException( "error loading file rbac-store-model.xml", e );
            }
        }
        else
        {
            model = new MemoryRbacModel();
        }

        // Satisfy base level settings.
        if ( model.getRoles() == null )
        {
            model.setRoles( new MemoryRoles() );
        }
    }

    public void store()
        throws Exception
    {
        RBACMemoryModelXpp3Writer writer = new RBACMemoryModelXpp3Writer();

        writer.write( new FileWriter( getRbacStoreFile() ), model );
    }

    public static String getBasedir()
    {
        String basedir = System.getProperty( "basedir" );

        if ( basedir == null )
        {
            basedir = new File( "" ).getAbsolutePath();
        }

        return basedir;
    }

    // ----------------------------------------------------------------------
    // Role methods
    // ----------------------------------------------------------------------
    public Role addRole( Role role )
        throws RbacStoreException
    {
        model.getRoles().addRole( role );
        return role;
    }

    public Role getRole( int roleId )
        throws RbacObjectNotFoundException
    {
        return model.getRoles().getRole( roleId );
    }

    public List getAllRoles()
        throws RbacStoreException
    {
        return model.getRoles().flattenRoleHierarchy();
    }

    public List getAssignableRoles()
        throws RbacStoreException
    {
        List assignableRoles = new ArrayList();

        for ( Iterator i = model.getRoles().iterator(); i.hasNext(); )
        {
            Role role = (Role) i.next();

            if ( role.isAssignable() )
            {
                assignableRoles.add( role );
            }
        }

        return assignableRoles;
    }

    public void removeRole( int roleId )
        throws RbacStoreException, RbacObjectNotFoundException
    {
        // just removing top lvl roles atm.
        if ( getRole( roleId ) != null )
        {
            model.getRoles().removeRole( getRole( roleId ) );
        }
    }

    // ----------------------------------------------------------------------
    // Permission methods
    // ----------------------------------------------------------------------
    public void addPermission( int roleId, Permission permission )
        throws RbacStoreException, RbacObjectNotFoundException
    {
        Role role = getRole( roleId );

        role.addPermission( permission );
    }

    public List getPermissions( int roleId )
        throws RbacStoreException, RbacObjectNotFoundException
    {
        return getRole( roleId ).getPermissions();
    }

    public Operation addOperation( Operation operation )
        throws RbacStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Permission addPermission( Permission permission )
        throws RbacStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Resource addResource( Resource resource )
        throws RbacStoreException
    {
        model.addResource( resource.getIdentifier(), (MemoryResource) resource );
        return resource;
    }

    public UserAssignment addUserAssignment( UserAssignment userAssignment )
        throws RbacStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Operation createOperation( String name, String description )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Permission createPermission( String name, String description )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Permission createPermission( String name, String description, String operation, String resource )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Resource createResource( String identifier )
    {
        Resource resource = new MemoryResource();
        resource.setIdentifier( identifier );

        return resource;
    }

    public Role createRole( String name, String description )
    {
        Role role = new MemoryRole();
        role.setName( name );
        role.setDescription( description );

        return role;
    }

    public UserAssignment createUserAssignment( Object principal )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Operation getOperation( int operationId )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List getOperations()
        throws RbacStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Permission getPermission( int permissionId )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List getPermissions()
        throws RbacStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Resource getResource( int resourceId )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List getResources()
        throws RbacStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List getRoles()
        throws RbacStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public UserAssignment getUserAssignment( Object principal )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List getUserAssignments()
        throws RbacStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void removeOperation( Operation operation )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        // TODO Auto-generated method stub

    }

    public void removePermission( Permission permission )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        // TODO Auto-generated method stub

    }

    public void removeResource( Resource resource )
        throws RbacStoreException
    {
        // TODO Auto-generated method stub

    }

    public void removeRole( Role role )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        // TODO Auto-generated method stub

    }

    public void removeUserAssignment( UserAssignment userAssignment )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        // TODO Auto-generated method stub

    }

    public void setOperations( List operation )
        throws RbacStoreException
    {
        // TODO Auto-generated method stub

    }

    public void setPermissions( List permissions )
        throws RbacStoreException
    {
        // TODO Auto-generated method stub

    }

    public void setResources( List resources )
        throws RbacStoreException
    {
        // TODO Auto-generated method stub

    }

    public void setRoles( List roles )
        throws RbacStoreException
    {
        // TODO Auto-generated method stub

    }

    public void setUserAssignments( List assignments )
        throws RbacStoreException
    {
        // TODO Auto-generated method stub

    }

    public Operation updateOperation( Operation operation )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Permission updatePermission( Permission permission )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Resource updateResource( Resource resource )
        throws RbacStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Role updateRole( Role role )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public UserAssignment updateUserAssignment( UserAssignment userAssignment )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set getAssignedPermissions( Object principal )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public UserAssignment createUserAssignment( String principal )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List getAllOperations()
        throws RbacStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List getAllPermissions()
        throws RbacStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List getAllResources()
        throws RbacStoreException
    {
        return Collections.unmodifiableList( new ArrayList( model.getResources().values() ) );
    }

    public List getAllUserAssignments()
        throws RbacStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public UserAssignment getUserAssignment( String principal )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Roles getAssignedRoles( Object principal )
        throws RbacObjectNotFoundException, RbacStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List getAllAssignableRoles()
        throws RbacStoreException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
