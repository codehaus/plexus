package org.codehaus.plexus.webdav.jackrabbit;

/*
 * Copyright 2001-2007 The Codehaus.
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

import org.apache.jackrabbit.core.security.AnonymousPrincipal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

/**
 * JackrabbitLoginModule - Everyone is allowed in.
 * 
 * NOTE: We don't want security to be handled by the jackrabbit webdav repository security.
 * We'll use the external security mechanism instead. (likely Plexus-Redback)
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class JackrabbitLoginModule
    implements LoginModule
{
    private final Set principals = new HashSet();
    private Subject subject;

    public boolean abort()
        throws LoginException
    {
        return true;
    }

    public boolean commit()
        throws LoginException
    {
        if ( principals.isEmpty() )
        {
            return false;
        }
        else
        {
            // add a principals (authenticated identities) to the Subject
            subject.getPrincipals().addAll( principals );
            return true;
        }
    }

    public void initialize( Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options )
    {
        this.subject = subject;
    }

    public boolean login()
        throws LoginException
    {
        principals.clear();
        principals.add( new AnonymousPrincipal() );

        return true;
    }

    public boolean logout()
        throws LoginException
    {
        subject.getPrincipals().clear();
        principals.clear();

        return true;
    }
}
