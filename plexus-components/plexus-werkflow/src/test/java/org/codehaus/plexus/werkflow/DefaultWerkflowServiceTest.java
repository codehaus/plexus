package org.codehaus.plexus.werkflow;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.werkflow.Engine;
import org.codehaus.werkflow.InitialContext;
import org.codehaus.werkflow.Instance;

/**
 * DefaultWerkflowServiceTest
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 */
public class DefaultWerkflowServiceTest
    extends PlexusTestCase
{
    public void testWerkflow() throws Exception
    {
        WerkflowService s = (WerkflowService) lookup(WerkflowService.ROLE);

        Engine engine = s.getEngine();
        assertNotNull(engine);

        InitialContext c = new InitialContext();
        c.set("true", Boolean.TRUE);
        c.set("false", Boolean.FALSE);

        Instance i = s.getEngine().newInstance("ted", "instance1", c);

        Thread.sleep(2000);

        assertNotNull(i.get("one"));

        assertTrue(i.isComplete());
    }
}
