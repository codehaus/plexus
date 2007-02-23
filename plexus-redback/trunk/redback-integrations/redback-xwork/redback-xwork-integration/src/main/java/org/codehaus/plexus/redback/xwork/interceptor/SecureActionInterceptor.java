package org.codehaus.plexus.redback.xwork.interceptor;

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

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.redback.authorization.AuthorizationResult;
import org.codehaus.plexus.redback.system.SecuritySession;
import org.codehaus.plexus.redback.system.SecuritySystem;
import org.codehaus.plexus.redback.system.SecuritySystemConstants;
import org.codehaus.plexus.xwork.interceptor.AbstractHttpRequestTrackerInterceptor;

import java.util.Iterator;
import java.util.List;

/**
 * SecureActionInterceptor: Interceptor that will detect webwork actions that implement the SecureAction
 * interface and providing they do verify that the current user is authorized to execute the action
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @author Jesse McConnell <jesse@codehaus.org>
 * @version $Id: SecureActionInterceptor.java 4035 2006-09-14 12:59:40Z joakime $
 * @plexus.component role="com.opensymphony.xwork.interceptor.Interceptor"
 * role-hint="pssSecureActionInterceptor"
 */
public class SecureActionInterceptor
    extends AbstractHttpRequestTrackerInterceptor
{
    private static final String REQUIRES_AUTHORIZATION = "requires-authorization";

    private static final String REQUIRES_AUTHENTICATION = "requires-authentication";

    /**
     * @plexus.requirement
     */
    private SecuritySystem securitySystem;

    /**
     * @plexus.configuration default-value="simple"
     */
    private String trackerName;
    
    public void destroy()
    {

    }

    public void init()
    {
        getLogger().info( this.getClass().getName() + " initialized!" );
    }

    protected String getTrackerName()
    {
        return trackerName;
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

        getLogger().debug( "SecureActionInterceptor: processing " + action.getClass().getName() );

        try
        {
            if ( action instanceof SecureAction )
            {
                SecureAction secureAction = (SecureAction) action;
                SecureActionBundle bundle = secureAction.getSecureActionBundle();

                if ( bundle == null )
                {
                    getLogger().error( "Null bundle detected." );

                    // TODO: send them somewhere else?
                    return invocation.invoke();
                }

                if ( bundle == SecureActionBundle.OPEN )
                {
                    getLogger().debug( "Bundle.OPEN detected." );

                    return invocation.invoke();
                }

                SecuritySession session =
                    (SecuritySession) context.getSession().get( SecuritySystemConstants.SECURITY_SESSION_KEY );

                // check the authentication requirements
                if ( bundle.requiresAuthentication() )
                {
                    if ( session == null || !session.isAuthenticated() )
                    {
                        getLogger().debug( "not authenticated, need to authenticate for this action" );
                        return processRequiresAuthentication( invocation );                        
                    }
                }

                List authzTuples = bundle.getAuthorizationTuples();

                // if operations are returned we need to perform authorization checks
                if ( authzTuples != null && authzTuples.size() > 0 )
                {
                    // authn adds a session, if there is no session they are not authorized and authn is required for
                    // authz, even if it is just a guest user
                    if ( session == null )
                    {
                        getLogger().debug( "session required for authorization to run" );
                        return processRequiresAuthentication( invocation );
                    }

                    for ( Iterator i = authzTuples.iterator(); i.hasNext(); )
                    {
                        SecureActionBundle.AuthorizationTuple tuple = (SecureActionBundle.AuthorizationTuple) i.next();

                        getLogger().debug( "checking authz for " + tuple.toString() );

                        AuthorizationResult authzResult =
                            securitySystem.authorize( session, tuple.getOperation(), tuple.getResource() );

                        getLogger().debug( "checking the interceptor authz " + authzResult.isAuthorized() + " for " +
                            tuple.toString() );

                        if ( authzResult.isAuthorized() )
                        {
                            getLogger().debug( session.getUser().getPrincipal() + " is authorized for action " +
                                secureAction.getClass().getName() + " by " + tuple.toString() );
                            return invocation.invoke();
                        }
                    }

                    return processRequiresAuthorization( invocation );
                }
            }
            else
            {
                getLogger().debug( "SecureActionInterceptor: " + action.getClass().getName() + " not a secure action" );
            }
        }
        catch ( SecureActionException se )
        {
            getLogger().error( "can't generate the SecureActionBundle, deny access: " + se.getMessage() );
            return processRequiresAuthentication( invocation );
        }

        getLogger().debug( "not a secure action " + action.getClass().getName() );
        String result = invocation.invoke();
        getLogger().debug( "Passing invocation up, result is [" + result + "] on call " +
            invocation.getAction().getClass().getName() );
        return result;
    }
    
    protected String processRequiresAuthorization( ActionInvocation invocation )
        throws ComponentLookupException
    {
        addActionInvocation( invocation ).setBackTrack();
        return REQUIRES_AUTHORIZATION;
    }
    
    protected String processRequiresAuthentication( ActionInvocation invocation )
        throws ComponentLookupException
    {
        addActionInvocation( invocation ).setBackTrack();
        return REQUIRES_AUTHENTICATION;
    }    
}
