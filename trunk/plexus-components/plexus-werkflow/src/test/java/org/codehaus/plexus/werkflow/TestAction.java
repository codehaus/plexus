package org.codehaus.plexus.werkflow;

import java.util.Map;

import junit.framework.Assert;

import org.codehaus.plexus.action.Action;
import org.codehaus.werkflow.Instance;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 */
public class TestAction
    extends Assert
    implements Action
{
    /**
     * @see org.codehaus.plexus.action.Action#execute(java.util.Map)
     */
    public void execute(Map context) throws Exception
    {
        Instance i = (Instance) context.get("instance");

        assertNotNull(i);

        i.put("one", "one");
    }

}
