package org.codehaus.plexus.redback.xwork.action.admin;

/*
 * Copyright 2005-2006 The Codehaus.
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

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.redback.rbac.RBACManager;
import org.codehaus.plexus.redback.rbac.RbacManagerException;
import org.codehaus.plexus.redback.rbac.RbacObjectInvalidException;
import org.codehaus.plexus.redback.rbac.RbacObjectNotFoundException;
import org.codehaus.plexus.redback.rbac.Resource;
import org.codehaus.plexus.redback.users.UserManager;
import org.codehaus.plexus.redback.users.UserNotFoundException;
import org.codehaus.plexus.redback.xwork.action.AbstractSecurityAction;
import org.codehaus.plexus.redback.xwork.action.CancellableAction;
import org.codehaus.plexus.redback.xwork.interceptor.SecureActionBundle;
import org.codehaus.plexus.redback.xwork.interceptor.SecureActionException;
import org.codehaus.plexus.redback.xwork.role.RoleConstants;
import org.codehaus.plexus.util.StringUtils;

/**
 * UserDeleteAction
 * 
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="com.opensymphony.xwork.Action" role-hint="redback-admin-user-delete"
 *                   instantiation-strategy="per-lookup"
 */
public class UserDeleteAction extends AbstractSecurityAction implements CancellableAction
{
    // ------------------------------------------------------------------
    // Plexus Component Requirements
    // ------------------------------------------------------------------

    /**
     * @plexus.requirement role-hint="cached"
     */
    private UserManager userManager;

    /**
     * @plexus.requirement role-hint="cached"
     */
    private RBACManager rbacManager;

    // ------------------------------------------------------------------
    // Action Parameters
    // ------------------------------------------------------------------

    private String username;

    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------

    public String confirm()
    {
        if ( username == null )
        {
            addActionError( getText( "cannot.remove.user.null.username" ) );
            return SUCCESS;
        }

        return INPUT;
    }

    public String submit()
    {
        if ( username == null )
        {
            addActionError( getText( "invalid.user.credentials" ) );
            return SUCCESS;
        }

        if ( StringUtils.isEmpty( username ) )
        {
            addActionError( getText( "cannot.remove.user.empty.username" ) );
            return SUCCESS;
        }

        try
        {
            rbacManager.removeUserAssignment( username );
        }
        catch ( RbacObjectNotFoundException e )
        {
            // ignore, this is possible since the user may never have had roles assigned
        }
        catch ( RbacObjectInvalidException e )
        {
            List list = new ArrayList();
            list.add( username );
            list.add( e.getMessage() );
            addActionError( getText( "cannot.remove.user.role", list ) );
        }
        catch ( RbacManagerException e )
        {
            List list = new ArrayList();
            list.add( username );
            list.add( e.getMessage() );
            addActionError( getText( "cannot.remove.user.role", list ) );
        }

        if ( getActionErrors().isEmpty() )
        {
            try
            {
                userManager.deleteUser( username );
            }
            catch ( UserNotFoundException e )
            {
                List list = new ArrayList();
                list.add( username );
                addActionError( getText( "cannot.remove.user.non.existent", list ) );
            }
        }

        return SUCCESS;
    }

    /**
     * Returns the cancel result. <p/> A basic implementation would simply be to return CANCEL.
     * 
     * @return
     */
    public String cancel()
    {
        return CANCEL;
    }

    // ------------------------------------------------------------------
    // Parameter Accessor Methods
    // ------------------------------------------------------------------

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public SecureActionBundle initSecureActionBundle() throws SecureActionException
    {
        SecureActionBundle bundle = new SecureActionBundle();
        bundle.setRequiresAuthentication( true );
        bundle.addRequiredAuthorization( RoleConstants.USER_MANAGEMENT_USER_DELETE_OPERATION, Resource.GLOBAL );
        return bundle;
    }

}
