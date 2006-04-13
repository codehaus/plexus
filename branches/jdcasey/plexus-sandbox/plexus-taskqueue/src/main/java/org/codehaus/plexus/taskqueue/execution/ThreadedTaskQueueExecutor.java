package org.codehaus.plexus.taskqueue.execution;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.taskqueue.Task;
import org.codehaus.plexus.taskqueue.TaskQueue;
import org.codehaus.plexus.taskqueue.TaskQueueException;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ThreadedTaskQueueExecutor
    extends AbstractLogEnabled
    implements TaskQueueExecutor, Initializable, Startable
{
    /** @requirement */
    private TaskQueue queue;

    /** @requirement */
    private TaskExecutor executor;

    /** @configuration */
    private String name;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private ExecutorRunnable executorRunnable;

    private Thread executorThread;

    private class ExecutorRunnable
        implements Runnable
    {
        private boolean shutdown;

        private boolean done;

        public void run()
        {
            while ( !shutdown )
            {
                Task task;

                try
                {
                    task = queue.take();
                }
                catch ( TaskQueueException e )
                {
                    getLogger().error( "Error while getting task from the task queue.", e );

                    continue;
                }

                if ( task == null )
                {
                    try
                    {
                        Thread.sleep( 100 );
                    }
                    catch ( InterruptedException ex )
                    {
                        // ignore
                    }

                    continue;
                }

                try
                {
                    executor.executeTask( task );
                }
                catch ( Throwable e )
                {
                    getLogger().error( "Error while executing task.", e );
                }
            }

            getLogger().info( "Executor thread '" + name + "' exited." );

            done = true;

            synchronized ( this )
            {
                notifyAll();
            }
        }

        public void shutdown()
        {
            getLogger().info( "Executor thread got shutdown signal." );

            shutdown = true;
        }

        public boolean isDone()
        {
            return done;
        }
    }

    // ----------------------------------------------------------------------
    // Component lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        if ( StringUtils.isEmpty( name ) )
        {
            throw new IllegalArgumentException( "'name' must be set." );
        }
    }

    public void start()
        throws Exception
    {
        getLogger().info( "Starting task executor, thread name '" + name + "'." );

        executorRunnable = new ExecutorRunnable();

        executorThread = new Thread( executorRunnable );

        executorThread.setDaemon( true );

        executorThread.start();
    }

    public void stop()
        throws Exception
    {
        int maxSleep = 10 * 1000; // 10 seconds

        int interval = 1000;

        int slept = 0;

        // signal the thread to stop
        executorRunnable.shutdown();

        executorThread.interrupt();

        while ( !executorRunnable.isDone() )
        {
            if ( slept > maxSleep )
            {
                getLogger().warn( "Timeout, stopping task executor '" + name + "." );

                break;
            }

            getLogger().info( "Waiting until task executor '" + name + "' is idling..." );

            try
            {
                synchronized ( executorThread )
                {
                    executorThread.wait( interval );
                }
            }
            catch ( InterruptedException ex )
            {
                // ignore
            }

            // TODO: should use System.currentTimeMillis()
            slept += interval;
        }
    }
}
