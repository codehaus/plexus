package org.codehaus.plexus.maven.plugin.service;

import java.io.File;

import org.codehaus.plexus.maven.plugin.AbstractAppServerMojoTest;

/*
 * Copyright 2007 The Codehaus Foundation.
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
 * @author <a href="mailto:olamy@codehaus.org">olamy</a>
 * @since 5 mars 07
 * @version $Id$
 */
public class ServicesRuntimePopulatorMojoTest
    extends AbstractAppServerMojoTest
{
    public void testAddServices()
        throws Exception
    {
        ServicesRuntimePopulatorMojo mojo = (ServicesRuntimePopulatorMojo) getAppServerMojo(
                                                                                             "src/test/unit/runtime-populator/pom.xml",
                                                                                             "add-services" );
        mojo.execute();

        File servicesDirectory = getTestFile( "target/plexus-runtime/services" );
        assertTrue( "no services directory", servicesDirectory.exists() );
        assertEquals( "not only one file in services dir", 1, servicesDirectory.list().length );
        File appArtifact = new File( servicesDirectory, "plexus-appserver-service-jetty-2.0-alpha-8-SNAPSHOT.sar" );
        assertTrue( "plexus-appserver-service-jetty-2.0-alpha-8-SNAPSHOT.sar not in services directory", appArtifact
            .exists() );

    }
}
