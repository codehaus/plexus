package org.codehaus.plexus.summit.pipeline.valve;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
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
 */

import org.codehaus.plexus.action.Action;
import org.codehaus.plexus.action.ActionManager;
import org.codehaus.plexus.action.ActionNotFoundException;
import org.codehaus.plexus.summit.rundata.RunData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: ActionValve.java 2034 2005-05-29 09:31:54Z trygvis $
 * @plexus.component role="org.codehaus.plexus.summit.pipeline.valve.Valve"
 * role-hint="org.codehaus.plexus.action.web.ActionValve"
 */
public class ActionValve
    extends AbstractValve
{
    /**
     * @plexus.requirement
     */
    private ActionManager actionManager;

    public void invoke( RunData data )
        throws IOException, ValveInvocationException
    {
        String actionId = data.getParameters().getString( "action", "" );

        if ( !actionId.equals( "" ) )
        {
            Action action;

            try
            {
                action = actionManager.lookup( actionId.trim() );

                Map m = createContext( data );

                m.put( "data", data );

                action.execute( m );
            }
            catch ( ActionNotFoundException e )
            {
                throw new ValveInvocationException( "Action with id = " + actionId + " cannot be found.", e );
            }
            catch ( Exception e )
            {
                throw new ValveInvocationException( "Error executing the action with id = " + actionId, e );
            }
        }
    }

    protected Map createContext( RunData data )
    {
        // The parameter map in the request consists of an array of values for
        // the given key so this is why this is being done.
        Map m = new HashMap();

        for ( Iterator i = data.getParameters().keys(); i.hasNext(); )
        {
            String key = (String) i.next();

            m.put( key, data.getParameters().get( key ) );
        }
        return m;
    }
}
