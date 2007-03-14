package org.codehaus.plexus.security.user.cached;

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

import net.sf.ehcache.CacheManager;

import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.provider.test.AbstractUserManagerTestCase;

/**
 * CachedUserManagerTest 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class CachedUserManagerTest
    extends AbstractUserManagerTestCase
{
    protected void setUp()
        throws Exception
    {
        super.setUp();

        setUserManager( (UserManager) lookup( UserManager.ROLE, "cached" ) );

        assertTrue( getUserManager() instanceof CachedUserManager );
    }

    protected void tearDown()
        throws Exception
    {
        CacheManager.getInstance().removalAll();
        super.tearDown();
    }
}
