package org.codehaus.plexus.werkflow;

import java.util.HashMap;

import org.codehaus.plexus.action.Action;
import org.codehaus.plexus.action.DefaultActionManager;
import org.codehaus.werkflow.Instance;
import org.codehaus.werkflow.simple.ActionManager;

/**
 * WerkflowActionManager
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class WerkflowActionManager
	extends DefaultActionManager
	implements ActionManager
{
	public void perform(String id, Instance instance) 
        throws Exception
	{
		Action a = lookup( id );
        
        HashMap map = new HashMap();
        map.put("instance", instance);
        
        a.execute(map);
	}
}
