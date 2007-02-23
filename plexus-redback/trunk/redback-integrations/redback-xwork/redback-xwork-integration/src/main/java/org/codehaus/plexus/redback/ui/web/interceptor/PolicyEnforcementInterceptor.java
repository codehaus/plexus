package org.codehaus.plexus.redback.ui.web.interceptor;

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

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.redback.configuration.UserConfiguration;
import org.codehaus.plexus.redback.system.SecuritySession;
import org.codehaus.plexus.redback.system.SecuritySystemConstants;
import org.codehaus.plexus.redback.system.SecuritySystem;
import org.codehaus.plexus.redback.system.DefaultSecuritySession;
import org.codehaus.plexus.redback.users.UserManager;
import org.codehaus.plexus.redback.users.User;

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
   private static final String SECURITY_USER_MUST_CHANGE_PASSWORD = "security-must-change-password";

    /**
     * @plexus.requirement
     */
    private UserConfiguration config;

    /**
     * @plexus.requirement
     */
    protected SecuritySystem securitySystem;

    public void destroy()
    {
        //ignore
    }

    public void init()
    {
        //ignore
    }

    /**
     * 1) validate that the user doesn't have to change their password, if they do then re-route accordingly
     *
     * @param actionInvocation
     * @return
     * @throws Exception
     */
    public String intercept( ActionInvocation actionInvocation )
        throws Exception
    {

        if ( config.getBoolean( "security.policy.strict.enforcement.enabled" ) )
        {
            getLogger().debug( "Enforcement: enforcing per click security policies." );


            ActionContext context = ActionContext.getContext();

            SecuritySession securitySession =
                    (SecuritySession) context.getSession().get( SecuritySystemConstants.SECURITY_SESSION_KEY );

            if ( securitySession != null )
            {
                UserManager userManager = securitySystem.getUserManager();
                User user = userManager.findUser( securitySession.getUser().getPrincipal() );
                securitySession = new DefaultSecuritySession( securitySession.getAuthenticationResult(), user );
                context.getSession().put( SecuritySystemConstants.SECURITY_SESSION_KEY, securitySession ); 
            }
            else
            {
                getLogger().debug( "Enforcement: no user security session detected, skipping enforcement" );
                return actionInvocation.invoke();
            }

            if ( checkForcePasswordChange( securitySession, actionInvocation ) )
            {
                return SECURITY_USER_MUST_CHANGE_PASSWORD;
            }
            
            return actionInvocation.invoke();
        }
        else
        {
            getLogger().debug( "Enforcement: not processing per click security policies." );
            return actionInvocation.invoke();
        }
    }


    private boolean checkForcePasswordChange( SecuritySession securitySession, ActionInvocation actionInvocation )
    {
        /*
         * FIXME: something less 'hackish'
         * 
         * these two classes should not be subject to this enforcement policy and this
         * ideally should be governed by the interceptor stacks but that just didn't work
         * when I was trying to solve the problem that way, psquad32 recommended I just
         * find a way to get around this interceptor in the particular case I needed to and use
         * "One stack to rule them all  
         */
        if ( "org.codehaus.plexus.redback.ui.web.action.PasswordAction".equals( actionInvocation.getAction().getClass().getName() ) )
        {
            getLogger().debug( "Enforcement: skipping force password check on password action" );
            return false;
        }

        if ( "org.codehaus.plexus.redback.ui.web.action.LoginAction".equals( actionInvocation.getAction().getClass().getName() ) )
        {
            getLogger().debug( "Enforcement: skipping force password check on login action" );
            return false;
        }

        if ( config.getBoolean( "security.policy.strict.force.password.change.enabled" ) )
        {
            getLogger().debug( "Enforcement: checking active user password change enabled" );

            if ( securitySession.getUser().isPasswordChangeRequired() )
            {
                getLogger().info( "Enforcement: User must change password - forwarding to change password page." );

                return true;
            }
            else
            {
                getLogger().debug( "Enforcement: User doesn't need to change password." );                
            }
        }
        return false;
    }

}