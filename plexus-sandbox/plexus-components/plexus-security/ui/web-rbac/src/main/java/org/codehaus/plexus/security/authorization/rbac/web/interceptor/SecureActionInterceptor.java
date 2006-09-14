package org.codehaus.plexus.security.authorization.rbac.web;

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

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.security.authorization.rbac.web.interceptor.SecureAction;
import org.codehaus.plexus.security.authorization.AuthorizationResult;
import org.codehaus.plexus.security.system.SecuritySession;
import org.codehaus.plexus.security.system.SecuritySystem;

import java.util.List;
import java.util.Iterator;

/**
 * SecureActionInterceptor
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @author Jesse McConnell <jesse@codehaus.org>
 * @version $Id: SecureActionInterceptor.java 4035 2006-09-14 12:59:40Z joakime $
 * 
 * @plexus.component
 *   role="com.opensymphony.xwork.interceptor.Interceptor"
 *   role-hint="pssSecureActionInterceptor"
 */
public class SecureActionInterceptor
    extends AbstractLogEnabled
    implements Interceptor
{
    /**
     * @plexus.requirement
     */
    private SecuritySystem securitySystem;


    public void destroy()
    {

    }

    public void init()
    {
        getLogger().info( this.getClass().getName() + " initialized!" );
    }

    /**
     * process the action to determine if it implements SecureAction and then act
     * accordingly
     *
     * @param invocation
     * @return
     * @throws Exception
     */
    public String intercept( ActionInvocation invocation )
        throws Exception
    {
        ActionContext context = ActionContext.getContext();

        Action action = (Action) context.getActionInvocation().getAction();

        if ( action instanceof SecureAction )
        {
            SecureAction secureAction = (SecureAction) action;

            SecuritySession session = (SecuritySession) context.getSession().get( SecuritySession.ROLE );

            // check the authentication requirements
            if ( secureAction.authenticationRequired() )
            {
                if ( session == null || !session.getAuthenticationResult().isAuthenticated() )
                {
                    getLogger().debug( "not authenticated, need to authentication for this action" );
                    
                    return "requires-authentication";
                }
            }

            List opList = secureAction.getRequiredOperations();

            // if operations are returned we need to perform authorization checks
            if ( opList != null && opList.size() > 0 )
            {
                if ( session == null )
                {
                    getLogger().debug( "session required for authorization to run" );
                    return "requires-authentication";
                }
                else if ( secureAction.getRequiredResource() == null || "".equals( secureAction.getRequiredResource() ) )
                {
                    getLogger().warn( "***" );
                    getLogger().warn( "*** action is not configured correctly, required resource should return something revelant, or the global resource" );
                    getLogger().warn( "***" );
                    
                    return "requires-authorization";
                }

                for ( Iterator i = opList.iterator(); i.hasNext(); )
                {
                    String operation = (String) i.next();
                    AuthorizationResult authzResult =
                        securitySystem.authorize( session, operation, secureAction.getRequiredResource() );

                    if ( authzResult.isAuthorized() )
                    {
                        return invocation.invoke();
                    }
                }

                return "requires-authorization";
            }
        }
        getLogger().debug( "not a secure action " + action.getClass().getName() );
        return invocation.invoke();
    }
}
