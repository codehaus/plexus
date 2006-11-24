package org.codehaus.plexus.security.policy.rules;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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
import org.codehaus.plexus.security.configuration.UserConfiguration;
import org.codehaus.plexus.security.policy.PasswordRule;

/**
 * AbstractPasswordRule
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public abstract class AbstractPasswordRule
    implements PasswordRule, Initializable
{
    protected static final String PASSWORD_RULE_CONFIGKEY = "security.policy.password.rule";

    protected boolean enabled = true;

    /**
     * @plexus.requirement
     */
    protected UserConfiguration config;

    public boolean isEnabled()
    {
        return enabled;
    }

    protected void configure( String configPrefix )
    {
        configure( config, configPrefix );
    }

    protected void configure( UserConfiguration config, String configPrefix )
    {
        enabled = config.getBoolean( configPrefix + ".enabled", true );
    }
}
