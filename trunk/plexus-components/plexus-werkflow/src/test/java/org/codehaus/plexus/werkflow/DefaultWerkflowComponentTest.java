package org.codehaus.plexus.werkflow;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.werkflow.Engine;
import org.codehaus.werkflow.InitialContext;
import org.codehaus.werkflow.Transaction;
import org.codehaus.werkflow.Workflow;
import org.codehaus.werkflow.spi.RobustInstance;
import org.codehaus.werkflow.spi.SatisfactionSpec;
import org.codehaus.werkflow.spi.DefaultSatisfactionValues;
import org.codehaus.werkflow.spi.Instance;

import java.util.Arrays;

/**
 * DefaultWerkflowServiceTest
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 */
public class DefaultWerkflowComponentTest
    extends PlexusTestCase
{
    public void testWerkflow()
        throws Exception
    {
        WorkflowEngine workflowEngine = (WorkflowEngine) lookup( WorkflowEngine.ROLE );

        InitialContext context = workflowEngine.createContext();

        Workflow workflow = workflowEngine.getWorkflow( "bloggie" );

        Transaction transaction = workflowEngine.beginTransaction( workflow.getId(), "instance1", context );

        RobustInstance instance = workflowEngine.getInstance( transaction.getInstanceId() );

        transaction.commit();

        try
        {
            Thread.sleep( 100 );
        }
        catch ( InterruptedException ie )
        {
        }

        assertEquals( "instance1", instance.getId() );

        String state = (String) instance.get( "state" );

        assertNotNull( state );

        assertEquals( "evaluating", state );

        assertTrue( !instance.isComplete() );

        SatisfactionSpec[] eligibleSatisfacions = instance.getEligibleSatisfactions();

        assertEquals( 2, eligibleSatisfacions.length );

        Transaction tx = workflowEngine.beginTransaction( instance.getId() );

        // satisfy 1st choice
        DefaultSatisfactionValues sv = new DefaultSatisfactionValues();

        sv.setValue( "choice", "green" );

        tx.satisfy( "pick_color", sv );

        tx.commit();

        try
        {
            Thread.sleep( 100 );
        }
        catch ( InterruptedException ie )
        {
        }
        
        assertEquals( "green", instance.get( "pick_color.choice" ) );

        eligibleSatisfacions = instance.getEligibleSatisfactions();

        assertEquals( 1, eligibleSatisfacions.length );

        assertTrue( !instance.isComplete() );

        tx = workflowEngine.beginTransaction( instance.getId() );

        // satisfy 2nd choice
        sv = new DefaultSatisfactionValues();

        sv.setValue( "choice", "reject" );

        tx.satisfy( "approval", sv );

        tx.commit();

        try
        {
            Thread.sleep( 100 );
        }
        catch ( InterruptedException ie )
        {
        }

        assertEquals( "reject", instance.get( "approval.choice" ) );

        eligibleSatisfacions = instance.getEligibleSatisfactions();

        assertEquals( 0, eligibleSatisfacions.length );

        System.out.println( "state: " + instance.getState() );

        state = (String) instance.get( "state" );

        assertNotNull( state );

        assertEquals( "evaluated", state );

        assertTrue( instance.isComplete() );

        workflowEngine.stop();
    }
}
