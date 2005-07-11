package org.codehaus.plexus.action;

import org.codehaus.plexus.PlexusTestCase;

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
}
