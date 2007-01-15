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

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class BuildProjectTask
    implements Task
{
    private boolean passAEntryEvaluator;

    private boolean passBEntryEvaluator;

    private boolean passAExitEvaluator;

    private boolean passBExitEvaluator;

    private long timestamp;

    private long maxExecutionTime;

    private long executionTime;

    private boolean cancelled;

    private boolean done;

    private boolean started;

    private boolean ignoreInterrupts;

    public BuildProjectTask( boolean passAEntryEvaluator, boolean passBEntryEvaluator, boolean passAExitEvaluator,
                             boolean passBExitEvaluator )
    {
        this.passAEntryEvaluator = passAEntryEvaluator;

        this.passBEntryEvaluator = passBEntryEvaluator;

        this.passAExitEvaluator = passAExitEvaluator;

        this.passBExitEvaluator = passBExitEvaluator;
    }

    public BuildProjectTask( long timestamp )
    {
        this( true, true, true, true );

        this.timestamp = timestamp;
    }

    public boolean isPassAEntryEvaluator()
    {
        return passAEntryEvaluator;
    }

    public boolean isPassBEntryEvaluator()
    {
        return passBEntryEvaluator;
    }

    public boolean isPassAExitEvaluator()
    {
        return passAExitEvaluator;
    }

    public boolean isPassBExitEvaluator()
    {
        return passBExitEvaluator;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public long getMaxExecutionTime()
    {
        return maxExecutionTime;
    }

    public void setMaxExecutionTime( long timeout )
    {
        maxExecutionTime = timeout;
    }

    public void setExecutionTime( long l )
    {
        this.executionTime = l;
    }

    public long getExecutionTime()
    {
        return executionTime;
    }

    public boolean isCancelled()
    {
        return cancelled;
    }

    public void cancel()
    {
        cancelled = true;
    }

    public void done()
    {
        this.done = true;
    }

    public boolean isDone()
    {
        return done;
    }

    public boolean isStarted()
    {
        return started;
    }

    public void start()
    {
        this.started = true;
    }

    public void setIgnoreInterrupts( boolean ignore )
    {
        this.ignoreInterrupts = ignore;
    }

    public boolean ignoreInterrupts()
    {
        return ignoreInterrupts;
    }

}
