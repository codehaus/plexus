package org.codehaus.plexus.workflow;

/*
 * LICENSE
 */

import java.util.Map;
import junit.framework.Assert;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class AddUserWorkflowAction 
    extends AbstractLogEnabled
    implements WorkflowAction {

    public static boolean executed;

    public void execute(Map caseAttributes, Map otherAttributes)
    {
        Object message;
        AddUserMessage entity;

        getLogger().info("AddUserWorkflowAction");
        getLogger().info("caseAttributes: " + caseAttributes);
        getLogger().info("otherAttributes: " + otherAttributes);
        message = caseAttributes.get("message");

        Assert.assertNotNull(message);
        Assert.assertTrue(message instanceof AddUserMessage);

        entity = (AddUserMessage)message;
        Assert.assertEquals("Hello World!", entity.getPayload());

        executed = true;
    }

    public static boolean isExecuted() {
        return executed;
    }
}
