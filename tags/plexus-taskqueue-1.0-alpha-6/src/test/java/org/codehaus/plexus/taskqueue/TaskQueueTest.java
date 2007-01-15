package org.codehaus.plexus.taskqueue;

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

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class TaskQueueTest
    extends PlexusTestCase
{
    private TaskQueue taskQueue;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        taskQueue = (TaskQueue) lookup( TaskQueue.ROLE );
    }

    // NOTE: If we were using a blocking queue, the sleep/continue in the ThreadedTaskQueueExecutor wouldn't
    // be necessary; the queue would block until an element was available.
    public void testEmptyQueue()
        throws Exception
    {
        assertNull( taskQueue.take() );
    }

    public void testTaskEntryAndExitEvaluators()
        throws Exception
    {
        assertTaskIsAccepted( new BuildProjectTask( true, true, true, true ) );

        assertTaskIsRejected( new BuildProjectTask( false, true, true, true ) );

        assertTaskIsRejected( new BuildProjectTask( true, false, true, true ) );

        assertTaskIsRejected( new BuildProjectTask( true, true, false, true ) );

        assertTaskIsRejected( new BuildProjectTask( true, true, true, false ) );
    }

    public void testTaskViabilityEvaluators()
        throws Exception
    {
        // The first and last task should be accepted

        Task task1 = new BuildProjectTask( 0 );

        Task task2 = new BuildProjectTask( 10 );

        Task task3 = new BuildProjectTask( 20 );

        Task task4 = new BuildProjectTask( 30 );

        Task task5 = new BuildProjectTask( 40 );

        Task task6 = new BuildProjectTask( 100 );

        assertTrue( taskQueue.put( task1 ) );

        assertTrue( taskQueue.put( task2 ) );

        assertTrue( taskQueue.put( task3 ) );

        assertTrue( taskQueue.put( task4 ) );

        assertTrue( taskQueue.put( task5 ) );

        assertTrue( taskQueue.put( task6 ) );

        Task actualTask1 = taskQueue.take();

        assertNotNull( actualTask1 );

        assertEquals( task1, actualTask1 );

        Task actualTask6 = taskQueue.take();

        assertNotNull( actualTask6 );

        assertEquals( task6, actualTask6 );

        assertNull( taskQueue.take() );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void assertTaskIsAccepted( Task expectedTask )
        throws Exception
    {
        taskQueue.put( expectedTask );

        Task actualTask = taskQueue.take();

        assertEquals( expectedTask, actualTask );
    }

    private void assertTaskIsRejected( Task expectedTask )
        throws Exception
    {
        taskQueue.put( expectedTask );

        Task actualTask = taskQueue.take();

        assertNull( actualTask );
    }
}
