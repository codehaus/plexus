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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Configurable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultTaskQueue
    extends AbstractLogEnabled
    implements TaskQueue, Contextualizable, Configurable, Initializable
{
    private PlexusContainer container;

    private List taskEntryEvaluators;

    private List taskExitEvaluators;

    private List taskViabilityEvaluators;

    private List queue;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void contextualize( Context context )
        throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    public void configure( PlexusConfiguration config )
        throws PlexusConfigurationException
    {
        PlexusConfiguration[] entryEvaluators = config.getChild( "task-entry-evaluators" ).getChildren( "task-entry-evaluator" );

        taskEntryEvaluators = new ArrayList();

        for ( int i = 0; i < entryEvaluators.length; i++ )
        {
            configureEntryEvaluator( entryEvaluators[ i ] );
        }

        PlexusConfiguration[] exitEvaluators = config.getChild( "task-exit-evaluators" ).getChildren( "task-exit-evaluator" );

        taskExitEvaluators = new ArrayList();

        for ( int i = 0; i < entryEvaluators.length; i++ )
        {
            configureExitEvaluator( exitEvaluators[ i ] );
        }

        PlexusConfiguration[] viabilityEvaluators = config.getChild( "task-viability-evaluators" ).getChildren( "task-viability-evaluator" );

        taskViabilityEvaluators = new ArrayList();

        for ( int i = 0; i < viabilityEvaluators.length; i++ )
        {
            configureViabilityEvaluator( viabilityEvaluators[ i ] );
        }
    }

    public void initialize()
        throws Exception
    {
        queue = new LinkedList();
    }

    // ----------------------------------------------------------------------
    // TaskQueue Implementation
    // ----------------------------------------------------------------------

    public boolean put( Task task )
        throws TaskQueueException
    {
        // ----------------------------------------------------------------------
        // Check that all the task entry evaluators accepts the task
        // ----------------------------------------------------------------------

        for ( Iterator it = taskEntryEvaluators.iterator(); it.hasNext(); )
        {
            TaskEntryEvaluator taskEntryEvaluator = (TaskEntryEvaluator) it.next();

            boolean result = taskEntryEvaluator.evaluate( task );

            if ( !result )
            {
                return false;
            }
        }

        // ----------------------------------------------------------------------
        // The task was accepted, enqueue it
        // ----------------------------------------------------------------------

        enqueue( task );

        // ----------------------------------------------------------------------
        // Check that all the task viability evaluators accepts the task
        // ----------------------------------------------------------------------

        for ( Iterator it = taskViabilityEvaluators.iterator(); it.hasNext(); )
        {
            TaskViabilityEvaluator taskViabilityEvaluator = (TaskViabilityEvaluator) it.next();

            List toBeRemoved = taskViabilityEvaluator.evaluate( Collections.unmodifiableList( queue ) );

            for ( Iterator it2 = toBeRemoved.iterator(); it2.hasNext(); )
            {
                Task t = (Task) it2.next();

                queue.remove( t );
            }
        }

        return true;
    }

    public Task take()
        throws TaskQueueException
    {
        while( true )
        {
            Task task = dequeue();

            if ( task == null )
            {
                return null;
            }

            for ( Iterator it = taskExitEvaluators.iterator(); it.hasNext(); )
            {
                TaskExitEvaluator taskExitEvaluator = (TaskExitEvaluator) it.next();

                boolean result = taskExitEvaluator.evaluate( task );

                if ( !result )
                {
                    // the task wasn't accepted; drop it.
                    task = null;

                    break;
                }
            }

            if ( task != null )
            {
                return task;
            }
        }
    }

    // ----------------------------------------------------------------------
    // Queue Management
    // ----------------------------------------------------------------------

    private void enqueue( Task task )
    {
        synchronized( queue )
        {
            queue.add( task );
        }
    }

    private Task dequeue()
    {
        synchronized( queue )
        {
            if ( queue.size() == 0 )
            {
                return null;
            }

            return (Task) queue.remove( 0 );
        }
    }

    // ----------------------------------------------------------------------
    // Configuration Helpers
    // ----------------------------------------------------------------------

    protected void configureEntryEvaluator( PlexusConfiguration config )
        throws PlexusConfigurationException
    {
        String name = config.getValue();

        TaskEntryEvaluator taskEntryEvaluator = null;

        try
        {
            taskEntryEvaluator = (TaskEntryEvaluator) container.lookup( TaskEntryEvaluator.ROLE, name );
        }
        catch ( ComponentLookupException e )
        {
            throw new PlexusConfigurationException( "Couldn't look up task entry evaluator '" + name + "'.", e );
        }

        taskEntryEvaluators.add( taskEntryEvaluator );
    }

    protected void configureExitEvaluator( PlexusConfiguration config )
        throws PlexusConfigurationException
    {
        String name = config.getValue();

        TaskExitEvaluator taskExitEvaluator = null;

        try
        {
            taskExitEvaluator = (TaskExitEvaluator) container.lookup( TaskExitEvaluator.ROLE, name );
        }
        catch ( ComponentLookupException e )
        {
            throw new PlexusConfigurationException( "Couldn't look up task exit evaluator '" + name + "'.", e );
        }

        taskExitEvaluators.add( taskExitEvaluator );
    }

    protected void configureViabilityEvaluator( PlexusConfiguration config )
        throws PlexusConfigurationException
    {
        String name = config.getValue();

        TaskViabilityEvaluator taskViabilityEvaluator = null;

        try
        {
            taskViabilityEvaluator = (TaskViabilityEvaluator) container.lookup( TaskViabilityEvaluator.ROLE, name );
        }
        catch ( ComponentLookupException e )
        {
            throw new PlexusConfigurationException( "Couldn't look up task viability evaluator '" + name + "'.", e );
        }

        taskViabilityEvaluators.add( taskViabilityEvaluator );
    }
}
