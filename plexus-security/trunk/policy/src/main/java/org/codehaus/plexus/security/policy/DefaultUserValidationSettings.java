package org.codehaus.plexus.security.policy;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.security.configuration.UserConfiguration;

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

/**
 * DefaultUserValidationSettings 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.codehaus.plexus.security.policy.UserValidationSettings"
 */
public class DefaultUserValidationSettings
    implements UserValidationSettings, Initializable
{
    /**
     * @plexus.requirement
     */
    private UserConfiguration config;

    private boolean emailValidationRequired;

    private int emailValidationTimeout;

    private String emailSubject;

    public boolean isEmailValidationRequired()
    {
        return emailValidationRequired;
    }

    public int getEmailValidationTimeout()
    {
        return emailValidationTimeout;
    }

    public String getEmailSubject()
    {
        return emailSubject;
    }

    public void initialize()
        throws InitializationException
    {
        final String PREFIX = "email.validation";
        this.emailValidationRequired = config.getBoolean( PREFIX + ".required", true );
        this.emailValidationTimeout = config.getInt( PREFIX + ".timeout", 2880 );
        this.emailSubject = config.getString( PREFIX + ".subject", "Welcome to the unconfigured system." );
    }
}
