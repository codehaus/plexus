package org.codehaus.plexus.summit;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
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
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class AbstractTest
    extends PlexusTestCase
{
    /**
     * Basedir for all i/o
     */
    public String basedir = System.getProperty("basedir");

    /**
     * Setup the test.
     */
    public void setUp() throws Exception
    {
        super.setUp();
        // Nothing done here yet.
    }
   
    /**
     * Tear down the test.
     */
    public void tearDown() throws Exception
    {
        super.tearDown();
        // Nothing to do here yet.
    }
    
    public void message(String message)
    {
        System.out.println("");
        System.out.println("-------------------------------------------------------------");
        System.out.println(message);
        System.out.println("-------------------------------------------------------------");
        System.out.println("");
    }        
}
