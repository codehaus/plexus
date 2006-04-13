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

import com.opensymphony.workflow.FactoryException;
import com.opensymphony.workflow.StoreException;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.config.Configuration;
import com.opensymphony.workflow.config.DefaultConfiguration;
import com.opensymphony.workflow.spi.WorkflowEntry;
import com.opensymphony.workflow.spi.WorkflowStore;
import com.opensymphony.module.propertyset.PropertySet;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.List;

/**
 * @plexus.component
 *   role="org.codehaus.plexus.osworkflow.PlexusOSWorkflow"
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultPlexusOsWorkflow
    extends AbstractLogEnabled
    implements Initializable, PlexusOSWorkflow
{
    // ----------------------------------------------------------------------
    // Requirements
    // ----------------------------------------------------------------------

    /**
     * @plexus.requirement role="org.codehaus.plexus.action.Action"
     */
    private Map actions;

    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    /**
     * @plexus.configuration
     */
    private Properties properties;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private Configuration configuration;

    private WorkflowStore store;

    private Map workflowInstances = new HashMap();

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        getLogger().info( "Initializing OS Workflow." );

        PlexusActionFunctionProvider.setActions( actions );

        configuration = new DefaultConfiguration();

        try
        {
            configuration.load( null );

            getLogger().info( "Persistence engine: " + configuration.getPersistence() );

            String[] workflowNames = configuration.getWorkflowNames();

            getLogger().info( "Showing all currently configured osworkflow names (" + workflowNames.length + " in total):" );

            for ( int i = 0; i < workflowNames.length; i++ )
            {
                String workflowName = workflowNames[ i ];

                getLogger().info( " " + workflowName );
            }

            store = configuration.getWorkflowStore();
        }
        catch ( FactoryException e )
        {
            throw new InitializationException( "Could not initialize OS Workflow.", e );
        }
        catch ( StoreException e )
        {
            throw new InitializationException( "Could not initialize OS Workflow.", e );
        }

        getLogger().info( "OS Workflow initialized." );
    }

    // ----------------------------------------------------------------------
    // PlexusOSWorkflow Implementation
    // ----------------------------------------------------------------------

    public long startWorkflow( String workflowName, String username, Map context )
        throws WorkflowException
    {
        Workflow workflow = new BasicWorkflow( username );

        workflow.setConfiguration( configuration );

        getLogger().info( "Starting workflow '" + workflowName + "'." );

        long workflowId = workflow.initialize( workflowName, 1, context );

        workflowInstances.put( Long.toString( workflowId ), workflow );

        return workflowId;
    }

    public PropertySet getContext( long workflowId )
        throws WorkflowException
    {
        return store.getPropertySet( workflowId );
    }

    public boolean isWorkflowDone( long workflowId )
        throws WorkflowException
    {
        WorkflowEntry entry = store.findEntry( workflowId );

        int state = entry.getState();

        return state == WorkflowEntry.COMPLETED ||
               state == WorkflowEntry.KILLED;
    }

    public void doAction( long workflowId, int actionId, Map actionContext )
        throws WorkflowException
    {
        getWorkflow( workflowId ).doAction( workflowId,
                                            actionId,
                                            actionContext );
    }

    public List getCurrentSteps( long workflowId )
        throws WorkflowException
    {
        try
        {
            return store.findCurrentSteps( workflowId );
        }
        catch ( StoreException e )
        {
            throw new WorkflowException( "Error while accessing the store.", e );
        }
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    private Workflow getWorkflow( long workflowId )
        throws WorkflowException
    {
        Workflow workflow = (Workflow) workflowInstances.get( Long.toString( workflowId ) );

        if ( workflow == null )
        {
            throw new WorkflowException( "No such osworkflow '" + workflowId + "'.");
        }

        // TODO: if the workflow is finished, remove it.

        return workflow;
//        try
//        {
//            WorkflowStore store = configuration.getWorkflowStore();
//
//            WorkflowEntry entry = store.findEntry( toWorkflowId( workflowId ) );
//
//            return entry.;
//        }
//        catch ( StoreException e )
//        {
//            throw new PlexusOSWorkflowException( "Error while retreiving the osworkflow.", e );
//        }
    }
}
