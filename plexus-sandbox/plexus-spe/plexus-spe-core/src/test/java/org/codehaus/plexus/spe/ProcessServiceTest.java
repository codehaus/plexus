package org.codehaus.plexus.spe;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.spe.action.EchoAction;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ProcessServiceTest
    extends PlexusTestCase
{
    protected void setUp()
        throws Exception
    {
        super.setUp();
        EchoAction.messages.clear();
    }

    public void testHelloWorldProcess()
        throws Exception
    {
        ProcessService processService = (ProcessService) lookup( ProcessService.ROLE );

        processService.loadProcess( getTestFile( "src/test/resources/process/process-1.xml").toURL() );

        Map<String, Serializable> context = new HashMap<String, Serializable>();
        context.put( "message", "Context Hello World!" );
        int processId = processService.executeProcess( "hello-world", context );

        Thread.sleep( 1000 );

        assertTrue( processService.hasCompleted( processId ));

        assertEquals( 2, EchoAction.messages.size() );
        assertEquals( "Configuration Hello World!", EchoAction.messages.get( 0 ) );
        assertEquals( "Context Hello World!", EchoAction.messages.get( 1 ) );
    }

    public void testContextModifyingProcess()
        throws Exception
    {
        ProcessService processService = (ProcessService) lookup( ProcessService.ROLE );

        processService.loadProcess( getTestFile( "src/test/resources/process/process-2.xml").toURL() );

        Map<String, Serializable> context = new HashMap<String, Serializable>();
        context.put( "message", "Hello World!" );
        int processId = processService.executeProcess( "context-modifying-process", context );

        waitForCompletion( 3000, processService, processId );
        assertEquals( 2, EchoAction.messages.size() );
        assertEquals( "Hello World!", EchoAction.messages.get( 0 ) );
        assertEquals( "Modified Hello World!", EchoAction.messages.get( 1 ) );
    }

    public void testAntBasedTest()
        throws Exception
    {
        ProcessService processService = (ProcessService) lookup( ProcessService.ROLE );

        processService.loadProcess( getTestFile( "src/test/resources/process/process-3.xml").toURL() );

        int processId = processService.executeProcess( "ant-based-process", new HashMap<String, Serializable>() );

        waitForCompletion( 3000, processService, processId );

        // TODO: Asserts
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void waitForCompletion( long time, ProcessService processService, int processId )
        throws InterruptedException, ProcessException
    {
        int sleepTime = 100;

        while ( time > 0 )
        {
            if ( processService.hasCompleted( processId ) )
            {
                break;
            }

            Thread.sleep( sleepTime );
            time -= sleepTime;
        }
    }
}
