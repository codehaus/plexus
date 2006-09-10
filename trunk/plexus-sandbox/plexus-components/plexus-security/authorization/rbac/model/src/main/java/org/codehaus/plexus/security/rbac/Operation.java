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
 * Operation
 *
 * In RBAC the operation is an action or functionality that can be linked with a
 * particular resource into an assignable Permission.  Operations don't exist outside
 * Permissions.
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public interface Operation
{
    /**
     * Plexus Role Name
     */
    public static final String ROLE = Operation.class.getName();

    /**
     * Long description of an operation.
     *
     * @return String
     */
    public String getDescription();

    /**
     * name of the operation that is used in the act of authorization
     *
     * 'modify-foo', 'change-password'
     * 
     * NOTE: This field is considered the Primary Key for this object.
     * 
     * @return the name of the operation.
     */
    public String getName();

    /**
     *
     * @param description
     */
    public void setDescription( String description );

    /**
     * Set name of the operation that is used in the act of authorization
     * 
     * 'modify-foo', 'change-password'
     * 
     * NOTE: This field is considered the Primary Key for this object.
     * 
     * @param name
     */
    public void setName( String name );
}