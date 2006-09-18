package org.codehaus.plexus.rbac.template;

import org.codehaus.plexus.security.rbac.Role;
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
 * RoleTemplateManager:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 */
public interface RoleTemplateManager
{
    String ROLE = RoleTemplateManager.class.getName();

    public Role getRole( String roleName )
        throws RoleTemplateException;

    public Role getRole( String roleName, String resource )
        throws RoleTemplateException;

}