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
public class CalledWorkflowAction
    extends AbstractLogEnabled
    implements WorkflowAction {

    public static boolean executed;

    public void execute(Map caseAttributes, Map otherAttributes)
    {
        Object obj;
        String payload;

        getLogger().info("CalledWorkflowAction");
        getLogger().info("caseAttributes: " + caseAttributes);
        getLogger().info("otherAttributes: " + otherAttributes);
        obj = caseAttributes.get("inParam");

        Assert.assertNotNull(obj);
        Assert.assertTrue(obj instanceof String);

        payload = (String)obj;
        Assert.assertEquals("Hello World!".toUpperCase(), payload);
        caseAttributes.put("output", payload.toLowerCase());

        executed = true;
    }

    public static boolean isExecuted() {
        return executed;
    }
}
