package org.codehaus.plexus.workflow;

/*
 * LICENSE
 */

import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface WorkflowActionManager {
    boolean hasAction(String name);

    void execute(Map caseAttributes, Map otherAttributes)
        throws WorkflowException;
}
