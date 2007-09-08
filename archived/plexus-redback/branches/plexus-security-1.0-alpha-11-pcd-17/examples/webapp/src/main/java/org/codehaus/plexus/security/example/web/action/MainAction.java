package org.codehaus.plexus.security.example.web.action;

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
 * MainAction 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component
 *   role="com.opensymphony.xwork.Action"
 *   role-hint="main"
 */
public class MainAction
    extends PlexusActionSupport
{
    /**
     * @plexus.requirement
     */
    private SecuritySystem securitySystem;

    public String show()
    {
        if ( securitySystem == null )
        {
            session.put( "SecuritySystemWARNING", "SecuritySystem is null!" );
        }
        else
        {
            session.put( "security_id_authenticator", securitySystem.getAuthenticatorId() );
            session.put( "security_id_authorizor", securitySystem.getAuthorizerId() );
            session.put( "security_id_user_management", securitySystem.getUserManagementId() );
        }

        return SUCCESS;
    }
}
