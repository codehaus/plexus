package org.codehaus.plexus.action;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.util.Map;

/**

 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultActionManager
    extends AbstractLogEnabled
    implements ActionManager
{
    private Map actions;

    public Action lookup( String id )
        throws ActionNotFoundException
    {
        Action action = (Action) actions.get( id );

        if ( action == null )
        {
            throw new ActionNotFoundException( "Cannot find action: " + id );
        }

        return action;
    }
}
