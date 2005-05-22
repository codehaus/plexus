package org.codehaus.plexus.action;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.action.web.ActionValve;
import org.codehaus.plexus.summit.pipeline.valve.Valve;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class ActionTest
    extends PlexusTestCase
{
    public void testActions() 
        throws Exception
    {
        ActionManager manager = (ActionManager) lookup(ActionManager.ROLE);
        assertNotNull(manager);
        
        Action a = manager.lookup("foo");
        assertNotNull(a);
        assertTrue(a instanceof FooAction);
        
        try
        {
            Action b = manager.lookup("bleh");
            fail("ActionNotFoundException wasn't thrown.");
        }
        catch (ActionNotFoundException e)
        {
        }
    }
    
    public void testValve() 
        throws Exception
    {
        ActionValve valve = (ActionValve) lookup(Valve.ROLE, ActionValve.class.getName());
        
        assertNotNull(valve);
    }
}
