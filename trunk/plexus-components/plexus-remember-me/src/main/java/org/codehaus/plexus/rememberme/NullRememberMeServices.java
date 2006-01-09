package org.codehaus.plexus.rememberme;

/* Copyright 2004, 2005 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.security.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Implementation of {@link RememberMeServices} that does nothing.
 * 
 * <p>
 * Used as a default by several framework classes.
 * </p>
 *
 * @author Ben Alex
 * @version $Id$
 */
public class NullRememberMeServices
    implements RememberMeServices
{
    //~ Methods ================================================================

    public Authentication autoLogin( HttpServletRequest request, HttpServletResponse response )
    {
        return null;
    }

    public void loginFail(HttpServletRequest request, HttpServletResponse response )
    {
    }

    public void loginSuccess(HttpServletRequest request,HttpServletResponse response,
        Authentication successfulAuthentication )
    {
    }
}
