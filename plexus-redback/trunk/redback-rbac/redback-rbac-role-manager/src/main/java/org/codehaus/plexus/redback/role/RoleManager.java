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

import java.net.URL;

import org.codehaus.plexus.redback.role.model.RedbackRoleModel;

/**
 * RoleProfileManager:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $Id:$
 */
public interface RoleManager
{
    public static final String ROLE = RoleManager.class.getName();
    
    /**
     * load the model and create/verify operations, resources, etc exist and make static roles
     * @param resourceLocation
     * @throws RoleManagerException
     */
    public void loadRoleModel( URL resourceLocation ) throws RoleManagerException;

    public void loadRoleModel( RedbackRoleModel model ) throws RoleManagerException;

    /**
     * locate a role with the corresponding name and generate it with the given resource, ${resource} 
     * in the model will be replaced with this resource string, if this resource does not exist, it 
     * will be created.
     * 
     * @param roleName
     * @param resource
     * @throws RoleManagerException
     */
    public void createTemplatedRole( String templateId, String resource ) throws RoleManagerException;

    /**
     * removes a role corresponding to the role Id that was manufactured with the given resource
     * 
     * it also removes any user assignments for that role
     * 
     * @param roleId
     * @param resource
     * @throws RoleManagerException
     */
    public void removeTemplatedRole( String templateId, String resource ) throws RoleManagerException;
    
    
    /**
     * allows for a role coming from a template to be renamed effectively swapping out the bits of it that 
     * were labeled with the oldResource with the newResource
     * 
     * it also manages any user assignments for that role
     * 
     * @param templateId
     * @param oldResource
     * @param newResource
     * @throws RoleManagerException
     */
    public void updateRole( String templateId, String oldResource, String newResource ) throws RoleManagerException;
    
    
    /**
     * Assigns the role indicated by the roleId to the given principal
     * 
     * @param roleId
     * @param principal
     * @throws RoleManagerException
     */
    public void assignRole( String roleId, String principal ) throws RoleManagerException;
    
    /**
     * Unassigns the role indicated by the role id from the given principal
     * 
     * @param roleId
     * @param principal
     * @throws RoleManagerException
     */
    public void unassignRole( String roleId, String principal ) throws RoleManagerException;
    
    /**
     * true of a role exists with the given roleId
     * 
     * @param roleId
     * @return
     * @throws RoleManagerException
     */
    public boolean roleExists( String roleId ) throws RoleManagerException;

    /**
     * get the blessed model, the current operating instructions for all things role management
     */
    public RedbackRoleModel getModel();
}