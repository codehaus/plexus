package org.codehaus.plexus.summit.pull;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

