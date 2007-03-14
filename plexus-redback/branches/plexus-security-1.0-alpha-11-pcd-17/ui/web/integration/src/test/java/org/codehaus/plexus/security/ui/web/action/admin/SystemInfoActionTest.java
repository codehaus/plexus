package org.codehaus.plexus.security.ui.web.action.admin;

/*
 * Copyright 2005-2006 The Codehaus.
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
 * SystemInfoActionTest
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class SystemInfoActionTest
    extends PlexusTestCase
{
    private SystemInfoAction systeminfo;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        systeminfo = (SystemInfoAction) lookup( "com.opensymphony.xwork.Action", "pss-sysinfo" );
    }

    public void testSystemInfoDump()
    {
        String result = systeminfo.show();
        assertNotNull( result );
        assertEquals( "success", result );
        assertNotNull( systeminfo.getDetails() );
    }
}
