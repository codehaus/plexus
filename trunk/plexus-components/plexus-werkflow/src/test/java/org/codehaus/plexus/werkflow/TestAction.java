package org.codehaus.plexus.werkflow;

import java.util.Map;
import java.util.Properties;

import junit.framework.Assert;

import org.codehaus.plexus.action.Action;
import org.codehaus.werkflow.spi.Instance;

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
        Properties p = (Properties) context.get("action");

        assertNotNull(i);
        assertNotNull(p);
        String state = (String) p.get("state");

        if (state != null)
        {
            i.put("state", state);
        }


    }

}
