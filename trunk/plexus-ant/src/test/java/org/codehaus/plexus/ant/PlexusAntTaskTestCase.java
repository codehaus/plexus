package org.codehaus.plexus.ant;

import junit.framework.TestCase;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamPumper;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.codehaus.plexus.util.cli.DefaultConsumer;

import java.io.File;
import java.io.PrintWriter;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class PlexusAntTaskTestCase
    extends TestCase
{
    public void testWagonAntTask()
        throws Exception
    {
        Commandline cli = new Commandline();

        cli.setExecutable( "ant" );

        cli.setWorkingDirectory( new File( System.getProperty( "basedir" ), "src/test/resources/ant" ).getAbsolutePath() );

        Process p = cli.execute();

        StreamPumper errorPumper = new StreamPumper( p.getErrorStream(), new PrintWriter( System.err, true ) );

        new Thread( errorPumper ).start();

        StreamConsumer consumer = new DefaultConsumer();

        StreamPumper inputPumper = new StreamPumper( p.getInputStream(), consumer );

        errorPumper.start();

        inputPumper.start();

        int exitVal = p.waitFor();
    }
}
