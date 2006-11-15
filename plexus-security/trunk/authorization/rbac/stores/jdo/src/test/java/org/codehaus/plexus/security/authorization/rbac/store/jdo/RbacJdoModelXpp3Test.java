package org.codehaus.plexus.security.authorization.rbac.store.jdo;

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

import junit.framework.TestCase;
import org.codehaus.plexus.security.authorization.rbac.jdo.JdoOperation;
import org.codehaus.plexus.security.authorization.rbac.jdo.JdoPermission;
import org.codehaus.plexus.security.authorization.rbac.jdo.JdoResource;
import org.codehaus.plexus.security.authorization.rbac.jdo.JdoRole;
import org.codehaus.plexus.security.authorization.rbac.jdo.JdoUserAssignment;
import org.codehaus.plexus.security.authorization.rbac.jdo.RbacDatabase;
import org.codehaus.plexus.security.authorization.rbac.jdo.io.xpp3.RbacJdoModelXpp3Reader;
import org.codehaus.plexus.security.authorization.rbac.jdo.io.xpp3.RbacJdoModelXpp3Writer;
import org.codehaus.plexus.security.rbac.Operation;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Test the XPP3 reader and writer generated.
 */
public class RbacJdoModelXpp3Test
    extends TestCase
{
    public void testXpp3()
        throws IOException, XmlPullParserException
    {
        RbacDatabase database = new RbacDatabase();

        JdoRole role = new JdoRole();
        role.setAssignable( true );
        role.setDescription( "descriptor" );
        role.setName( "name" );
        role.setPermanent( true );
        role.addChildRoleName( "childRole1" );
        role.addChildRoleName( "childRole2" );

        JdoPermission permission = new JdoPermission();
        permission.setDescription( "permDesc" );
        permission.setName( "permName" );

        JdoOperation operation = new JdoOperation();
        operation.setDescription( "opDesc" );
        operation.setName( "opName" );
        operation.setPermanent( true );
        operation.setResourceRequired( true );
        permission.setOperation( operation );

        JdoResource resource = new JdoResource();
        resource.setIdentifier( "resId" );
        resource.setPattern( true );
        resource.setPermanent( true );
        permission.setResource( resource );
        permission.setPermanent( true );
        role.addPermission( permission );

        database.addRole( role );

        JdoUserAssignment assignment = new JdoUserAssignment();
        assignment.setPermanent( false );
        assignment.setPrincipal( "principal" );
        assignment.setTimestamp( new Date() );
        assignment.addRoleName( "name" );

        database.addUserAssignment( assignment );

        StringWriter w = new StringWriter();
        new RbacJdoModelXpp3Writer().write( w, database );

        RbacDatabase newDatabase = new RbacJdoModelXpp3Reader().read( new StringReader( w.toString() ) );

        List expectedRoles = database.getRoles();
        List roles = newDatabase.getRoles();
        assertEquals( expectedRoles.size(), roles.size() );
        for ( Iterator i = roles.iterator(); i.hasNext(); )
        {
            role = (JdoRole) i.next();

            boolean found = false;
            for ( Iterator j = expectedRoles.iterator(); j.hasNext(); )
            {
                JdoRole expectedRole = (JdoRole) j.next();
                if ( expectedRole.getName().equals( role.getName() ) )
                {
                    found = true;

                    assertRole( expectedRole, role );
                }
            }
            if ( !found )
            {
                fail( "Couldn't find role: " + role.getName() );
            }
        }

        List expectedUserAssignments = database.getUserAssignments();
        List userAssignments = newDatabase.getUserAssignments();
        assertEquals( expectedUserAssignments.size(), userAssignments.size() );
        for ( Iterator i = userAssignments.iterator(); i.hasNext(); )
        {
            assignment = (JdoUserAssignment) i.next();

            boolean found = false;
            for ( Iterator j = expectedUserAssignments.iterator(); j.hasNext(); )
            {
                JdoUserAssignment expectedAssignment = (JdoUserAssignment) j.next();
                if ( expectedAssignment.getPrincipal().equals( assignment.getPrincipal() ) )
                {
                    found = true;

                    assertUserAssignment( expectedAssignment, assignment );
                }
            }
            if ( !found )
            {
                fail( "Couldn't find assignment: " + assignment.getPrincipal() );
            }
        }
    }

    private void assertRole( JdoRole expectedRole, JdoRole role )
    {
        assertEquals( expectedRole.getDescription(), role.getDescription() );
        assertPermissions( expectedRole.getPermissions(), role.getPermissions() );
        assertEquals( expectedRole.getChildRoleNames(), role.getChildRoleNames() );
    }

    private void assertUserAssignment( JdoUserAssignment expectedAssignment, JdoUserAssignment assignment )
    {
        SimpleDateFormat sdf = new SimpleDateFormat( "EEE, d MMM yyyy HH:mm:ss Z" );
        assertNotNull( expectedAssignment.getTimestamp() );
        assertNotNull( assignment.getTimestamp() );

        assertEquals( sdf.format( expectedAssignment.getTimestamp() ), sdf.format( assignment.getTimestamp() ) );
        assertEquals( expectedAssignment.getRoleNames(), assignment.getRoleNames() );
    }

    private void assertPermissions( List expectedPermissions, List permissions )
    {
        assertEquals( expectedPermissions.size(), permissions.size() );
        for ( Iterator i = permissions.iterator(); i.hasNext(); )
        {
            JdoPermission permission = (JdoPermission) i.next();

            boolean found = false;
            for ( Iterator j = expectedPermissions.iterator(); j.hasNext(); )
            {
                JdoPermission expectedPermission = (JdoPermission) j.next();
                if ( expectedPermission.getName().equals( permission.getName() ) )
                {
                    found = true;

                    assertPermission( expectedPermission, permission );
                }
            }
            if ( !found )
            {
                fail( "Couldn't find permission: " + permission.getName() );
            }
        }
    }

    private void assertPermission( JdoPermission expectedPermission, JdoPermission permission )
    {
        assertEquals( expectedPermission.getDescription(), permission.getDescription() );
        assertOperation( expectedPermission.getOperation(), permission.getOperation() );
        assertResource( expectedPermission.getResource(), permission.getResource() );
    }

    private void assertResource( Resource expectedResource, Resource resource )
    {
        assertEquals( expectedResource.getIdentifier(), resource.getIdentifier() );
    }

    private void assertOperation( Operation expectedOperation, Operation operation )
    {
        assertEquals( expectedOperation.getName(), operation.getName() );
        assertEquals( expectedOperation.getDescription(), operation.getDescription() );
    }
}
