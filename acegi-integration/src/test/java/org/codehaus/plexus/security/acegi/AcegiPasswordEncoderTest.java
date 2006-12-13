package org.codehaus.plexus.security.acegi;

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

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.security.policy.PasswordEncoder;

/**
 * AcegiPasswordEncoderTest 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class AcegiPasswordEncoderTest
    extends PlexusTestCase
{
    public void testEncodePasswordTechniqueMatchesDefault()
        throws Exception
    {
        PasswordEncoder encoderAcegi = (PasswordEncoder) lookup( PasswordEncoder.ROLE, "acegi" );
        PasswordEncoder encoderDefault = (PasswordEncoder) lookup( PasswordEncoder.ROLE, "sha256" );

        String salt = "fletchLives";
        String rawPassword = "NoMoreSecrets";

        // Give both encoders the same system salt.
        encoderAcegi.setSystemSalt( salt );
        encoderDefault.setSystemSalt( salt );
        
        // Encode the same password on both encoders.
        String encodedByAcegi = encoderAcegi.encodePassword( rawPassword );
        String encodedByDefault = encoderDefault.encodePassword( rawPassword );
        
        // Verify that the encoders match.
        assertEquals( "Default SHA256 and Acegi should match", encodedByDefault, encodedByAcegi );
    }

}
