package org.codehaus.plexus.osworkflow;

/*
 * Copyright 2005 The Apache Software Foundation.
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
 *
 */

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.osworkflow.PlexusOSWorkflow;
import org.codehaus.plexus.osworkflow.PlexusOSWorkflowException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultPlexusOsWorkflowTest
    extends PlexusTestCase
{
    private PlexusOSWorkflow workflowEngine;

    private String workflowId;

    private boolean stop;

    public void testInitialization()
        throws Exception
    {
        workflowEngine = (PlexusOSWorkflow) lookup( PlexusOSWorkflow.ROLE );

        Map context = new HashMap();

        Thread thread = new CheckerThread();

        synchronized ( thread )
        {
            System.err.println( "Starting" );
            thread.start();
            System.err.println( "waiting 1" );
            thread.wait();
            System.err.println( "waited 1" );
        }

        String workflowName = "add-objects";

        workflowId = workflowEngine.startWorkflow( workflowName, "myworkflow", "trygvis", context );

        System.out.println( "workflowId = " + workflowId );

        Thread.sleep( 1000 );

        Map actionContext = new HashMap();

        actionContext.put( "number-of-objects", new Integer( 10 ) );

        workflowEngine.doAction( workflowId, "1", actionContext );

        synchronized( thread )
        {
            System.err.println( "stopping" );
            stop = true;
            thread.interrupt();
            System.err.println( "waiting 2" );
            thread.wait();
        }
    }

    private boolean ping()
    {
        try
        {
            if ( workflowEngine == null || workflowId == null )
            {
                return false;
            }

            System.err.println( "pinging!" );

            return workflowEngine.isWorkflowDone( workflowId );
        }
        catch ( PlexusOSWorkflowException e )
        {
            e.printStackTrace();

            return false;
        }
    }

    private class CheckerThread
        extends Thread
    {
        public void run()
        {
            synchronized ( this )
            {
                System.err.println( "Started!" );
                notifyAll();
            }

            while ( !stop )
            {
                if ( ping() )
                {
                    break;
                }

                try
                {
                    Thread.sleep( 100 );
                }
                catch ( InterruptedException e )
                {
                    System.err.println( "Interrupted" );
                    stop = true;
                }
            }

            System.err.println( "stopped" );

            synchronized ( this )
            {
                notifyAll();
            }
        }
    }
}
