package org.codehaus.plexus.security.user.policy;

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

import org.codehaus.plexus.PlexusTestCase;

/**
 * PasswordEncoderTest 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class PasswordEncoderTest
    extends PlexusTestCase
{
    private static final String PASSWORD = "s3cret";
    private static final String ENCODED_SHA1 = "/vNB+F2HQ559kaLUZbmHHvZrXpg=";
    private static final String ENCODED_SHA256 = "HsHCa1DV08WNlYMYGvgHZlX+AHVr9yhZQLo2cPmfy6A=";
    
    public void testSHA1Encoding() throws Exception
    {
        PasswordEncoder encoder = (PasswordEncoder) lookup( PasswordEncoder.ROLE, "sha1" );
        
        assertNotNull(encoder);
        
        String encoded = encoder.encodePassword( PASSWORD );
        
        assertEquals( ENCODED_SHA1, encoded );
    }
    
    public void testSHA256Encoding() throws Exception
    {
        PasswordEncoder encoder = (PasswordEncoder) lookup( PasswordEncoder.ROLE, "sha256" );
        
        assertNotNull(encoder);
        
        String encoded = encoder.encodePassword( PASSWORD );
        
        assertEquals( ENCODED_SHA256, encoded );
    }
    
    public void testSHA1IsPasswordValid() throws Exception
    {
        PasswordEncoder encoder = (PasswordEncoder) lookup( PasswordEncoder.ROLE, "sha1" );
        
        assertNotNull(encoder);
        
        assertTrue( encoder.isPasswordValid( ENCODED_SHA1, PASSWORD ) );
    }
    
    public void testSHA256IsPasswordValid() throws Exception
    {
        PasswordEncoder encoder = (PasswordEncoder) lookup( PasswordEncoder.ROLE, "sha256" );
        
        assertNotNull(encoder);
        
        assertTrue( encoder.isPasswordValid( ENCODED_SHA256, PASSWORD ) );
    }
    
}
