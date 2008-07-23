package org.codehaus.plexus.spe.apps.server;

import org.codehaus.plexus.embed.Embedder;
import org.codehaus.plexus.spe.rmi.server.RemoteProcessEngineExposer;
import org.codehaus.plexus.spe.ProcessService;
import org.codehaus.plexus.spe.model.ProcessDescriptor;

import java.io.File;
import java.util.Collection;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ServerCli
{
    private final static Object lock = new Object();

    public static void main( String[] args )
        throws Exception
    {
        File basedir = new File( System.getProperty( "basedir" ) ).getAbsoluteFile();
        File plexusXml = new File( basedir, "etc/plexus.xml" );
        File processes = new File( basedir, "etc/processes" );

        Embedder embedder = new Embedder();
        embedder.setConfiguration( plexusXml.toURL() );
        embedder.addContextValue( "plexus.home", basedir.getAbsolutePath() );
        embedder.start();

        // -----------------------------------------------------------------------
        //
        // -----------------------------------------------------------------------

        ProcessService processService = (ProcessService) embedder.lookup( ProcessService.ROLE );

        System.out.println( "Loading descriptors from " + processes );
        Collection<ProcessDescriptor> descriptors = processService.loadProcessDirectory( processes );

        System.out.println( "Loaded " + descriptors.size() + " descriptors:" );
        for ( ProcessDescriptor descriptor : descriptors )
        {
            System.out.println( "o " + descriptor.getId() );
        }

        // -----------------------------------------------------------------------
        //
        // -----------------------------------------------------------------------

        RemoteProcessEngineExposer exposer = (RemoteProcessEngineExposer) embedder.lookup( RemoteProcessEngineExposer.ROLE );

        // -----------------------------------------------------------------------
        //
        // -----------------------------------------------------------------------

        Runtime.getRuntime().addShutdownHook( new Thread() {

            public void run()
            {
                synchronized( lock )
                {
                    lock.notifyAll();
                }
            }
        });

        synchronized( lock )
        {
            lock.wait();
        }

        // -----------------------------------------------------------------------
        //
        // -----------------------------------------------------------------------

        embedder.release( exposer );
        embedder.release( processService );
    }
}
