package org.codehaus.plexus.workflow;

import java.util.Map;

/**
 * This is a generic workflow manager component.
 * 
 * <b>NOTES:</b>
 * <ul>
 *   <li>The separation of the process package and the process id might be a bit werkflow specific.</li>
 * </ul>
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 *
 * @version $Id$
 */
public interface WorkflowManager
{
/*
    void startManager( Map context )
        throws Exception;
*/

    void acceptActivityMessage( Object message )
        throws WorkflowException;

    String callProcess(String packageId, String processId, Map attributes) 
        throws WorkflowException;

    void waitForProcess(String packageId, String processId, String id) 
        throws WorkflowException;

    Map getProcessAttributes(String packageId, String processId, String caseId)
        throws WorkflowException;
}
