package org.codehaus.plexus.workflow;

/*
 * LICENSE
 */

import java.util.Map;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultWorkflowActionManager
    extends AbstractLogEnabled
    implements WorkflowActionManager {

    /** */
    private Map actions;

    public boolean hasAction(String name) {
        return actions.containsKey(name);
    }

    public void execute(Map caseAttributes, Map otherAttributes)
        throws WorkflowException {
        String name;
        WorkflowAction action;

        getLogger().debug("DefaultWorkflowActionManager");
        getLogger().debug("caseAttributes: " + caseAttributes);
        getLogger().debug("otherAttributes: " + otherAttributes);

        name = (String)otherAttributes.get("id");
        if(name == null)
            throw new WorkflowException("Missing required attribute: 'id'.");

        getLogger().debug("Executing " + name);
        action = (WorkflowAction)actions.get(name);

        if(action == null)
            throw new NoSuchActionException(name);

        try {
            action.execute(caseAttributes, otherAttributes);
        }
        catch(Throwable ex) {
            throw new WorkflowException("Exception while executing action.", ex);
        }
    }
}
