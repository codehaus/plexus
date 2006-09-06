package org.codehaus.plexus.security.authorization.memory;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.security.authorization.rbac.Operation;
import org.codehaus.plexus.security.authorization.rbac.Permission;
import org.codehaus.plexus.security.authorization.rbac.RBACModel;
import org.codehaus.plexus.security.authorization.rbac.Role;
import org.codehaus.plexus.security.authorization.rbac.UserAssignment;
import org.codehaus.plexus.security.authorization.rbac.io.xpp3.RBACModelXpp3Reader;
import org.codehaus.plexus.security.authorization.rbac.io.xpp3.RBACModelXpp3Writer;
import org.codehaus.plexus.security.authorization.rbac.store.RbacStore;
import org.codehaus.plexus.security.authorization.rbac.store.RbacStoreException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/*
 * Copyright 2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * MemoryRbacStore: a very quick and dirty implementation of a rbac store
 *
 * WARNING: not for actual usage, its not sound
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @version $Id:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.security.authorization.rbac.store.RbacStore"
 *   role-hint="memory"
 */
public class MemoryRbacStore
    implements RbacStore, Initializable
{
    RBACModel store = null;

    public void initialize()
        throws InitializationException
    {
        RBACModelXpp3Reader reader = new RBACModelXpp3Reader();

        try
        {
            store = reader.read( new FileReader( getBasedir() + File.separator + "rbac-store-model.xml" ) );
        }
        catch ( Exception e )
        {
            throw new InitializationException( "error loading file rbac-store-model.xml", e );
        }
    }


    public void store()
        throws Exception
    {
        RBACModelXpp3Writer writer = new RBACModelXpp3Writer();

        writer.write( new FileWriter( getBasedir() + File.separator + "rbac-store-model.xml"), store);
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
    public void addRole( Role role )
        throws RbacStoreException
    {
        store.addRole( role );
    }

    public Role getRole( int roleId )
        throws RbacStoreException
    {
        for (Iterator i = store.getRoles().iterator(); i.hasNext(); )
        {
            Role role = (Role)i.next();

            if ( role.getId() == roleId )
            {
                return role;
            }
        }

        throw new RbacStoreException( "unable to locate role: " + roleId );
    }

    public List getAllRoles()
        throws RbacStoreException
    {
        List gatheredRoles = new ArrayList();

        for (Iterator i = store.getRoles().iterator(); i.hasNext(); )
        {
            Role role = (Role) i.next();

            gatherRoles( gatheredRoles, role );
        }

        return gatheredRoles;
    }

    private void gatherRoles( List gatheredRoles, Role role )
    {
        if ( role.getRoles() != null && role.getRoles().size() > 0 )
        {
            for ( Iterator i = role.getRoles().iterator(); i.hasNext(); )
            {
                Role r = (Role)i.next();

                gatheredRoles.add( r );

                gatherRoles( gatheredRoles, r );
            }
        }
        else
        {
            gatheredRoles.add( role );
        }
    }

    public List getAssignableRoles()
        throws RbacStoreException
    {
        List assignableRoles = new ArrayList();

        for ( Iterator i = store.getRoles().iterator(); i.hasNext(); )
        {
            Role role = (Role)i.next();

            if ( role.isAssignable() )
            {
                assignableRoles.add( role );
            }
        }

        return assignableRoles;
    }


    // ----------------------------------------------------------------------
    // Permission methods
    // ----------------------------------------------------------------------
    public void addPermission( int roleId, Permission permission )
        throws RbacStoreException
    {
        Role role = getRole( roleId );

        role.addPermission( permission );
    }

    public List getPermissions( int roleId )
        throws RbacStoreException
    {
        return getRole( roleId ).getPermissions();
    }

    // ----------------------------------------------------------------------
    // Operation methods
    // ----------------------------------------------------------------------
    public void addOperation( int permissionId, Operation operation )
        throws RbacStoreException
    {
    }

    // ----------------------------------------------------------------------
    // Role Assignment methods
    // ----------------------------------------------------------------------

    public List getRoleAssignments( String principal )
        throws RbacStoreException
    {
        return store.getAssignments();
    }

    public void addRoleAssignment( String principal, int roleId )
        throws RbacStoreException
    {
        Role role = null;
        for ( Iterator i = store.getRoles().iterator(); i.hasNext(); )
        {
            Role temp = (Role) i.next();

            if ( temp.getId() == roleId )
            {
                role = temp;
                break;
            }
        }

        if ( role != null )
        {
            UserAssignment assignment = null;
            boolean roleAdded = false;

            for (Iterator i = store.getAssignments().iterator(); i.hasNext(); )
            {
                assignment = (UserAssignment)i.next();

                if ( principal != null && principal.equals( assignment.getPrincipal() ) )
                {
                    assignment.addRole( role );
                    roleAdded = true;
                    break;
                }
            }

            if ( !roleAdded )
            {
                assignment = new UserAssignment();
                assignment.addRole( role );
                assignment.setPrincipal( principal );

                store.addAssignment( assignment );
            }
        }
        else
        {
            throw new RbacStoreException( "role does not exist: " + roleId );
        }

    }


    public void removeRoleAssignment( String principal, int roleId )
        throws RbacStoreException
    {
        UserAssignment assignment = getUserAssignment( principal );

        for (Iterator i = assignment.getRoles().iterator(); i.hasNext(); )
        {
            Role r = (Role) i.next();

            if ( r.getId() == roleId )
            {
                assignment.removeRole( r );
            }
        }
    }


    private UserAssignment getUserAssignment( String principal )
        throws RbacStoreException
    {
        for ( Iterator i = store.getAssignments().iterator(); i.hasNext(); )
        {
            UserAssignment assignment = (UserAssignment) i.next();

            if ( principal != null && principal.equals( assignment.getPrincipal() ) )
            {
                return assignment;
            }
        }
        throw new RbacStoreException( "unable to locate user assignment for principal: " + principal );
    }


}
