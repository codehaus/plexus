package org.codehaus.plexus.workflow;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class NoSuchActionException extends WorkflowException {
    private String actionName;
    
	public NoSuchActionException(String actionName) {
        super("Missing action: '" + actionName + "'.");
		this.actionName = actionName;
    }
    
    public String getActionName() {
    	return actionName;
    }
}
