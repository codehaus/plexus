package org.codehaus.plexus.werkflow;

import org.codehaus.plexus.action.ActionManager;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.werkflow.Engine;
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
import org.codehaus.werkflow.spi.SatisfactionManager;
import org.codehaus.werkflow.spi.WorkflowManager;

import java.io.File;

/**
 * The default WerkflowComponent implementation.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 */
public class DefaultWerkflowComponent
    extends AbstractLogEnabled
    implements WerkflowComponent, Initializable
{
    private String werkflowDirectory;

    private Engine engine;

    private ActionManager manager;

    private ExpressionFactory expressionFactory;

    public Engine getEngine()
    {
        return engine;
    }

    public org.codehaus.werkflow.simple.ActionManager getActionManager()
    {
        // Be optimistic that our plexus ActionManager also implements
        // the necessary Werkflow ActionManager interface.
        return (org.codehaus.werkflow.simple.ActionManager) manager;
    }

    public ExpressionFactory getExpressionFactory()
    {
        return expressionFactory;
    }

    /**
     * @see org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable#initialize()
     */
    public void initialize() 
        throws InitializationException
    {
        engine = createEngine();

        expressionFactory = new OgnlExpressionFactory();

        try
        {
            loadWerkflows();
        }
        catch ( Exception e )
        {
            throw new InitializationException( "Cannot load workflows: ", e );
        }
    }

    protected Engine createEngine()
    {
        PersistenceManager pm = new SimplePersistenceManager();
        WorkflowManager wm = new SimpleWorkflowManager();
        SatisfactionManager sm = new SimpleSatisfactionManager();
        InstanceManager im = new SimpleInstanceManager();

        Engine engine = new Engine();
        engine.setPersistenceManager(pm);
        engine.setSatisfactionManager(sm);
        engine.setWorkflowManager(wm);
        engine.setInstanceManager(im);

        engine.start();

        return engine;
    }

    private void loadWerkflows() throws Exception
    {
        File werkflowDirFile = new File(werkflowDirectory);

        if (!werkflowDirFile.isDirectory())
        {
            getLogger().warn(
                    werkflowDirectory
                            + " is not a valid directory for werkflows.");
        }
        else
        {
            File[] werkflows = werkflowDirFile.listFiles();

            for (int i = 0; i < werkflows.length; i++)
            {
                if ( werkflows[i].getAbsolutePath().endsWith(".xml") )
                {
                    Workflow workflow = SimpleWorkflowReader.read(
                            getActionManager(), 
                            getExpressionFactory(),
                            werkflows[i]);
    
                    engine.getWorkflowManager().addWorkflow(workflow);
                }
            }
        }
    }
}
