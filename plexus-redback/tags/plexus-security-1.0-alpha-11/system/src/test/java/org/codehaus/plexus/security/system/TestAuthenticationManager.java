package org.codehaus.plexus.security.system;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.security.authentication.AuthenticationManager;
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

/**
 * TestAuthenticationManager:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 */
public class TestAuthenticationManager
    extends PlexusTestCase
{

    protected void setUp()
        throws Exception
    {
        super.setUp();
    }


   public void testAuthenticatorPopulation()
       throws Exception
   {
       AuthenticationManager authManager = (AuthenticationManager) lookup( AuthenticationManager.ROLE );

       assertEquals( 1, authManager.getAuthenticators().size() );
   }

}
