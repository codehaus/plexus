package org.codehaus.plexus.werkflow;

import java.util.HashMap;
import java.util.Properties;

import org.codehaus.plexus.action.Action;
import org.codehaus.plexus.action.DefaultActionManager;
import org.codehaus.werkflow.spi.Instance;
import org.codehaus.werkflow.simple.ActionManager;

/**
 * WerkflowActionManager
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 */
public class WerkflowActionManager
    extends DefaultActionManager
    implements ActionManager
{
    public void perform(String actionId, Instance instance, Properties properties) throws Exception
    {
        Action a = lookup(actionId);
        HashMap map = new HashMap();
        map.put("instance", instance);

        properties.put("actionId", actionId);
        map.put("action", properties);

        System.out.println("properties: " + properties);

        a.execute(map);
    }
}
