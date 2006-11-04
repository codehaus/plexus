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

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

import java.util.Map;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface PlexusOSWorkflow
{
    String ROLE = PlexusOSWorkflow.class.getName();

    long startWorkflow( String workflowName, String username, Map context )
        throws WorkflowException;

    PropertySet getContext( long workflowId )
        throws WorkflowException;

    boolean isWorkflowDone( long workflowId )
        throws WorkflowException;

    void doAction( long workflowId, int actionId, Map actionContext )
        throws WorkflowException;

    List getCurrentSteps( long workflowId )
        throws WorkflowException;
}
