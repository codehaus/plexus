package org.codehaus.plexus.redback.rbac.memory;

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

import org.codehaus.plexus.redback.rbac.RBACManager;
import org.codehaus.plexus.redback.rbac.memory.MemoryRbacManager;
import org.codehaus.plexus.redback.tests.AbstractRbacManagerTestCase;

/**
 * MemoryRbacManagerTest 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id: MemoryRbacManagerTest.java 5883 2007-02-21 22:46:14Z joakime $
 */
public class MemoryRbacManagerTest
    extends AbstractRbacManagerTestCase
{
    /**
     * Creates a new RbacStore which contains no data.
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();
        
        setRbacManager( (MemoryRbacManager) lookup( RBACManager.ROLE, "memory" ) );
    }
}
