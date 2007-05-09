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

import org.codehaus.plexus.redback.rbac.Resource;
import org.codehaus.plexus.redback.role.RoleManager;
import org.codehaus.plexus.redback.role.model.RedbackRoleModel;
import org.codehaus.plexus.redback.xwork.action.AbstractSecurityAction;
import org.codehaus.plexus.redback.xwork.interceptor.SecureActionBundle;
import org.codehaus.plexus.redback.xwork.interceptor.SecureActionException;
import org.codehaus.plexus.redback.xwork.role.RoleConstants;

/**
 * RolesAction
 *
 * @author <a href="mailto:jmcconnell@apache.org">Jesse McConnell</a>
 * @version $Id: RolesAction.java 6386 2007-05-02 21:51:16Z jesse $
 * @plexus.component role="com.opensymphony.xwork.Action"
 * role-hint="redback-role-model"
 * instantiation-strategy="per-lookup"
 */
public class RoleModelAction
    extends AbstractSecurityAction
{
    /**
     * @plexus.requirement role-hint="default"
     */
    private RoleManager manager;

    private RedbackRoleModel model;

    public String view()
    {     
        model = manager.getModel();
        
        return SUCCESS;
    }

    public RedbackRoleModel getModel()
    {
        return model;
    }

    public void setModel( RedbackRoleModel model )
    {
        this.model = model;
    }

    public SecureActionBundle initSecureActionBundle()
        throws SecureActionException
    {
        SecureActionBundle bundle = new SecureActionBundle();
        bundle.setRequiresAuthentication( true );
        bundle.addRequiredAuthorization( RoleConstants.USER_MANAGEMENT_RBAC_ADMIN_OPERATION, Resource.GLOBAL );
        return bundle;
    }
}
