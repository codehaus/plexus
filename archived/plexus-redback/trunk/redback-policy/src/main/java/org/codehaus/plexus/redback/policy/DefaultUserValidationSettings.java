package org.codehaus.plexus.redback.policy;

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

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.redback.configuration.UserConfiguration;

/**
 * DefaultUserValidationSettings
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="org.codehaus.plexus.redback.policy.UserValidationSettings"
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
        this.emailValidationRequired = config.getBoolean( "email.validation.required" );
        this.emailValidationTimeout = config.getInt( "email.validation.timeout" );
        this.emailSubject = config.getString( "email.validation.subject" );
    }
}
