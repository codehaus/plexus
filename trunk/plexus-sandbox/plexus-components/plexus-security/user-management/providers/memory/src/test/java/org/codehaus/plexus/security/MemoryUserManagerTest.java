package org.codehaus.plexus.security;

/*
 * Copyright 2005 The Codehaus.
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

import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.memory.MemoryUserManager;
import org.codehaus.plexus.security.user.provider.test.AbstractUserManagerTestCase;

/**
 * {@link MemoryUserManager} test:
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public class MemoryUserManagerTest
    extends AbstractUserManagerTestCase
{

    protected void setUp()
        throws Exception
    {
        super.setUp();

        setUserManager( (UserManager) lookup( UserManager.ROLE, "memory" ) );
    }
}
