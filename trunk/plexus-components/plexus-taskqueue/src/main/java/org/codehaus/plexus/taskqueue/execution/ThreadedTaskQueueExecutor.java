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
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;
import org.codehaus.plexus.taskqueue.Task;
import org.codehaus.plexus.taskqueue.TaskQueue;
import org.codehaus.plexus.util.StringUtils;

import edu.emory.mathcs.backport.java.util.concurrent.ExecutionException;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import edu.emory.mathcs.backport.java.util.concurrent.Future;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import edu.emory.mathcs.backport.java.util.concurrent.TimeoutException;

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

    private ExecutorService executorService;

    private class ExecutorRunnable
        extends Thread
    {
        private boolean shutdown;

        private boolean done;

        public void run()
        {
            while ( !shutdown )
            {
                final Task task;

                try
                {
                    task = queue.poll( 100, TimeUnit.MILLISECONDS );
                }
                catch ( InterruptedException e )
                {
                    continue;
                }

                if ( task == null )
                {
                    continue;
                }

                Future future = executorService.submit( new Runnable()
                {
                    public void run()
                    {
                        try
                        {
                            executor.executeTask( task );
                        }
                        catch ( TaskExecutionException e )
                        {
                            getLogger().error( "Error executing task", e );
                        }
                    }
                } );

                try
                {
                    for ( ;; )
                    {
                        try
                        {
                            future.get( task.getMaxExecutionTime(), TimeUnit.MILLISECONDS );
                        }
                        catch ( InterruptedException e )
                        {
                            if ( shutdown )
                            {
                                getLogger()
                                    .info( "Interrupted while waiting for task to complete; shutdown flag set, so cancelling task" );
                                // cancel this task and bail (the while loop will terminate due to the shutdown flag).
                                cancel( future );
                            }
                            else
                            {
                                // when can this thread be interrupted, and should we ignore it if shutdown = false?
                                getLogger().warn( "Interrupted while waiting for task to complete; ignoring", e );
                            }
                        }
                    }
                }
                catch ( ExecutionException e )
                {
                    getLogger().error( "Error executing task", e );
                }
                catch ( TimeoutException e )
                {
                    getLogger().warn( "Task " + task + " didn't complete within time, cancelling it." );

                    cancel( future );
                }
            }

            getLogger().info( "Executor thread '" + name + "' exited." );

            done = true;

            synchronized ( this )
            {
                notifyAll();
            }
        }

        private void cancel( Future future )
        {
            if ( !future.cancel( true ) )
            {
                if ( !future.isDone() && !future.isCancelled() )
                {
                    getLogger().warn( "Unable to cancel task" );
                }
                else
                {
                    getLogger().warn( "Task not cancelled. done: " + future.isDone() + " cancelled: "
                        + future.isCancelled() );
                }
            }
            else
            {
                getLogger().debug( "Task successfully canceled" );
            }
        }

        public void shutdown()
        {
            getLogger().info( "Executor thread got shutdown signal." );

            shutdown = true;

            interrupt();
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
        throws InitializationException
    {
        if ( StringUtils.isEmpty( name ) )
        {
            throw new IllegalArgumentException( "'name' must be set." );
        }
    }

    public void start()
        throws StartingException
    {
        getLogger().info( "Starting task executor, thread name '" + name + "'." );

        this.executorService = Executors.newSingleThreadExecutor();

        executorRunnable = new ExecutorRunnable();

        executorRunnable.setDaemon( true );

        executorRunnable.start();
    }

    public void stop()
        throws StoppingException
    {
        executorRunnable.shutdown();

        int maxSleep = 10 * 1000; // 10 seconds

        int interval = 1000;

        long endTime = System.currentTimeMillis() + maxSleep;

        while ( !executorRunnable.isDone() )
        {
            if ( System.currentTimeMillis() > endTime )
            {
                getLogger().warn( "Timeout, stopping task executor '" + name + "." );

                break;
            }

            getLogger().info( "Waiting until task executor '" + name + "' is idling..." );

            try
            {
                synchronized ( executorRunnable )
                {
                    executorRunnable.wait( interval );
                }
            }
            catch ( InterruptedException ex )
            {
                // ignore
            }
        }
    }
}
