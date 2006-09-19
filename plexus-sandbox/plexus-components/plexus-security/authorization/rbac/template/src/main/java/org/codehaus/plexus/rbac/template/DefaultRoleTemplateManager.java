package org.codehaus.plexus.rbac.template;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.security.rbac.RBACManager;
import org.codehaus.plexus.security.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.security.rbac.Role;

import java.util.List;
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
 * DefaultRoleTemplateManager:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.rbac.template.RoleTemplateManager"
 *   role-hint="default"
 */
public class DefaultRoleTemplateManager
    extends AbstractLogEnabled
    implements RoleTemplateManager
{
    /**
     * @plexus.requirement
     */
    private PlexusContainer container;

    /**
     * @plexus.requirement
     */
    private RBACManager rbacManager;

    /**
     * @plexus.requirement role="org.codehaus.plexus.rbac.template.RoleTemplate"
     */
    List roleTemplates;
    
    /**
     *
     * @param roleTemplateName
     * @return
     * @throws org.codehaus.plexus.rbac.template.RoleTemplateException
     */
    public Role getRole( String roleTemplateName )
        throws RoleTemplateException
    {
        try
        {
            RoleTemplate roleTemplate =  (RoleTemplate)container.lookup( RoleTemplate.ROLE, roleTemplateName );

            getLogger().info("role template found " + roleTemplate.getRoleName() );
            getLogger().info( "role found " + rbacManager.getRole( roleTemplate.getRoleName()).getName());
System.out.println( "rbacmanager in role manager " + rbacManager.toString());
            return rbacManager.getRole( roleTemplate.getRoleName() );
        }
        catch ( ComponentLookupException cle )
        {
            throw new RoleTemplateException( "unable to locate role template " + roleTemplateName, cle );
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RoleTemplateException( "unable to locate role " + roleTemplateName, ne );
        }
    }

    public Role getRole( String roleTemplateName, String resource )
        throws RoleTemplateException
    {
        try
        {
            DynamicRoleTemplate roleTemplate =  (DynamicRoleTemplate)container.lookup( DynamicRoleTemplate.ROLE, roleTemplateName );

            if ( rbacManager.roleExists( roleTemplate.getRoleName( roleTemplateName, resource ) ) )
            {
                return rbacManager.getRole( roleTemplate.getRoleName( roleTemplateName, resource ) );
            }
            else
            {
                roleTemplate.createRole( roleTemplateName, resource );

                return rbacManager.getRole( roleTemplate.getRoleName( roleTemplateName, resource ) );
            }
        }
        catch ( ComponentLookupException cle )
        {
            throw new RoleTemplateException( "unable to locate role template " + roleTemplateName, cle );
        }
        catch ( RbacObjectNotFoundException ne )
        {
            throw new RoleTemplateException( "unable to locate role " + roleTemplateName, ne );
        }
    }
}