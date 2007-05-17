package org.codehaus.plexus.redback.taglib.jsp;

/*
 * Copyright 2006 The Codehaus.
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
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.redback.authorization.AuthorizationException;
import org.codehaus.plexus.redback.system.SecuritySession;
import org.codehaus.plexus.redback.system.SecuritySystem;
import org.codehaus.plexus.redback.system.SecuritySystemConstants;
import org.codehaus.plexus.xwork.PlexusLifecycleListener;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

/**
 * IfAuthorizedTag:
 *
 * @author Jesse McConnell <jesse@codehaus.org>
 * @version $Id$
 */
public class IfAuthorizedTag
    extends ConditionalTagSupport
{
    private String permission;

    private String resource;

    public void setPermission( String permission )
    {
        this.permission = permission;
    }

    public void setResource( String resource )
    {
        this.resource = resource;
    }

    protected boolean condition()
        throws JspTagException
    {

        Boolean authzStatusBool = (Boolean) pageContext.getAttribute( "redbackCache" + permission + resource );
        boolean authzStatus;

        //long execTime = System.currentTimeMillis();
        
        if ( authzStatusBool == null )
        {
            ActionContext context = ActionContext.getContext();

            PlexusContainer container = (PlexusContainer) context.getApplication().get( PlexusLifecycleListener.KEY );
            SecuritySession securitySession =
                (SecuritySession) context.getSession().get( SecuritySystemConstants.SECURITY_SESSION_KEY );

            try
            {
                SecuritySystem securitySystem = (SecuritySystem) container.lookup( SecuritySystem.ROLE );

                authzStatus = securitySystem.isAuthorized( securitySession, permission, resource );
                pageContext.setAttribute( "redbackCache" + permission + resource, Boolean.valueOf( authzStatus ) );
            }
            catch ( ComponentLookupException cle )
            {
                throw new JspTagException( "unable to locate security system", cle );
            }
            catch ( AuthorizationException ae )
            {
                throw new JspTagException( "error with authorization", ae );
            }
            
            //System.out.println( "[PERF] " + "redbackCache" + permission + resource + " Time: " + (System.currentTimeMillis() - execTime) ); 
        }
        else
        {
            authzStatus = authzStatusBool.booleanValue();
            //System.out.println( "[PERF][Cached] " + "redbackCache" + permission + resource + " Time: " + (System.currentTimeMillis() - execTime) ); 
        }

        pageContext.setAttribute( "ifAuthorizedTag", Boolean.valueOf( authzStatus ) );
        return authzStatus;
    }
}
