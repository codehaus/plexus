package org.codehaus.plexus.builder.runtime.platform;

import org.codehaus.plexus.builder.runtime.PlexusRuntimeBootloaderGeneratorException;

import java.io.File;
import java.util.Properties;

/**
 * Generator to create the Solaris x86 32 bit specific portions of the JSW booter.
 *
 * @author Andrew Williams
 * @version $Id$
 * @since 2.0-alpha-9
 */
public class SolarisX8632JswPlatformGenerator
    extends AbstractJswPlatformGenerator
{
    public static final String SOLARIS = "solaris-x86-32";

    public static final String SOLARIS_SOURCE = JSW + "/wrapper-solaris-x86-32-" + JSW_VERSION;

    public void generate( File binDirectory,
                          String resourceDir,
                          Properties configurationProperties )
        throws PlexusRuntimeBootloaderGeneratorException
    {
        // TODO: make it configurable - we don't always want a subdir
        File solarisBinDir = new File( binDirectory, SOLARIS );
        tools.mkdirs( solarisBinDir );

        File runSh = new File( solarisBinDir, "run.sh" );
        tools.filterCopy( tools.getResourceAsStream( JSW + "/wrapper-common-" + JSW_VERSION + "/src/bin/sh.script.in" ), runSh, configurationProperties );
        tools.executable( runSh );

        tools.copyResource( SOLARIS + "/wrapper", SOLARIS_SOURCE + "/bin/wrapper", true, binDirectory  );
        tools.copyResource( SOLARIS + "/libwrapper.so", SOLARIS_SOURCE + "/lib/libwrapper.so", false, binDirectory );

        Properties solarisProps = new Properties();
        solarisProps.setProperty( "library.path", "../../bin/" + SOLARIS );
        solarisProps.setProperty( "extra.path", "" );
        copyWrapperConf( solarisBinDir, configurationProperties, solarisProps );
    }
}
