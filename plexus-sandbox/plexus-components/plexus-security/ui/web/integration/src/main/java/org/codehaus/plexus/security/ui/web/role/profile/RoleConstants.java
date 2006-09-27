package org.codehaus.plexus.security.ui.web.role.profile;

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

/**
 * RoleConstants:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 */
public class RoleConstants
{
    // roles
    public static final String SYSTEM_ADMINISTRATOR_ROLE = "System Administrator";
    public static final String USER_ADMINISTRATOR_ROLE = "User Administrator";
    public static final String REGISTERED_USER_ROLE = "Registered User";
    public static final String GUEST_ROLE = "Guest";

    // operations against configuration
    public static final String CONFIGURATION_EDIT_OPERATION = "configuration-edit";

    // operations against user
    public static final String USER_MANAGEMENT_USER_CREATE_OPERATION = "user-management-user-create";
    public static final String USER_MANAGEMENT_USER_EDIT_OPERATION = "user-management-user-edit";
    public static final String USER_MANAGEMENT_USER_DELETE_OPERATION = "user-management-user-delete";
    public static final String USER_MANAGEMENT_USER_LIST_OPERATION = "user-management-user-list";
    
    // operations against user assignment.
    public static final String USER_MANAGEMENT_ROLE_GRANT_OPERATION = "user-management-role-grant";
    public static final String USER_MANAGEMENT_ROLE_DROP_OPERATION = "user-management-role-drop";
    
    // operations against rbac objects.
    public static final String USER_MANAGEMENT_RBAC_ADMIN_OPERATION = "user-management-rbac-admin";
}