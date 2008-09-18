package org.codehaus.plexus.tools.cli;

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
