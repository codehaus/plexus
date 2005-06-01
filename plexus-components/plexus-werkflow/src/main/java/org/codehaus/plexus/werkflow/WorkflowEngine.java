package org.codehaus.plexus.werkflow;

import org.codehaus.werkflow.DuplicateInstanceException;
import org.codehaus.werkflow.InitialContext;
import org.codehaus.werkflow.NoSuchInstanceException;
import org.codehaus.werkflow.NoSuchWorkflowException;
import org.codehaus.werkflow.Transaction;
import org.codehaus.werkflow.Workflow;
import org.codehaus.werkflow.spi.RobustInstance;

import java.util.Map;

/**
 * The Plexus Werkflow component.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 */
public interface WorkflowEngine
{
    String ROLE = WorkflowEngine.class.getName();

    InitialContext createContext();

    Workflow getWorkflow( String id )
        throws NoSuchWorkflowException;

    Transaction beginTransaction( String workflowId, String instanceId, InitialContext context )
        throws NoSuchWorkflowException, DuplicateInstanceException,InterruptedException,Exception;

    Transaction beginTransaction( String instanceId )
        throws NoSuchInstanceException,InterruptedException,Exception;

    RobustInstance getInstance( String instanceId )
        throws NoSuchInstanceException,Exception;

    void stop();

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    void startWorkflow( String workflowId, String instanceId, Map parameters )
        throws NoSuchWorkflowException, DuplicateInstanceException,InterruptedException,Exception;
}
