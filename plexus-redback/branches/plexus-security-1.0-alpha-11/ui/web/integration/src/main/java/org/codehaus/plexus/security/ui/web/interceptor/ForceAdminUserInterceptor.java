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

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.UserNotFoundException;

/**
 * EnvironmentCheckInterceptor
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id: EnvironmentCheckInterceptor.java 4057 2006-09-15 23:43:16Z joakime $
 * @plexus.component role="com.opensymphony.xwork.interceptor.Interceptor"
 * role-hint="pssForceAdminUserInterceptor"
 */
public class ForceAdminUserInterceptor
    extends AbstractLogEnabled
    implements Interceptor
{
    private static final String SECURITY_ADMIN_USER_NEEDED = "security-admin-user-needed";

    private static boolean checked = false;

    /**
     * @plexus.requirement role-hint="jdo"
     */
    private UserManager userManager;

    public void destroy()
    {
        // no-op
    }

    public void init()
    {

    }

    public String intercept( ActionInvocation invocation )
        throws Exception
    {
        if ( checked )
        {
            return invocation.invoke();
        }

        try
        {
            User user = userManager.findUser( "admin" );
            if ( user == null )
            {
                getLogger().info( "No admin user configured - forwarding to admin user creation page." );
                return SECURITY_ADMIN_USER_NEEDED;
            }
            checked = true;
            getLogger().info( "Admin user found. No need to configure admin user." );
        }
        catch ( UserNotFoundException e )
        {
            getLogger().info( "No admin user found - forwarding to admin user creation page." );
            return SECURITY_ADMIN_USER_NEEDED;
        }

        return invocation.invoke();
    }
}
