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

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

import java.util.List;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface TaskQueue
{
    String ROLE = TaskQueue.class.getName();

    // ----------------------------------------------------------------------
    // Queue operations
    // ----------------------------------------------------------------------

    /**
     * @param task
     *            The task to add to the queue.
     * @return Returns true if the task was accepted into the queue.
     */
    boolean put( Task task )
        throws TaskQueueException;

    Task take()
        throws TaskQueueException;

    // ----------------------------------------------------------------------
    // Queue Inspection
    // ----------------------------------------------------------------------

    List getQueueSnapshot()
        throws TaskQueueException;

    /**
     * Retrieves and removes the head of the queue, waiting at most timeout timeUnit when no element is available.
     *
     * @param timeout
     *            time to wait, in timeUnit units
     * @param timeUnit
     *            how to interpret the timeout parameter.
     * @return the head of the queue, or null if the timeout elapsed
     * @throws InterruptedException
     *             when this thread is interrupted while waiting
     */
    Task poll( int timeout, TimeUnit timeUnit )
        throws InterruptedException;
}
