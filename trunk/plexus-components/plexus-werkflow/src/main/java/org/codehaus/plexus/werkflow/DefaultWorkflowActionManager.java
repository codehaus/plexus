package org.codehaus.plexus.werkflow;

import org.codehaus.plexus.action.Action;
import org.codehaus.plexus.action.DefaultActionManager;
import org.codehaus.werkflow.spi.Instance;
import org.codehaus.werkflow.simple.ActionManager;

import java.util.HashMap;
import java.util.Properties;

/**
 * WerkflowActionManager
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 */
public class DefaultWorkflowActionManager
    extends DefaultActionManager
    implements WorkflowActionManager
{
    public void perform( String actionId, Instance instance, Properties properties )
        throws Exception
    {
        Action action = lookup( actionId );

        HashMap map = new HashMap();

        map.put( "instance", instance );

        properties.put( "actionId", actionId );

        map.put( "properties", properties );

        action.execute( map );
    }
}
