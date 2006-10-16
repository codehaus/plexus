package org.codehaus.plexus.workflow;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.werkflow.DuplicateInstanceException;
import org.codehaus.werkflow.Engine;
import org.codehaus.werkflow.InitialContext;
import org.codehaus.werkflow.NoSuchInstanceException;
import org.codehaus.werkflow.NoSuchWorkflowException;
import org.codehaus.werkflow.Transaction;
import org.codehaus.werkflow.Workflow;
import org.codehaus.werkflow.helpers.SimpleInstanceManager;
import org.codehaus.werkflow.helpers.SimplePersistenceManager;
import org.codehaus.werkflow.helpers.SimpleSatisfactionManager;
import org.codehaus.werkflow.helpers.SimpleWorkflowManager;
import org.codehaus.werkflow.simple.ExpressionFactory;
import org.codehaus.werkflow.simple.SimpleWorkflowReader;
import org.codehaus.werkflow.simple.ognl.OgnlExpressionFactory;
import org.codehaus.werkflow.spi.InstanceManager;
import org.codehaus.werkflow.spi.PersistenceManager;
import org.codehaus.werkflow.spi.RobustInstance;
import org.codehaus.werkflow.spi.SatisfactionManager;
import org.codehaus.werkflow.spi.WorkflowManager;

import java.io.File;
import java.util.Map;

/**
 * The default WerkflowComponent implementation.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 */
public class DefaultWorkflowEngine
    extends AbstractLogEnabled
    implements WorkflowEngine, Initializable, Startable
{
    // ----------------------------------------------------------------------
    // Requirements
    // ----------------------------------------------------------------------

    private WorkflowActionManager actionManager;

    private WorkflowErrorHandler errorHandler;

    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    private String workflowDirectory;

    // ----------------------------------------------------------------------
    // Fields
    // ----------------------------------------------------------------------

    private Engine engine;

    private ExpressionFactory expressionFactory;

    // ----------------------------------------------------------------------
    // Implementation
    // ----------------------------------------------------------------------

    public Workflow getWorkflow( String id )
        throws NoSuchWorkflowException
    {
        return engine.getWorkflowManager().getWorkflow( id );
    }

    public void startWorkflow( String workflowId, String instanceId, Map properties )
        throws NoSuchWorkflowException, DuplicateInstanceException,InterruptedException,Exception
    {
        InitialContext context = createContext( properties );

        Transaction transaction = beginTransaction( workflowId, instanceId, context );

        transaction.commit();
    }

    public Transaction beginTransaction( String workflowId, String instanceId, InitialContext context )
        throws NoSuchWorkflowException, DuplicateInstanceException,InterruptedException,Exception
    {
        return engine.beginTransaction( workflowId, instanceId, context );
    }

    public Transaction beginTransaction( String instanceId )
        throws NoSuchInstanceException,InterruptedException,Exception
    {
        return engine.beginTransaction( instanceId );
    }

    public RobustInstance getInstance( String instanceId )
        throws NoSuchInstanceException,Exception
    {
        return engine.getInstanceManager().getInstance( instanceId );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public ExpressionFactory getExpressionFactory()
    {
        return expressionFactory;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected InitialContext createContext( Map parameters )
    {
        InitialContext context = new InitialContext( parameters );

        context.set( "true", Boolean.TRUE );

        context.set( "false", Boolean.FALSE );

        return context;
    }

    private void loadWerkflows()
        throws Exception
    {
        File werkflowDirFile = new File( workflowDirectory );

        if ( !werkflowDirFile.isDirectory() )
        {
            getLogger().warn( workflowDirectory + " is not a valid directory for werkflows." );
        }
        else
        {
            File[] werkflows = werkflowDirFile.listFiles();

            for ( int i = 0; i < werkflows.length; i++ )
            {
                if ( werkflows[i].getAbsolutePath().endsWith( ".xml" ) )
                {
                    Workflow workflow = SimpleWorkflowReader.read( actionManager,
                                                                   expressionFactory,
                                                                   werkflows[i] );

                    engine.getWorkflowManager().addWorkflow( workflow );

                    getLogger().info( "Loaded workflow: " + werkflows[i] );
                }
            }
        }
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    /**
     * @see org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable#initialize()
     */
    public void initialize()
        throws InitializationException
    {
        PersistenceManager pm = new SimplePersistenceManager();

        WorkflowManager wm = new SimpleWorkflowManager();

        SatisfactionManager sm = new SimpleSatisfactionManager();

        InstanceManager im = new SimpleInstanceManager();

        engine = new Engine();

        engine.setPersistenceManager( pm );

        engine.setSatisfactionManager( sm );

        engine.setWorkflowManager( wm );

        engine.setInstanceManager( im );

        engine.setErrorHandler( errorHandler );

        expressionFactory = new OgnlExpressionFactory();

    }

    public void start()
        throws StartingException
    {
        engine.start();

        try
        {
            loadWerkflows();
        }
        catch ( Exception e )
        {
            throw new StartingException( "Cannot load workflows: ", e );
        }
    }

    public void stop()
        throws StoppingException
    {
        engine.stop();
    }
}
