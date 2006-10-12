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

/**
 * DefaultUserValidationSettings 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.codehaus.plexus.security.policy.UserValidationSettings"
 */
public class DefaultUserValidationSettings
    implements UserValidationSettings
{
    /**
     * @plexus.configuration default-value="true"
     */
    private boolean emailValidationRequired;

    /**
     * Timeout (in minutes) for the key generated for an email validation to remain valid.
     * Default value is equivalent to 48 hours
     * 
     * @plexus.configuration default-value="2880"
     */
    private int emailValidationTimeout;

    /**
     * @plexus.configuration default-value="/security/login!login.action"
     */
    private String emailLoginPath;
    
    /**
     * @plexus.configuration default-value="Unconfigured Subject Line"
     */
    private String emailSubject;
    
    public boolean isEmailValidationRequired()
    {
        return emailValidationRequired;
    }

    public int getEmailValidationTimeout()
    {
        return emailValidationTimeout;
    }

    public String getEmailLoginPath()
    {
        return emailLoginPath;
    }

    public String getEmailSubject()
    {
        return emailSubject;
    }
}
