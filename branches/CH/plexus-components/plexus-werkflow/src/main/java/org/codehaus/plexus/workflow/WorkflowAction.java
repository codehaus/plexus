package org.codehaus.plexus.workflow;

/*
 * LICENSE
 */

import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface WorkflowAction {
    void execute(Map caseAttributes, Map otherAttributes)
        throws Exception;
}
