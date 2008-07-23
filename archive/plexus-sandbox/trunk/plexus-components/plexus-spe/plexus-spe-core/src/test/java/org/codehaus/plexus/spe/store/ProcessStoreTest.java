package org.codehaus.plexus.spe.store;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.spe.model.ProcessDescriptor;
import org.codehaus.plexus.spe.model.ProcessInstance;
import org.codehaus.plexus.spe.model.StepDescriptor;
import org.codehaus.plexus.spe.model.StepInstance;
import org.codehaus.plexus.spe.model.LogMessage;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ProcessStoreTest
    extends PlexusTestCase
{
    protected void setUp()
        throws Exception
    {
        super.setUp();

        LoggerManager loggerManager = (LoggerManager) lookup( LoggerManager.ROLE );
        loggerManager.setThreshold( Logger.LEVEL_DEBUG );

        ProcessInstanceStore store = (ProcessInstanceStore) lookup( ProcessInstanceStore.ROLE );

        for ( ProcessInstance instance : store.getInstances() )
        {
            store.deleteInstance( instance.getId() );
        }

        assertEquals( 0, store.getInstances().size() );
    }

    public void testSimple()
        throws Exception
    {
        ProcessInstanceStore store = (ProcessInstanceStore) lookup( ProcessInstanceStore.ROLE );

        Map<String, Serializable> context = new HashMap<String, Serializable>();

        ProcessDescriptor process = new ProcessDescriptor();
        process.setId( "hello-world" );
        StepDescriptor stepDescriptor = new StepDescriptor( "echo-message" );
        process.addStep( stepDescriptor );
        store.createInstance( process, context );
    }

    public void testBasic()
        throws Exception
    {
        ProcessInstanceStore store = (ProcessInstanceStore) lookup( ProcessInstanceStore.ROLE );

        Map<String, Serializable> context = new HashMap<String, Serializable>();

        // -----------------------------------------------------------------------
        // Create the process descriptor
        // -----------------------------------------------------------------------

        StepDescriptor stepDescriptor1 = new StepDescriptor( "echo-message" );

        StepDescriptor stepDescriptor2 = new StepDescriptor( "voice-message" );

        context.put( "message", "Hello World!" );
        context.put( "user", new User( "Trygve", 123 ) );

        ProcessDescriptor process = new ProcessDescriptor();
        process.setId( "hello-world" );
        process.addStep( stepDescriptor1 );
        process.addStep( stepDescriptor2 );

        // -----------------------------------------------------------------------
        // Create an instance of the process
        // -----------------------------------------------------------------------

        ProcessInstance instance = store.createInstance( process, context );

        assertEquals( instance.getProcessId(), process.getId() );
        assertEquals( 0, instance.getEndTime() );
        assertTrue( instance.getCreatedTime() > 0 );
        assertEquals( null, instance.getErrorMessage() );
        assertEquals( 2, instance.getSteps().size() );
        StepInstance step1 = instance.getSteps().get( 0 );
        assertEquals( "echo-message", step1.getExecutorId() );
        assertEquals( 0, step1.getLogMessages().size() );

        StepInstance step2 = instance.getSteps().get( 1 );
        assertEquals( "voice-message", step2.getExecutorId() );
        assertEquals( 0, step2.getLogMessages().size() );

        // -----------------------------------------------------------------------
        // Make sure there's exactly one active process
        // -----------------------------------------------------------------------

        Collection<? extends ProcessInstance> instances = store.getActiveInstances();

        assertNotNull( instances );

        assertEquals( 1, instances.size() );

        ProcessInstance actualProcess = instances.iterator().next();

        assertEquals( instance.getId(), actualProcess.getId() );
        assertEquals( instance.getProcessId(), actualProcess.getProcessId() );
        try
        {
            actualProcess.getContext();
            fail( "Expected Exception" );
        }
        catch ( Exception e )
        {
            // expected
        }

        // -----------------------------------------------------------------------
        // Try loading the process again directly, this time including the entire
        // objects
        // -----------------------------------------------------------------------

        actualProcess = store.getInstance( instance.getId(), true );

        assertNotNull( actualProcess );
        assertEquals( instance.getId(), actualProcess.getId() );
        assertEquals( instance.getProcessId(), actualProcess.getProcessId() );
        assertEquals( 2, actualProcess.getContext().size() );
        assertEquals( "Hello World!", actualProcess.getContext().get( "message" ) );
        assertNotNull( actualProcess.getContext().get( "user" ) );
        assertEquals( "Trygve", ((User)actualProcess.getContext().get( "user" )).getName() );
        assertEquals( 123, ((User)actualProcess.getContext().get( "user" )).getAge() );
        assertEquals( instance.getContext(), actualProcess.getContext() );

        // steps
        assertEquals( instance.getSteps().size(), actualProcess.getSteps().size() );
        StepInstance stepInstance1 = actualProcess.getSteps().get( 0 );
        assertEquals( 0, stepInstance1.getEndTime() );
        assertEquals( instance.getId(), stepInstance1.getProcessInstanceId() );
        assertEquals( 0, stepInstance1.getLogMessages().size() );

        StepInstance stepInstance2 = actualProcess.getSteps().get( 1 );
        assertEquals( 0, stepInstance2.getEndTime() );
        assertEquals( instance.getId(), stepInstance2.getProcessInstanceId() );
        assertEquals( 0, stepInstance2.getLogMessages().size() );

        // -----------------------------------------------------------------------
        // Change the context and save again
        // -----------------------------------------------------------------------

        actualProcess.putContext( "message", "yo yo!" );
        stepInstance1.addLogMessage( createLogMessage( "step 1, message 1" ) );
        stepInstance1.addLogMessage( createLogMessage( "step 1, message 2" ) );
        stepInstance1.addLogMessage( createLogMessage( "step 1, message 3" ) );
        stepInstance2.addLogMessage( createLogMessage( "step 2, message 1" ) );
        stepInstance2.addLogMessage( createLogMessage( "step 2, message 2" ) );

        store.saveInstance( actualProcess );

        actualProcess = store.getInstance( actualProcess.getId(), true );

        assertEquals( "yo yo!", actualProcess.getContext().get( "message" ) );

        stepInstance1 = actualProcess.getSteps().get( 0 );
        assertEquals( 3, stepInstance1.getLogMessages().size() );
        assertEquals( "step 1, message 1", stepInstance1.getLogMessages().get( 0 ).getMessage() );
        assertEquals( "step 1, message 2", stepInstance1.getLogMessages().get( 1 ).getMessage() );
        assertEquals( "step 1, message 3", stepInstance1.getLogMessages().get( 2 ).getMessage() );

        stepInstance2 = actualProcess.getSteps().get( 1 );
        assertEquals( 2, stepInstance2.getLogMessages().size() );
        assertEquals( "step 2, message 1", stepInstance2.getLogMessages().get( 0 ).getMessage() );
        assertEquals( "step 2, message 2", stepInstance2.getLogMessages().get( 1 ).getMessage() );
    }

    private LogMessage createLogMessage( String message )
    {
        LogMessage logMessage = new LogMessage();
        logMessage.setMessage( message );
        logMessage.setTimestamp( System.currentTimeMillis() );
        return logMessage;
    }

    private static class User
        implements Serializable
    {
        private String name;

        private int age;

        public User()
        {
        }

        public User( String name, int age )
        {
            this.name = name;
            this.age = age;
        }

        public String getName()
        {
            return name;
        }

        public void setName( String name )
        {
            this.name = name;
        }

        public int getAge()
        {
            return age;
        }

        public void setAge( int age )
        {
            this.age = age;
        }

        public boolean equals( Object o )
        {
            if ( this == o )
            {
                return true;
            }

            if ( o == null || getClass() != o.getClass() )
            {
                return false;
            }

            User user = (User) o;

            return name.equals( user.name );
        }

        public int hashCode()
        {
            return name.hashCode();
        }
    }
}
