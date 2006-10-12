package org.codehaus.plexus.security.ui.web.model;

/*
 * Copyright 2001-2006 The Codehaus.
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

import org.codehaus.plexus.security.rbac.Permission;
import org.codehaus.plexus.security.rbac.Role;

import java.util.Iterator;

/**
 * EditRoleDetails - Existing user Role Details. 
 * 
 * This is a placeholder for information passed back 
 * and forth between the Action and the Client.
 * 
 * We intentionally do not hook up the actual object to prevent
 * creative injection of fields and values by the untrusted client. 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class EditRoleDetails
    extends RoleDetails
{
    public EditRoleDetails( Role role )
    {
        super.setName( role.getName() );
        super.setDescription( role.getDescription() );
        Iterator it;
        
        it = role.getChildRoleNames().iterator();
        while(it.hasNext())
        {
            super.addChildRoleName( (String) it.next() );
        }
        
        it = role.getPermissions().iterator();
        while(it.hasNext())
        {
            Permission perm = (Permission) it.next();
            super.addPermission( perm.getName(), perm.getOperation().getName(), perm.getResource().getIdentifier() );
        }
    }

    public void setName( String name )
    {
        // Prevent Change (This is an edit after all)
    }
}
