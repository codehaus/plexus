/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.werkflow.continuum;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.werkflow.WorkflowEngine;
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
public class ContinuumWorkflowTest
    extends PlexusTestCase
{
    public void testWerkflow()
        throws Exception
    {
        WorkflowEngine workflowEngine = (WorkflowEngine) lookup( WorkflowEngine.ROLE );

        InitialContext context = workflowEngine.createContext();

        Workflow workflow = workflowEngine.getWorkflow( "continuum" );

        Transaction transaction = workflowEngine.beginTransaction( workflow.getId(), "instance", context );

        RobustInstance instance = workflowEngine.getInstance( transaction.getInstanceId() );

        transaction.commit();

        try
        {
            Thread.sleep( 100 );
        }
        catch ( InterruptedException ie )
        {
        }

        assertEquals( "instance", instance.getId() );

        assertEquals( "done", instance.get( "action-update-scm" ) );

        assertEquals( "done", instance.get( "action-update-metadata" ) );

        assertEquals( "done", instance.get( "action-build" ) );

        assertTrue( instance.isComplete() );

        workflowEngine.stop();
    }
}
