package org.codehaus.plexus.summit.pull;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.summit.view.DefaultViewContext;

/**
 * DefaultPoolServiceTest.java
 * 
 * @author <a href="dan@envoisolutions.com">Dan Diephouse</a>
 * @since Jan 27, 2003
 */
public class DefaultPullServiceTest
    extends PlexusTestCase
{    
    public void testService()
        throws Exception
    {
        PullService pull = ( PullService ) lookup ( PullService.ROLE );

        assertNotNull( pull );

        DefaultViewContext context = new DefaultViewContext();
        
        // Even though RunData is null, it will still populate the context with
        // the global tools.
        pull.populateContext( context, null );
        
         MockTool tool = (MockTool) context.get( "tool" );

        assertNotNull( tool );
        
        //releaseComponent( pull );
        pull = null;
    }
}

