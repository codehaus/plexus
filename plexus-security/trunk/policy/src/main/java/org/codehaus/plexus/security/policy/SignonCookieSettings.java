package org.codehaus.plexus.security.policy;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

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

/**
 * SignonCookieSettings
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="org.codehaus.plexus.security.policy.CookieSettings" role-hint="signon"
 */
public class SignonCookieSettings
    extends AbstractCookieSettings
{
    public void initialize()
        throws InitializationException
    {
        cookieTimeout = config.getInt( "security.signon.timeout" );
        domain = config.getString( "security.signon.domain" );
        path = config.getString( "security.signon.path" );
    }

    public boolean isEnabled()
    {
        return true;
    }
}
