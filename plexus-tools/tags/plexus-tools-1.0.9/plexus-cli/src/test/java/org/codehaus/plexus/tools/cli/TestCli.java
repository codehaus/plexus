package org.codehaus.plexus.tools.cli;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.util.FileUtils;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public class TestCli
    extends AbstractCli
{
    public static void main( String[] args )
        throws Exception
    {
        new TestCli().execute( args );
    }

    public Options buildCliOptions( Options options )
    {
        options.addOption(
            OptionBuilder.withLongOpt( "name" ).withDescription( "Display name." ).hasArg().create( 'n' ) );

        return options;
    }

    public void invokePlexusComponent( CommandLine cli,
                                       PlexusContainer container )
        throws Exception
    {
        if ( cli.hasOption( 'n' ) )
        {
            String directory = cli.getOptionValue( 'n' );

            FileUtils.mkdir( new File( directory, "target" ).getAbsolutePath() );

            FileUtils.fileWrite( new File( directory, "target/cli.txt").getAbsolutePath() , "NAME_OPTION_INVOKED" );
        }
    }
}
