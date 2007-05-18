package org.codehaus.plexus;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/*
 * Licensed to The Codehaus ( www.codehaus.org ) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The Codehaus licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * Test that our advertised components can be loaded.
 *
 * @author Andrew Williams
 * @version $Id$
 * @since 1.0-alpha-23
 */
public class ComponentsTest
    extends PlexusTestCase
{
    private ComponentLookupManager lookupManager;

    public void setUp()
        throws Exception
    {
        super.setUp();

        lookupManager = (ComponentLookupManager) lookup( ComponentLookupManager.ROLE );
    }

    public void testLookupManagerComponent()
        throws ComponentLookupException
    {
        assertNotNull( lookupManager );

        assertSame( lookupManager, lookupManager.lookup( ComponentLookupManager.ROLE ) );
    }
}
