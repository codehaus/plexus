package org.codehaus.plexus.security.ui.web.action.admin;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

/**
 * SystemInfoAction 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="com.opensymphony.xwork.Action"
 *                   role-hint="pss-sysinfo"
 *                   instantiation-strategy="per-lookup"
 */
public class SystemInfoAction
    extends PlexusActionSupport
{
    // ------------------------------------------------------------------
    // Plexus Component Requirements
    // ------------------------------------------------------------------

    /**
     * @plexus.requirement
     */
    private SecuritySystem securitySystem;
    
    // ------------------------------------------------------------------
    // Action Parameters
    // ------------------------------------------------------------------

    private String authentication;

    private String authorization;

    private String userManagement;
    
    private String keyManagement;
    
    private String policy;
    
    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------

    public String show()
    {
        authentication = securitySystem.getAuthenticatorId();
        authorization = securitySystem.getAuthorizerId();
        userManagement = securitySystem.getUserManagementId();
        keyManagement = securitySystem.getKeyManagementId();
        policy = securitySystem.getPolicyId();

        return SUCCESS;
    }
    
    // ------------------------------------------------------------------
    // Parameter Accessor Methods
    // ------------------------------------------------------------------

    public String getAuthentication()
    {
        return authentication;
    }

    public String getAuthorization()
    {
        return authorization;
    }

    public String getUserManagement()
    {
        return userManagement;
    }

    public String getKeyManagement()
    {
        return keyManagement;
    }

    public String getPolicy()
    {
        return policy;
    }
}
