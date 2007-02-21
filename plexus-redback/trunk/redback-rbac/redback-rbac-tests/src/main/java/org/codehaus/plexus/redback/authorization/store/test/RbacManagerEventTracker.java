package org.codehaus.plexus.redback.authorization.store.test;

/*
 * Copyright 2001-2006 The Codehaus.
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

import org.codehaus.plexus.redback.rbac.Permission;
import org.codehaus.plexus.redback.rbac.RBACManagerListener;
import org.codehaus.plexus.redback.rbac.Role;
import org.codehaus.plexus.redback.rbac.UserAssignment;

import java.util.ArrayList;
import java.util.List;

/**
 * RbacManagerEventTracker
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class RbacManagerEventTracker
    implements RBACManagerListener
{
    public long initCount = 0;

    public Boolean lastDbFreshness;

    public List addedRoleNames = new ArrayList();

    public List removedRoleNames = new ArrayList();

    public List addedPermissionNames = new ArrayList();

    public List removedPermissionNames = new ArrayList();

    public void rbacInit( boolean freshdb )
    {
        log( "Init - freshdb: " + freshdb );
        initCount++;
        lastDbFreshness = Boolean.valueOf( freshdb );
    }

    public void rbacPermissionRemoved( Permission permission )
    {
        log( "Permission Removed: " + permission.getName() );
        addUnique( removedPermissionNames, permission.getName() );
    }

    public void rbacPermissionSaved( Permission permission )
    {
        log( "Permission Saved: " + permission.getName() );
        addUnique( addedPermissionNames, permission.getName() );
    }

    public void rbacRoleRemoved( Role role )
    {
        log( "Role Removed: " + role.getName() );
        addUnique( removedRoleNames, role.getName() );
    }

    public void rbacRoleSaved( Role role )
    {
        log( "Role Saved: " + role.getName() );
        addUnique( addedRoleNames, role.getName() );
    }

    public void rbacUserAssignmentRemoved( UserAssignment userAssignment )
    {

    }

    public void rbacUserAssignmentSaved( UserAssignment userAssignment )
    {

    }

    private void addUnique( List list, Object obj )
    {
        if ( !list.contains( obj ) )
        {
            list.add( obj );
        }
    }

    private void log( String msg )
    {
        System.out.println( "[RBAC Event Tracker] " + msg );
    }
}
