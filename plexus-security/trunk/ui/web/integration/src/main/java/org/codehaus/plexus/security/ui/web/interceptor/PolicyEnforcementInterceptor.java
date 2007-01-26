package org.codehaus.plexus.security.ui.web.interceptor;

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

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.security.system.SecuritySession;
import org.codehaus.plexus.security.system.SecuritySystemConstants;

import javax.servlet.http.HttpSession;

/**
 * Interceptor to force the user to perform actions, when required.
 *
 * @author Edwin Punzalan
 * @plexus.component role="com.opensymphony.xwork.interceptor.Interceptor" role-hint="pssPolicyEnforcementInterceptor"
 */
public class PolicyEnforcementInterceptor
        extends AbstractLogEnabled
    implements Interceptor
{
   private static final String SECURITY_USER_MUST_CHANGE_PASSWORD = "must-change-password";

    public void destroy()
    {
        //ignore
    }

    public void init()
    {
        //ignore
    }

    public String intercept( ActionInvocation actionInvocation )
        throws Exception
    {
        SecuritySession session = getSecuritySession();

        if ( session != null )
        {
            if ( session.getUser().isPasswordChangeRequired() )
            {
                getLogger().info( "User must change password - forwarding to change password page." );

                return SECURITY_USER_MUST_CHANGE_PASSWORD;
            }
        }

        return actionInvocation.invoke();
    }

    private SecuritySession getSecuritySession()
    {
        HttpSession session = ServletActionContext.getRequest().getSession();
        if ( session == null )
        {
            getLogger().debug( "No HTTP Session exists." );
            return null;
        }

        SecuritySession secSession =
            (SecuritySession) session.getAttribute( SecuritySystemConstants.SECURITY_SESSION_KEY );

        return secSession;
    }
}
