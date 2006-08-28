package org.codehaus.plexus.workflow;

import org.codehaus.werkflow.DuplicateInstanceException;
import org.codehaus.werkflow.InitialContext;
import org.codehaus.werkflow.NoSuchInstanceException;
import org.codehaus.werkflow.NoSuchWorkflowException;
import org.codehaus.werkflow.Transaction;
import org.codehaus.werkflow.Workflow;
import org.codehaus.werkflow.spi.RobustInstance;

import java.util.Map;
import java.util.Properties;

/**
 * The Plexus Werkflow component.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 */
public interface WorkflowEngine
{
    String ROLE = WorkflowEngine.class.getName();

    Workflow getWorkflow( String id )
        throws NoSuchWorkflowException;

    Transaction beginTransaction( String workflowId, String instanceId, InitialContext context )
        throws NoSuchWorkflowException, DuplicateInstanceException,InterruptedException,Exception;

    Transaction beginTransaction( String instanceId )
        throws NoSuchInstanceException,InterruptedException,Exception;

    RobustInstance getInstance( String instanceId )
        throws NoSuchInstanceException,Exception;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    void startWorkflow( String workflowId, String instanceId, Map properties )
        throws NoSuchWorkflowException, DuplicateInstanceException,InterruptedException,Exception;
}
