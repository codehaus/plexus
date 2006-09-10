package org.codehaus.plexus.security.rbac;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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
 * Permission
 *
 * A permission is the wrapper for an operation and a resource effectively saying
 * that the operation is authorized for that resource.
 *
 * P(Operation, Resource)
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public interface Permission
{
    /**
     * Plexus Role Name
     */
    public static final String ROLE = Permission.class.getName();

    /**
     * Implementation managed ID for this object.
     *
     * NOTE: There is intentionally no .setId(int) object.
     *
     * @return the id for this object.
     */
    public int getId();

    /**
     * Long description of the Permission
     */
    public String getDescription();

    /**
     * short name of the permission
     */
    public String getName();

    /**
     * Operation that this permission is authorizing
     */
    public Operation getOperation();

    /**
     * This is the resource associated with this permission.
     *
     * Implementors must always supply a Resource.
     *
     * @return the Resource.
     */
    public Resource getResource();

    /**
     * Set null
     *
     * @param description
     */
    public void setDescription( String description );

    /**
     * Set null
     *
     * @param name
     */
    public void setName( String name );

    /**
     * Set null
     *
     * @param operation
     */
    public void setOperation( Operation operation );

    /**
     *
     * @param resource
     */
    public void setResource( Resource resource );

}