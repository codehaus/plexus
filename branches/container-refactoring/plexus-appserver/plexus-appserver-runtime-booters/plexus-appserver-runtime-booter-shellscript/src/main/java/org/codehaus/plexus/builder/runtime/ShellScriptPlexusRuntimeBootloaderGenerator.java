package org.codehaus.plexus.builder.runtime;

import java.io.File;
import java.util.Properties;

/**
 * @author <a href="jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 */
public class ShellScriptPlexusRuntimeBootloaderGenerator
    extends AbstractPlexusRuntimeBootloaderGenerator
{
    private final static String UNIX_LAUNCHER_TEMPLATE = "shellscript/plexus.vm";

    private final static String WINDOWS_LAUNCHER_TEMPLATE = "shellscript/plexus-bat.vm";

    public void generate( File outputDirectory, Properties configurationProperties )
        throws PlexusRuntimeBootloaderGeneratorException
    {
        mkdirs( outputDirectory );

        mergeTemplate( ShellScriptPlexusRuntimeBootloaderGenerator.UNIX_LAUNCHER_TEMPLATE, new File( outputDirectory, "plexus.sh" ), false );

        mergeTemplate( ShellScriptPlexusRuntimeBootloaderGenerator.WINDOWS_LAUNCHER_TEMPLATE, new File( outputDirectory, "plexus.bat" ), true );

        executable( new File( outputDirectory, "plexus.sh" ) );
    }
}
