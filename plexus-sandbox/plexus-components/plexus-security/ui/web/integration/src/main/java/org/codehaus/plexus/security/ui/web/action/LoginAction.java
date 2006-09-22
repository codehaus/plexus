package org.codehaus.plexus.security.ui.web.action;

/*
 * Copyright 2001-2006 The Codehaus.
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

import org.codehaus.plexus.security.authentication.PasswordBasedAuthenticationDataSource;
import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.util.StringUtils;

/**
 * LoginAction
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="com.opensymphony.xwork.Action"
 *                   role-hint="pss-login"
 *                   instantiation-strategy="per-lookup"
 */
public class LoginAction
    extends AbstractAuthenticationAction
{
    
    // ------------------------------------------------------------------
    // Plexus Component Requirements
    // ------------------------------------------------------------------

    /**
     * @plexus.requirement
     */
    protected SecuritySystem securitySystem;
    
    private String username;

    private String password;
    
    private boolean cancelButton;

    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------
    
    public String show()
    {
        return INPUT;
    }
    
    public String login()
    {
        if ( cancelButton )
        {
            return LOGIN_CANCEL;
        }
        
        if ( StringUtils.isEmpty(username)  )
        {
            addFieldError( "username", "Username cannot be empty." );
            return ERROR;
        }
        
        PasswordBasedAuthenticationDataSource authdatasource = new PasswordBasedAuthenticationDataSource();
        authdatasource.setPrincipal( username );
        authdatasource.setPassword( password );
        
        return webLogin( securitySystem, authdatasource );
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public boolean isCancelButton()
    {
        return cancelButton;
    }

    public void setCancelButton( boolean cancelButton )
    {
        this.cancelButton = cancelButton;
    }
}
