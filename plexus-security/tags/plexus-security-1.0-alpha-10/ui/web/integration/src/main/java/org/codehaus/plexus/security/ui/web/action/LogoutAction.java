package org.codehaus.plexus.security.ui.web.action;

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

import com.opensymphony.webwork.dispatcher.SessionMap;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionBundle;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionException;
import org.codehaus.plexus.security.ui.web.util.AutoLoginCookies;

/**
 * LogoutAction
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="com.opensymphony.xwork.Action"
 * role-hint="pss-logout"
 * instantiation-strategy="per-lookup"
 */
public class LogoutAction
    extends AbstractSecurityAction
{
    // Result Names.
    private static final String LOGOUT = "security-logout";

    /**
     * @plexus.requirement
     */
    private AutoLoginCookies autologinCookies;

    public String logout()
    {
        autologinCookies.removeRememberMeCookie();
        autologinCookies.removeSignonCookie();

        setAuthTokens( null );

        if ( session != null )
        {
            ( (SessionMap) session ).invalidate();
        }

        return LOGOUT;
    }

    public SecureActionBundle initSecureActionBundle()
        throws SecureActionException
    {
        return SecureActionBundle.OPEN;
    }
}
