package org.codehaus.plexus.tools.cli;

/*
 * Copyright 2006 The Codehaus Foundation.
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
import org.codehaus.plexus.util.FileUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.io.File;

/**
 * @author Jason van Zyl
 */
public class CliTest
    extends PlexusTestCase
{
    public void testCli()
        throws Exception
    {
        String[] args = new String[]{"-n", getBasedir()};

        Class clazz = TestCli.class;

        Method m = clazz.getMethod( "main", new Class[]{String[].class} );

        int modifiers = m.getModifiers();

        if ( Modifier.isStatic( modifiers ) && Modifier.isPublic( modifiers ) )
        {
            if ( m.getReturnType() == Integer.TYPE || m.getReturnType() == Void.TYPE )
            {
                m.invoke( clazz, new Object[]{args} );
            }
        }

        String s = FileUtils.fileRead( new File( getBasedir(), "target/cli.txt" ) );

        assertEquals( "NAME_OPTION_INVOKED", s );
    }
}
