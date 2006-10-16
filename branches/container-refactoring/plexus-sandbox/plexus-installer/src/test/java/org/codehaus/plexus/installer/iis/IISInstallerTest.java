package org.codehaus.plexus.installer.iis;

/*
 * Copyright 2005 The Apache Software Foundation.
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
import org.codehaus.plexus.installer.Installer;
import org.codehaus.plexus.installer.iis.IISInstaller;

/**
 * Test case for an IIS installer
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class IISInstallerTest
    extends PlexusTestCase
{
    /**
     * Create an installer
     *
     * @throws Exception
     */
    public void testCreateInstaller()
        throws Exception
    {
        IISInstaller installer = (IISInstaller) lookup( Installer.ROLE, "iis" );

        installer.setInstallerName( "myInstallerIis" );
        installer.setProductName( "myProduct" );
        installer.setProductVersion( "1.0" );
        installer.setProductCompany( "ASF" );
        installer.setProductURL( "http://codehaus.org" );

        installer.setDestFile( getTestFile( "target/myInstaller.iis" ) );

        installer.createArchive();
    }
}
