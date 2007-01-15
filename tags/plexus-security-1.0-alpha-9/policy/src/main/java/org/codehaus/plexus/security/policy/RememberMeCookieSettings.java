package org.codehaus.plexus.security.policy;

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

import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

/**
 * RememberMeCookieSettings
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id: RememberMeCookieSettings.java 4565 2006-11-03 18:56:00Z joakime $
 * @plexus.component role="org.codehaus.plexus.security.policy.CookieSettings" role-hint="rememberMe"
 */
public class RememberMeCookieSettings
    extends AbstractCookieSettings
{
    private boolean enabled;

    private static final String PREFIX = "security.rememberme";

    public boolean isEnabled()
    {
        return enabled;
    }

    public void initialize()
        throws InitializationException
    {
        super.initialize();
        
        this.enabled = config.getBoolean( PREFIX + ".enabled", true );
    }

    protected String getConfigKeyPrefix()
    {
        return PREFIX;
    }
}
