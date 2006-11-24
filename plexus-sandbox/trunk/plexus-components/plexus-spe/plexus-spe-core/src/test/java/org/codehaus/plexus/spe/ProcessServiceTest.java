package org.codehaus.plexus.spe;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.spe.action.EchoAction;
import org.codehaus.plexus.spe.model.ProcessInstance;

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

        processService.loadProcess( getTestFile( "src/test/resources/process/process-1.xml" ).toURL() );

        Map<String, Serializable> context = new HashMap<String, Serializable>();
        context.put( "message", "Context Hello World!" );
        int instanceId = processService.executeProcess( "hello-world", context );

        Thread.sleep( 1000 );

        assertTrue( processService.hasCompleted( instanceId ) );

        assertEquals( 2, EchoAction.messages.size() );
        assertEquals( "Configuration Hello World!", EchoAction.messages.get( 0 ) );
        assertEquals( "Context Hello World!", EchoAction.messages.get( 1 ) );
    }

    public void testContextModifyingProcess()
        throws Exception
    {
        ProcessService processService = (ProcessService) lookup( ProcessService.ROLE );

        processService.loadProcess( getTestFile( "src/test/resources/process/process-2.xml" ).toURL() );

        Map<String, Serializable> context = new HashMap<String, Serializable>();
        context.put( "message", "Hello World!" );
        int instanceId = processService.executeProcess( "context-modifying-process", context );

        waitForCompletion( 3000, processService, instanceId );
        assertEquals( 2, EchoAction.messages.size() );
        assertEquals( "Hello World!", EchoAction.messages.get( 0 ) );
        assertEquals( "Modified Hello World!", EchoAction.messages.get( 1 ) );
    }

    public void testExceptionHandling()
        throws Exception
    {
        ProcessService processService = (ProcessService) lookup( ProcessService.ROLE );

        processService.loadProcess(
            getTestFile( "src/test/resources/process/exception-throwing-process.xml" ).toURL() );
        processService.loadProcess(
            getTestFile( "src/test/resources/process/runtime-exception-throwing-process.xml" ).toURL() );

        // ----------------------------------------------------------------------
        // Test handling of runtime exceptions.
        // ----------------------------------------------------------------------

        Map<String, Serializable> context = new HashMap<String, Serializable>();
        context.put( "message", "Runtime Exception Message" );
        int instanceId = processService.executeProcess( "runtime-exception-throwing-process", context );

        waitForCompletion( 3000, processService, instanceId, false );

        ProcessInstance processInstance = processService.getProcessInstance( instanceId );

        assertTrue( processInstance.getErrorMessage().contains( "Runtime Exception Message" ) );

        // ----------------------------------------------------------------------
        // Test handling of "normal" exceptions
        // ----------------------------------------------------------------------

        context = new HashMap<String, Serializable>();
        context.put( "message", "Non-Runtime Exception Message" );
        instanceId = processService.executeProcess( "exception-throwing-process", context );

        waitForCompletion( 3000, processService, instanceId, false );

        processInstance = processService.getProcessInstance( instanceId );

        assertTrue( processInstance.getErrorMessage().contains( "Non-Runtime Exception Message" ) );
    }

    public void testAntBasedTest()
        throws Exception
    {
        ProcessService processService = (ProcessService) lookup( ProcessService.ROLE );

        processService.loadProcess( getTestFile( "src/test/resources/process/process-3.xml" ).toURL() );

        int instanceId = processService.executeProcess( "ant-based-process", new HashMap<String, Serializable>() );

        waitForCompletion( 3000, processService, instanceId );
    }

    public void testProcessWithObjectsInTheContext()
        throws Exception
    {
        ProcessService processService = (ProcessService) lookup( ProcessService.ROLE );

        processService.loadProcess( getTestFile( "src/test/resources/process/process-4.xml" ).toURL() );

        HashMap<String, Serializable> context = new HashMap<String, Serializable>();

        User user = new User();
        user.setUsername( "trygvis" );
        user.setFirstName( "Trygve" );
        user.setLastName( "Laugstol" );

        context.put( "user", user );
        context.put( "username", "foo" );

        int instanceId = processService.executeProcess( "process-4", context );

        waitForCompletion( 3000, processService, instanceId );

        // TODO: Asserts
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void waitForCompletion( long time, ProcessService processService, int instanceId )
        throws InterruptedException, ProcessException
    {
        waitForCompletion( time, processService, instanceId, true );
    }

    private void waitForCompletion( long time, ProcessService processService, int instanceId, boolean expectsSuccess )
        throws InterruptedException, ProcessException
    {
        int sleepTime = 100;

        while ( time > 0 )
        {
            if ( processService.hasCompleted( instanceId ) )
            {
                ProcessInstance processInstance = processService.getProcessInstance( instanceId );

                if ( expectsSuccess )
                {
                    if ( processInstance.getErrorMessage() != null )
                    {
                        System.out.println( "----------------------" );
                        System.out.println( "Process throwable:" );
                        System.out.println( processInstance.getErrorMessage() );
                        System.out.println( "----------------------" );

                        fail( "The process completed non-successfully" );
                    }
                }
                else
                {
                    assertNotNull( processInstance.getErrorMessage() );
                }

                break;
            }

            Thread.sleep( sleepTime );
            time -= sleepTime;
        }
    }
}
