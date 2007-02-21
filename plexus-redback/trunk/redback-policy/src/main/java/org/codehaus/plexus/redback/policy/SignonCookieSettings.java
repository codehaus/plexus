package org.codehaus.plexus.redback.policy;

/*
 * Copyright 2006-2007 The Codehaus.
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

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

/**
 * SignonCookieSettings
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="org.codehaus.plexus.redback.policy.CookieSettings" role-hint="signon"
 */
public class SignonCookieSettings
    extends AbstractCookieSettings
    implements Initializable
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
