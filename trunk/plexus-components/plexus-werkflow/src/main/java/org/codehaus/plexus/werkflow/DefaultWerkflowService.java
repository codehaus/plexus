package org.codehaus.plexus.werkflow;

import java.io.File;

import org.codehaus.plexus.action.ActionManager;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.werkflow.ognl.OgnlExpressionFactory;
import org.codehaus.werkflow.AutomaticEngine;
import org.codehaus.werkflow.Engine;
import org.codehaus.werkflow.Workflow;
import org.codehaus.werkflow.nonpersistent.NonPersistentInstanceManager;
import org.codehaus.werkflow.simple.ExpressionFactory;
import org.codehaus.werkflow.simple.SimpleWorkflowReader;

/**
 * The default WerkflowService implementation.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class DefaultWerkflowService
    extends AbstractLogEnabled
    implements WerkflowService, Initializable
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
	public void initialize() throws Exception
	{
		engine = new AutomaticEngine(new NonPersistentInstanceManager());
        
        expressionFactory = new OgnlExpressionFactory();
        
        File werkflowDirFile = new File(werkflowDirectory);
        
        if ( !werkflowDirFile.isDirectory() )
        {
        	getLogger().warn(werkflowDirectory + " is not a valid directory for werkflows.");
        }
        else
        {
            File[] werkflows = werkflowDirFile.listFiles();
            
            for ( int i = 0; i < werkflows.length; i++ )
            {
                Workflow workflow = SimpleWorkflowReader.read(
                        getActionManager(), 
                        getExpressionFactory(), 
                        werkflows[i]);
                
                engine.addWorkflow(workflow);
            }
        }
	}
}
