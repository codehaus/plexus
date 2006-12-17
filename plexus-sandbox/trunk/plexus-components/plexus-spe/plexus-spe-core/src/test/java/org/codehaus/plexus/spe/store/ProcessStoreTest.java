package org.codehaus.plexus.spe.store;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.spe.model.ProcessDescriptor;
import org.codehaus.plexus.spe.model.ProcessInstance;
import org.codehaus.plexus.spe.model.StepDescriptor;
import org.codehaus.plexus.spe.model.StepInstance;

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

        for ( ProcessInstance instance : store.getActiveInstances() )
        {
            store.deleteInstance( instance.getId() );
        }

        assertEquals( 0, store.getActiveInstances().size() );
    }

    public void testSimple()
        throws Exception
    {
        ProcessInstanceStore store = (ProcessInstanceStore) lookup( ProcessInstanceStore.ROLE );

        Map<String, Serializable> context = new HashMap<String, Serializable>();

        ProcessDescriptor process = new ProcessDescriptor();
        process.setId( "hello-world" );
        StepDescriptor stepDescriptor = new StepDescriptor();
        stepDescriptor.setExecutorId( "echo-message" );
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

        ProcessDescriptor process = new ProcessDescriptor();
        process.setId( "hello-world" );
        StepDescriptor stepDescriptor = new StepDescriptor();
        stepDescriptor.setExecutorId( "echo-message" );
        process.addStep( stepDescriptor );

        context.put( "message", "Hello World!" );
        context.put( "user", new User( "Trygve", 123 ) );

        // -----------------------------------------------------------------------
        // Create an instance of the process
        // -----------------------------------------------------------------------

        ProcessInstance instance = store.createInstance( process, context );

        assertEquals( instance.getProcessId(), process.getId() );
        assertEquals( 0, instance.getEndTime() );
        assertTrue( instance.getCreatedTime() > 0 );
        assertEquals( null, instance.getErrorMessage() );
        assertEquals( 1, instance.getSteps().size() );
        StepInstance step = (StepInstance) instance.getSteps().get( 0 );
        assertEquals( "echo-message", step.getExecutorId() );

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
            fail( "Expected JDODetachedFieldAccessException" );
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

        // -----------------------------------------------------------------------
        // Change the context and save again
        // -----------------------------------------------------------------------

        actualProcess.getContext().put( "message", "yo yo!" );

        store.saveInstance( actualProcess );

        actualProcess = store.getInstance( actualProcess.getId(), true );

        assertEquals( "yo yo!", actualProcess.getContext().get( "message" ) );
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
