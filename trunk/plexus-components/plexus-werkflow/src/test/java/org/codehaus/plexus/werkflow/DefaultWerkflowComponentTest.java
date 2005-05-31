package org.codehaus.plexus.werkflow;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.werkflow.Engine;
import org.codehaus.werkflow.InitialContext;
import org.codehaus.werkflow.Transaction;
import org.codehaus.werkflow.Workflow;
import org.codehaus.werkflow.spi.RobustInstance;
import org.codehaus.werkflow.spi.SatisfactionSpec;
import org.codehaus.werkflow.spi.DefaultSatisfactionValues;

import java.util.Arrays;

/**
 * DefaultWerkflowServiceTest
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 */
public class DefaultWerkflowComponentTest
    extends PlexusTestCase
{

    public void testWerkflow() throws Exception
    {
        WerkflowComponent s = (WerkflowComponent) lookup( WerkflowComponent.ROLE );

        Engine engine = s.getEngine();

        assertNotNull( engine );

        InitialContext c = new InitialContext();

        c.set( "true", Boolean.TRUE );

        c.set( "false", Boolean.FALSE );

        Workflow workflow = engine.getWorkflowManager().getWorkflow( "bloggie" );

        // new user action creating a new instance
        Transaction transaction = engine.beginTransaction( workflow.getId(), "instance1", c );

        RobustInstance instance = engine.getInstanceManager().getInstance( transaction.getInstanceId() );

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

        Transaction tx = engine.beginTransaction( instance.getId() );

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

        tx = engine.beginTransaction( instance.getId() );

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

        engine.stop();
    }
}
