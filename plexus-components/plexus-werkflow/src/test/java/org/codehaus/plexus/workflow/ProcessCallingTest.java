package org.codehaus.plexus.workflow;

/*
 * LICENSE
 */

import java.util.Map;
import java.util.HashMap;
import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ProcessCallingTest extends PlexusTestCase
{
    public void testCallProcess() throws Exception {
        WorkflowManager manager;
        AddUserMessage addUser;
        String payload = "Hello World!";
        String id;
        Map attributes;

        manager = (WorkflowManager)lookup(WorkflowManager.class.getName());

        attributes = new HashMap();
        attributes.put("inParam", payload);

        id = manager.callProcess("test", "callableProcess", attributes);
        System.err.println("id: " + id);
        attributes = manager.getProcessAttributes("test", "callableProcess", id);
        System.err.println("attributes: " + attributes);
        assertTrue(CalledWorkflowAction.isExecuted());
        assertNotNull(attributes.get("outParam"));
        assertEquals(attributes.get("outParam"), payload.toLowerCase());
    }
}
