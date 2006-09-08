package org.codehaus.plexus.security.rbac.permission;

import java.util.Set;
import java.util.HashSet;

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


/**
 * Permission entry which represents a single operation on a target object.
 * @deprecated use {@link org.codehaus.plexus.security.rbac.Operation} interface instead.
 */
public interface Operation
{
    /**
     * A null set of permission entries.
     */
    public static final Set ZERO_PERMISSION_ENTRY = new HashSet();
    /**
     * Matches all operations.
     */
    public static final String ALL_OPERATION = "*";
    /**
     * Matches all target objects.
     */
    public static final String ALL_OBJECT = "*";

    /**
     * Returns the operation of this permission entry.
     */
    public String getOperation();

    /**
     * Returns the target object of this permission entry.
     */
    public Object getObject();

    /**
     * Returns true iff this permission entry is greater than or equal to
     * the given permission entry in terms of access privileges.
     */
    public boolean ge( Operation pe );
}
