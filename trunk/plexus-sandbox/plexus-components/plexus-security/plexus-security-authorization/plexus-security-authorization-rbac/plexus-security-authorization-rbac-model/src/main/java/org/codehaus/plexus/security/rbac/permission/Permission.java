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
 * Permission which represents a set of operations
 * that can be performed on a set of target objects.
 */
public interface Permission
{
    /**
     * Permits all operations on all objects.
     */
    public static final Permission ALL_PERMISSION =
        new DefaultPermission( Operation.ALL_OPERATION, Operation.ALL_OBJECT );
    /**
     * Permits no operations on any object.
     */
    public static final Permission NO_PERMISSION = new AbstractPermission();
    /**
     * A null permission set.
     */
    public static final Set ZERO_PERMISSION = new HashSet();

    /**
     * Returns the permission entry set of this permission.
     */
    public Set getPermissionEntries();

    /**
     * Returns true iff this permission is greater than or equal to
     * the given permission in terms of access privileges.
     */
    public boolean ge( Permission p );

    /**
     * Returns the set of roles assigned to this permission.
     * For advanced permission-role review.
     */
    public Set getAssignedRoles();
}
