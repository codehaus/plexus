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
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;
import org.codehaus.plexus.action.Action;
import org.codehaus.plexus.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @plexus.component
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusActionFunctionProvider
    implements FunctionProvider
{
    private static Map actions = new HashMap();

    public static void setActions( Map actions )
    {
        PlexusActionFunctionProvider.actions = actions;
    }

    // ----------------------------------------------------------------------
    // FunctionProvider Implementation
    // ----------------------------------------------------------------------

    public void execute( Map transientVars, Map args, PropertySet ps )
        throws WorkflowException
    {
/*
        WorkflowEntry entry = (WorkflowEntry) transientVars.get( "entry" );
        WorkflowContext context = (WorkflowContext) transientVars.get( "context" );
        Integer actionId = (Integer) transientVars.get( "actionId" );
        Collection currentSteps = (Collection) transientVars.get( "currentSteps" );
        WorkflowStore store = (WorkflowStore) transientVars.get( "store" );
        WorkflowDescriptor descriptor = (WorkflowDescriptor) transientVars.get( "descriptor" );
*/

        String actionName = (String) args.get( "action.name" );

        if ( StringUtils.isEmpty( actionName ) )
        {
            throw new WorkflowException( "Missing argument 'action.name'." );
        }

        Action action = (Action) actions.get( actionName );

        if ( action == null )
        {
            throw new WorkflowException( "No such action '" + actionName + "'." );
        }

        try
        {
            Map context;

            if ( ps instanceof Map )
            {
                context = (Map) ps;
            }
            else
            {
                context = new PropertySetMap( ps );
            }

            ActionMap actionMap = new ActionMap( context, transientVars );

            action.execute( actionMap );
        }
        catch ( Exception e )
        {
            throw new WorkflowException( "Exception while executing action '" + actionName + "'.", e );
        }
    }
}
