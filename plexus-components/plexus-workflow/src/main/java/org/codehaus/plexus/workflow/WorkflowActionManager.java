package org.codehaus.plexus.workflow;

import org.codehaus.werkflow.simple.ActionManager;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: WorkflowActionManager.java 2044 2005-05-31 03:59:19Z jvanzyl $
 */
public interface WorkflowActionManager
    extends ActionManager
{
    static String ROLE = WorkflowActionManager.class.getName();
}
