package org.codehaus.plexus.examples.simple;

import java.io.FileReader;
import java.io.Reader;

import org.codehaus.plexus.embed.Embedder;

/** 
 * The main entry point into the "simple" application.  The class
 * demonstrates how to embed a Plexus container.
 * 
 * @author Pete Kazmier
 * @version $Revision$
 */
public class Main
{
    /** 
     * The main entry point for command line invocation.
     * 
     * @param args The command line arguments.
     * @exception Exception If there is a problem starting the Plexus
     * container or looking up a service.
     */
    public static void main( String[] args )
        throws Exception
    {
        // Setup, configure, and start Plexus
        Embedder embedder = new Embedder();
        Reader reader = new FileReader( "src/test/resources/plexus.xml" );
        embedder.setConfiguration( reader );
        embedder.start();

        // Use Plexus to lookup components
        HelloWorld component = (HelloWorld) embedder.lookup( HelloWorld.ROLE );

        // Use components obtained via Plexus
        System.out.println( component.sayHello() );

        // Clean things up by releasing the component
        embedder.release( component );

        // Finally, tell the Plexus container to shutdown
        embedder.stop();
    }
}
