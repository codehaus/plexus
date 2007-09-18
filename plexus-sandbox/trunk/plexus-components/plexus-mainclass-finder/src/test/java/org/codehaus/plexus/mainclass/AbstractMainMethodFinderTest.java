package org.codehaus.plexus.mainclass;

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
import org.codehaus.plexus.PlexusTestCase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 */
public abstract class AbstractMainMethodFinderTest
    extends PlexusTestCase
{
    private MainClassFinder mainClassFinder;

    public void setMainClassFinder( MainClassFinder mainClassFinder )
    {
        this.mainClassFinder = mainClassFinder;
    }

    public void testMainClassFinder() {
        
        List classPath = new ArrayList();

        classPath.add( new File(getBasedir(), "src/test/resources/classpath"));
        classPath.add( new File(getBasedir(), "src/test/resources/jarfiles/myjar.jar"));

        List mainClasses = mainClassFinder.findMainClasses( classPath);

        assertEquals( 2, mainClasses.size());

        assertEquals( "org.codehaus.plexus.mainclass.MainClassInClassPath", mainClasses.get( 0));

        assertEquals( "org.codehaus.plexus.mainclass.MainClassInJar", mainClasses.get( 1));
        
    }
}
