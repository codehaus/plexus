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
public class BasicWorkflowTest extends PlexusTestCase
{
    public void testBasic() throws Exception
    {
        WorkflowManager manager;
        AddUserMessage addUser;
        Map beans = new HashMap();

        manager = (WorkflowManager)lookup(WorkflowManager.class.getName());

        addUser = new AddUserMessage();
        addUser.setPayload("Hello World!");

        manager.acceptActivityMessage(addUser);

        assertTrue(AddUserWorkflowAction.isExecuted());
    }

    public void testCallProcess() throws Exception {
        WorkflowManager manager;
        AddUserMessage addUser;
        String payload = "Hello World!".toUpperCase();
        String id;
        Map attributes;

        manager = (WorkflowManager)lookup(WorkflowManager.class.getName());

        attributes = new HashMap();
        attributes.put("inParam", payload);

        id = manager.callProcess("test", "callableProcess", attributes);
        System.err.println("id: " + id);

        manager.waitForProcess("test", "callableProcess", id);
        attributes = manager.getProcessAttributes("test", "callableProcess", id);
        Thread.sleep(2000);

        System.err.println("attributes: " + attributes);
        assertTrue(CalledWorkflowAction.isExecuted());
        assertEquals(payload.toLowerCase(), attributes.get("outParam"));
    }
}
