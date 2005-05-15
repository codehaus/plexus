package org.codehaus.plexus.security.rbac.permission;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import java.util.HashSet;
import java.util.Set;

/**
 * A default implementation of the IPermission interface.
 */
public class DefaultPermission extends AbstractPermission
{
    /**
     * The current set of permission entries.
     */
    private final PermissionEntry[] entries;

    /**
     * Constructs with a set of permission entries.
     * Note that client is responsible for ensuring there is no duplicate entries
     * in the specified permission entry list.
     */
    public DefaultPermission( PermissionEntry[] entries )
    {
        this.entries = entries;
    }

    /**
     * Constructs with a single permission entry.
     *
     * @param obj the target object.
     * @param op  the operation.
     */
    public DefaultPermission( Object obj, String op )
    {
        this.entries = new PermissionEntry[]{new DefaultPermissionEntry( obj, op )};
    }

    /**
     * Constructs with a set of permission objects.
     */
    public DefaultPermission( Permission[] p )
    {
        Set set = new HashSet();
        for ( int i = 0; i < p.length; i++ )
        {
            PermissionEntry[] e = p[i].getPermissionEntries();
            for ( int j = 0; j < e.length; j++ )
            {
                set.add( e[j] );
            }
        }
        this.entries = (PermissionEntry[]) set.toArray( PermissionEntry.ZERO_PERMISSION_ENTRY );
    }

    /**
     * Returns the permission entry set of this permission.
     */
    public PermissionEntry[] getPermissionEntries()
    {
        return entries;
    }
}
