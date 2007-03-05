package org.codehaus.plexus.maven.plugin.application;

import java.io.File;

import org.codehaus.plexus.maven.plugin.AbstractAppServerMojoTest;
import org.codehaus.plexus.maven.plugin.application.ApplicationsRuntimePopulatorMojo;

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
 * @since 4 mars 07
 * @version $Id$
 */
public class ApplicationsRuntimePopulatorMojoTest
    extends AbstractAppServerMojoTest
{

    public void testAddApp()
        throws Exception
    {
        try
        {
            ApplicationsRuntimePopulatorMojo mojo = (ApplicationsRuntimePopulatorMojo) getAppServerMojo(
                                                                                                         "src/test/unit/runtime-populator/pom.xml",
                                                                                                         "add-apps" );
            mojo.execute();

            File appsDirectory = getTestFile( "target/plexus-runtime/apps" );
            assertTrue( "no apps directory", appsDirectory.exists() );
            assertEquals( "not only one file in apps dir", 1, appsDirectory.list().length );
            File appArtifact = new File( appsDirectory, "bar-1.0.jar" );
            assertTrue( "bar-1.0.jar not in apps directory", appArtifact.exists() );
        }
        catch ( Exception e )
        {
            //TODO remove
            e.printStackTrace();
            throw e;
        }

    }

}
