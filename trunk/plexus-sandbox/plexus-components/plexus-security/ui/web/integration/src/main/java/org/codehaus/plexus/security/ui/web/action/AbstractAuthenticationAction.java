package org.codehaus.plexus.security.ui.web.action;

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

import org.codehaus.plexus.security.system.SecuritySession;
import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.security.ui.web.WebSecurityConstants;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.xwork.action.PlexusActionSupport;

/**
 * AbstractAuthenticationAction 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class AbstractAuthenticationAction
    extends PlexusActionSupport
{
    // ------------------------------------------------------------------
    // Plexus Component Requirements
    // ------------------------------------------------------------------

    /**
     * @plexus.requirement
     */
    protected SecuritySystem securitySystem;
    
    // ------------------------------------------------------------------
    // Internal Support Methods
    // ------------------------------------------------------------------

    protected void setAuthTokens( SecuritySession securitySession, User user, boolean authStatus )
    {
        session.put( WebSecurityConstants.SECURITY_SESSION_KEY, securitySession );
        session.put( WebSecurityConstants.SECURITY_SESSION_USER, user );
        session.put( WebSecurityConstants.SECURITY_SESSION_USER, new Boolean( authStatus ) );
        this.setSession( session );
    }

}
