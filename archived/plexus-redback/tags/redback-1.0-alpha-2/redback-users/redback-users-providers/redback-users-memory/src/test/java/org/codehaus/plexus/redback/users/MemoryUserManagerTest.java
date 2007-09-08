package org.codehaus.plexus.redback.users;

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

import org.codehaus.plexus.redback.users.UserManager;
import org.codehaus.plexus.redback.users.provider.test.AbstractUserManagerTestCase;
import org.codehaus.plexus.redback.users.memory.MemoryUserManager;

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
