package org.codehaus.plexus.security.policy.encoders;

/*
 * Copyright 2006 The Apache Software Foundation.
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

import org.codehaus.plexus.security.policy.PasswordEncoder;

/**
 * SHA-256 Password Encoder.
 * 
 * @plexus.component role="org.codehaus.plexus.security.policy.PasswordEncoder" role-hint="sha256"
 * 
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class SHA256PasswordEncoder
    extends AbstractJAASPasswordEncoder
    implements PasswordEncoder
{
    public SHA256PasswordEncoder()
    {
        super( "SHA-256" );
    }
}
