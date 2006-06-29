package org.codehaus.plexus.builder.runtime;

import org.codehaus.plexus.PlexusTestCase;

import java.io.File;
import java.util.Properties;

/**
 * @author Jason van Zyl
 */
public class ShellScriptRuntimeBooterGeneratorTest
    extends PlexusTestCase
{
    public void testGenerator()
        throws Exception
    {
        PlexusRuntimeBootloaderGenerator generator =
            (PlexusRuntimeBootloaderGenerator) lookup( PlexusRuntimeBootloaderGenerator.ROLE, "shellscript" );

        File outputDirectory = new File( System.getProperty( "basedir" ), "target/runtime" );

        Properties p = new Properties();

        generator.generate( outputDirectory, p );

        assertTrue( new File( outputDirectory, "plexus.sh" ).exists() );

        assertTrue( new File( outputDirectory, "plexus.bat" ).exists() );
    }
}
