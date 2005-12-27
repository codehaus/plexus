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
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.StoreException;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.config.Configuration;
import com.opensymphony.workflow.config.DefaultConfiguration;
import com.opensymphony.workflow.spi.WorkflowStore;
import com.opensymphony.workflow.spi.WorkflowEntry;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import java.util.Map;
import java.util.Properties;
import java.util.HashMap;

/**
 * @plexus.component
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
     * @plexus.requirement role-hint="org.codehaus.plexus.action.Action"
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

    public long startWorkflow( String workflowName, String workflowInstanceName, String username, Map context )
        throws PlexusOSWorkflowException
    {
        Workflow workflow = new BasicWorkflow( username );

        workflow.setConfiguration( configuration );

        long workflowId;

        getLogger().info( "Initializing osworkflow '" + workflowName + "'." );

        try
        {
            workflowId = workflow.initialize( workflowName, 1, context );
        }
        catch ( WorkflowException e )
        {
            throw new PlexusOSWorkflowException( "Error while initializing the osworkflow.", e );
        }

        workflowInstances.put( Long.toString( workflowId ), workflow );

        return workflowId;
    }

    public boolean isWorkflowDone( long workflowId )
        throws PlexusOSWorkflowException
    {
        try
        {
            WorkflowEntry entry = store.findEntry( workflowId );

            int state = entry.getState();
            getLogger().info( "entry.getState() = " + state );

            return state == WorkflowEntry.COMPLETED ||
                   state == WorkflowEntry.KILLED;
        }
        catch ( WorkflowException e )
        {
            throw new PlexusOSWorkflowException( "Error while querying for osworkflow state.", e );
        }
    }

    public void doAction( long workflowId, int actionId, Map actionContext )
        throws PlexusOSWorkflowException
    {
        try
        {
            getWorkflow( workflowId ).doAction( workflowId,
                                                actionId,
                                                actionContext );
        }
        catch ( StoreException e )
        {
            throw new PlexusOSWorkflowException( "Error while execution action.", e );
        }
        catch ( InvalidInputException e )
        {
            throw new PlexusOSWorkflowException( "Error while execution action.", e );
        }
        catch ( WorkflowException e )
        {
            throw new PlexusOSWorkflowException( "Error while execution action.", e );
        }
    }

    // ----------------------------------------------------------------------
    // Private
    // ----------------------------------------------------------------------

    private Workflow getWorkflow( long workflowId )
        throws PlexusOSWorkflowException
    {
        Workflow workflow = (Workflow) workflowInstances.get( Long.toString( workflowId ) );

        if ( workflow == null )
        {
            throw new PlexusOSWorkflowException( "No such osworkflow '" + workflowId + "'.");
        }

        // TODO: if the osworkflow is finished, remove it.

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
