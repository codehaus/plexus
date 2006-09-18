package org.codehaus.plexus.rbac.template;

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

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.security.rbac.RBACManager;

/**
 * AbstractRoleTemplate:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 */
public abstract class AbstractRoleTemplate
    implements RoleTemplate, Initializable
{
    /**
     * @plexus.requirement
     */
    protected RBACManager rbacManager;

    public void initialize()
        throws InitializationException
    {
        if ( !rbacManager.roleExists( getRoleName() ) )
        {
            try
            {
                createRole();
            }
            catch ( RoleTemplateException e )
            {
                throw new InitializationException( "unable to initialize role " + getRoleName() );
            }
        }
    }
}