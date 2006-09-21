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

import org.codehaus.plexus.security.authentication.TokenBasedAuthenticationDataSource;
import org.codehaus.plexus.security.keys.AuthenticationKey;
import org.codehaus.plexus.security.keys.KeyManager;
import org.codehaus.plexus.security.keys.KeyManagerException;
import org.codehaus.plexus.security.keys.KeyNotFoundException;
import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserNotFoundException;
import org.codehaus.plexus.util.StringUtils;

/**
 * ValidateAccountAction 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="com.opensymphony.xwork.Action"
 *                   role-hint="pss-validate-account"
 *                   instantiation-strategy="per-lookup"
 */
public class ValidateAccountAction
    extends LoginAction
{
    /**
     * @plexus.requirement
     */
    private KeyManager keyManager;

    /**
     * @plexus.requirement
     */
    protected SecuritySystem securitySystem;

    private String key;

    public String autologin()
    {
        if ( StringUtils.isEmpty( key ) )
        {
            addActionError( "Validation failure." );
            return ERROR;
        }

        try
        {
            AuthenticationKey authkey = keyManager.findKey( key );
            TokenBasedAuthenticationDataSource authsource = new TokenBasedAuthenticationDataSource();
            authsource.setToken( authkey.getKey() );

            User user = securitySystem.getUserManager().findUser( authkey.getForPrincipal() );
            
            user.setValidated( true );
            
            securitySystem.getUserManager().updateUser( user );
            
            return webLogin( securitySystem, authsource );
        }
        catch ( KeyNotFoundException e )
        {
            addActionError( "Unable to find the key." );
            return ERROR;
        }
        catch ( KeyManagerException e )
        {
            addActionError( "Unable to process key at this time.  Please try again later." );
            return ERROR;
        }
        catch ( UserNotFoundException e )
        {
            addActionError( "Unable to find user." );
            return ERROR;
        }
    }

    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }
}
