package org.codehaus.plexus.redback.role;

/*
 * Copyright 2005 The Codehaus.
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

import org.codehaus.plexus.redback.role.model.RedbackRoleProfiles;


/**
 * RoleProfileManager:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $Id:$
 */
public interface RoleProfileManager
{

    /**
     * load the model and create/verify operations, resources, etc exist and make static roles
     * @param resourceLocation
     * @throws RoleProfileException
     */
    public abstract void loadRoleProfiles( String resourceLocation ) throws RoleProfileException;

    public abstract void loadRoleProfiles( RedbackRoleProfiles roleProfiles ) throws RoleProfileException;

    /**
     * locate a role with the corresponding name and generate it with the given resource, ${resource} 
     * in the model will be replaced with this resource string
     * 
     * @param roleName
     * @param resource
     * @throws RoleProfileException
     */
    public abstract void generateRole( String roleName, String resource ) throws RoleProfileException;

}