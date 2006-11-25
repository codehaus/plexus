package org.codehaus.plexus.security.management;

/*
 * Copyright 2006 The Apache Software Foundation.
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

import org.codehaus.plexus.security.authorization.rbac.jdo.RbacDatabase;
import org.codehaus.plexus.security.authorization.rbac.jdo.io.stax.RbacJdoModelStaxReader;
import org.codehaus.plexus.security.authorization.rbac.jdo.io.stax.RbacJdoModelStaxWriter;
import org.codehaus.plexus.security.keys.AuthenticationKey;
import org.codehaus.plexus.security.keys.KeyManager;
import org.codehaus.plexus.security.keys.jdo.AuthenticationKeyDatabase;
import org.codehaus.plexus.security.keys.jdo.io.stax.PlexusSecurityKeyManagementJdoStaxReader;
import org.codehaus.plexus.security.keys.jdo.io.stax.PlexusSecurityKeyManagementJdoStaxWriter;
import org.codehaus.plexus.security.rbac.Operation;
import org.codehaus.plexus.security.rbac.Permission;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacManagerException;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.rbac.Role;
import org.codehaus.plexus.security.rbac.UserAssignment;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.jdo.UserDatabase;
import org.codehaus.plexus.security.user.jdo.io.stax.UserManagementStaxReader;
import org.codehaus.plexus.security.user.jdo.io.stax.UserManagementStaxWriter;
import org.codehaus.plexus.util.IOUtil;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * JDO implementation of the data management tool.
 *
 * @todo do we really need JDO specifics here? Could optimize by going straight to JDOFactory
 * @todo check whether this current method logs everything unnecessarily.
 * @plexus.component role="org.codehaus.plexus.security.management.DataManagementTool" role-hint="jdo"
 */
public class JdoDataManagementTool
    implements DataManagementTool
{
    private static final String USERS_XML_NAME = "users.xml";

    private static final String KEYS_XML_NAME = "keys.xml";

    private static final String RBAC_XML_NAME = "rbac.xml";

    public void backupRBACDatabase( RBACManager manager, File backupDirectory )
        throws RbacManagerException, IOException, XMLStreamException
    {
        // TODO: this is inefficient - many of the resources, operations and permissions may be duplicated. We should
        //       have the modello stax writer write reference IDs, and add the operations, resources and permissions to
        //       the database itself

        RbacDatabase database = new RbacDatabase();
        database.setRoles( manager.getAllRoles() );
        database.setUserAssignments( manager.getAllUserAssignments() );

        RbacJdoModelStaxWriter writer = new RbacJdoModelStaxWriter();
        FileWriter fileWriter = new FileWriter( new File( backupDirectory, RBAC_XML_NAME ) );
        try
        {
            writer.write( fileWriter, database );
        }
        finally
        {
            IOUtil.close( fileWriter );
        }
    }

    public void backupUserDatabase( UserManager manager, File backupDirectory )
        throws IOException, XMLStreamException
    {
        UserDatabase database = new UserDatabase();
        database.setUsers( manager.getUsers() );

        UserManagementStaxWriter writer = new UserManagementStaxWriter();
        FileWriter fileWriter = new FileWriter( new File( backupDirectory, USERS_XML_NAME ) );
        try
        {
            writer.write( fileWriter, database );
        }
        finally
        {
            IOUtil.close( fileWriter );
        }
    }

    public void backupKeyDatabase( KeyManager manager, File backupDirectory )
        throws IOException, XMLStreamException
    {
        AuthenticationKeyDatabase database = new AuthenticationKeyDatabase();
        database.setKeys( manager.getAllKeys() );

        PlexusSecurityKeyManagementJdoStaxWriter writer = new PlexusSecurityKeyManagementJdoStaxWriter();
        FileWriter fileWriter = new FileWriter( new File( backupDirectory, KEYS_XML_NAME ) );
        try
        {
            writer.write( fileWriter, database );
        }
        finally
        {
            IOUtil.close( fileWriter );
        }
    }

    public void restoreRBACDatabase( RBACManager manager, File backupDirectory )
        throws IOException, XMLStreamException, RbacManagerException
    {
        RbacJdoModelStaxReader reader = new RbacJdoModelStaxReader();

        FileReader fileReader = new FileReader( new File( backupDirectory, RBAC_XML_NAME ) );

        RbacDatabase database;
        try
        {
            database = reader.read( fileReader );
        }
        finally
        {
            IOUtil.close( fileReader );
        }

        Map permissionMap = new HashMap();
        Map resources = new HashMap();
        Map operations = new HashMap();
        for ( Iterator i = database.getRoles().iterator(); i.hasNext(); )
        {
            Role role = (Role) i.next();

            // TODO: this could be generally useful and put into saveRole itself as long as the performance penalty isn't too harsh.
            //   Currently it always saves everything where it could pull pack the existing permissions, etc if they exist
            List permissions = new ArrayList();
            for ( Iterator j = role.getPermissions().iterator(); j.hasNext(); )
            {
                Permission permission = (Permission) j.next();

                if ( permissionMap.containsKey( permission.getName() ) )
                {
                    permission = (Permission) permissionMap.get( permission.getName() );
                }
                else if ( manager.permissionExists( permission ) )
                {
                    permission = manager.getPermission( permission.getName() );
                    permissionMap.put( permission.getName(), permission );
                }
                else
                {
                    Operation operation = permission.getOperation();
                    if ( operations.containsKey( operation.getName() ) )
                    {
                        operation = (Operation) operations.get( operation.getName() );
                    }
                    else if ( manager.operationExists( operation ) )
                    {
                        operation = manager.getOperation( operation.getName() );
                        operations.put( operation.getName(), operation );
                    }
                    else
                    {
                        operation = manager.saveOperation( operation );
                        operations.put( operation.getName(), operation );
                    }
                    permission.setOperation( operation );

                    Resource resource = permission.getResource();
                    if ( resources.containsKey( resource.getIdentifier() ) )
                    {
                        resource = (Resource) resources.get( resource.getIdentifier() );
                    }
                    else if ( manager.resourceExists( resource ) )
                    {
                        resource = manager.getResource( resource.getIdentifier() );
                        resources.put( resource.getIdentifier(), resource );
                    }
                    else
                    {
                        resource = manager.saveResource( resource );
                        resources.put( resource.getIdentifier(), resource );
                    }
                    permission.setResource( resource );

                    permission = manager.savePermission( permission );
                    permissionMap.put( permission.getName(), permission );
                }
                permissions.add( permission );
            }
            role.setPermissions( permissions );

            manager.saveRole( role );
        }

        for ( Iterator i = database.getUserAssignments().iterator(); i.hasNext(); )
        {
            UserAssignment userAssignment = (UserAssignment) i.next();

            manager.saveUserAssignment( userAssignment );
        }
    }

    public void restoreUsersDatabase( UserManager manager, File backupDirectory )
        throws IOException, XMLStreamException
    {
        UserManagementStaxReader reader = new UserManagementStaxReader();

        FileReader fileReader = new FileReader( new File( backupDirectory, USERS_XML_NAME ) );

        UserDatabase database;
        try
        {
            database = reader.read( fileReader );
        }
        finally
        {
            IOUtil.close( fileReader );
        }

        for ( Iterator i = database.getUsers().iterator(); i.hasNext(); )
        {
            User user = (User) i.next();

            manager.addUserUnchecked( user );
        }
    }

    public void restoreKeysDatabase( KeyManager manager, File backupDirectory )
        throws IOException, XMLStreamException
    {
        PlexusSecurityKeyManagementJdoStaxReader reader = new PlexusSecurityKeyManagementJdoStaxReader();

        FileReader fileReader = new FileReader( new File( backupDirectory, KEYS_XML_NAME ) );

        AuthenticationKeyDatabase database;
        try
        {
            database = reader.read( fileReader );
        }
        finally
        {
            IOUtil.close( fileReader );
        }

        for ( Iterator i = database.getKeys().iterator(); i.hasNext(); )
        {
            AuthenticationKey key = (AuthenticationKey) i.next();

            manager.addKey( key );
        }
    }
}
