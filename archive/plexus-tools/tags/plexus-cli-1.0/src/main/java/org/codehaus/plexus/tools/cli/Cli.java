package org.codehaus.plexus.tools.cli;

import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.PlexusContainer;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;

/**
 * @author Jason van Zyl
 */
public interface Cli
{
    Options buildCliOptions( Options options );

    void invokePlexusComponent( CommandLine cli,
                                PlexusContainer container )
        throws Exception;

    // this can be calculated
    String getPomPropertiesPath();
}
