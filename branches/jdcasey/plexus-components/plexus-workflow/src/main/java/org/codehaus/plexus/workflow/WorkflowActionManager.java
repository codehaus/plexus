package org.codehaus.plexus.workflow;

import org.codehaus.werkflow.simple.ActionManager;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface WorkflowActionManager
    extends ActionManager
{
    static String ROLE = WorkflowActionManager.class.getName();
}
