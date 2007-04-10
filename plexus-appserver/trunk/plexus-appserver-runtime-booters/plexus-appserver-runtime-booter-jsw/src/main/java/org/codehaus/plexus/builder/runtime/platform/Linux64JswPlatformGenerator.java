package org.codehaus.plexus.builder.runtime.platform;

import org.codehaus.plexus.builder.runtime.PlexusRuntimeBootloaderGeneratorException;

import java.io.File;
import java.util.Properties;

/**
 * Generator to create the Linux 64 bit specific portions of the JSW booter.
 *
 * @author Andrew Williams
 * @version $Id$
 * @since 2.0-alpha-9
 */
public class Linux64JswPlatformGenerator
    extends AbstractJswPlatformGenerator
{
    public static final String LINUX = "linux-x86-64";

    public static final String LINUX_SOURCE = JSW + "/wrapper-linux-x86-64-" + JSW_VERSION;

    public void generate( File binDirectory,
                          String resourceDir,
                          Properties configurationProperties )
        throws PlexusRuntimeBootloaderGeneratorException
    {
        // TODO: make it configurable - we don't always want a subdir
        File linuxBinDir = new File( binDirectory, LINUX );
        tools.mkdirs( linuxBinDir );

        File runSh = new File( linuxBinDir, "run.sh" );
        tools.filterCopy( tools.getResourceAsStream( JSW + "/wrapper-common-" + JSW_VERSION + "/src/bin/sh.script.in" ), runSh, configurationProperties );
        tools.executable( runSh );

        tools.copyResource( LINUX + "/wrapper", LINUX_SOURCE + "/bin/wrapper", true, binDirectory  );
        tools.copyResource( LINUX + "/libwrapper.so", LINUX_SOURCE + "/lib/libwrapper.so", false, binDirectory );

        Properties linuxProps = new Properties();
        linuxProps.setProperty( "library.path", "../../bin/" + LINUX );
        linuxProps.setProperty( "extra.path", "" );
        copyWrapperConf( linuxBinDir, configurationProperties, linuxProps );
    }
}
