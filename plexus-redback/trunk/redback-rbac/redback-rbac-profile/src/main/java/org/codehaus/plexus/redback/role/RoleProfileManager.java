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

import org.codehaus.plexus.redback.role.model.RedbackRoleModel;

/**
 * RoleProfileManager:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $Id:$
 */
public interface RoleProfileManager
{
    public static final String ROLE = RoleProfileManager.class.getName();
    
    /**
     * load the model and create/verify operations, resources, etc exist and make static roles
     * @param resourceLocation
     * @throws RoleProfileException
     */
    public void loadRoleModel( String resourceLocation ) throws RoleProfileException;

    public void loadRoleModel( RedbackRoleModel model ) throws RoleProfileException;

    /**
     * locate a role with the corresponding name and generate it with the given resource, ${resource} 
     * in the model will be replaced with this resource string, if this resource does not exist, it 
     * will be created.
     * 
     * @param roleName
     * @param resource
     * @throws RoleProfileException
     */
    public void createRole( String templateId, String resource ) throws RoleProfileException;

    /**
     * removes a role corresponding to the role Id that was manufactured with the given resource
     * 
     * @param roleId
     * @param resource
     * @throws RoleProfileException
     */
    public void removeRole( String templateId, String resource ) throws RoleProfileException;
    
    
}