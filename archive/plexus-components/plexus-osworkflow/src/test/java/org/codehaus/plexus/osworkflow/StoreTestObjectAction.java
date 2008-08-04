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

import org.codehaus.plexus.action.Action;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class StoreTestObjectAction
    extends AbstractLogEnabled
    implements Action
{
    public void setResultMessages( List list, Map map )
    {
    }

    public void addResultMessage( String string, Map map )
    {
    }

    public List getResultMessages( Map map )
    {
        return null;
    }

    public boolean hasResultMessages( Map map )
    {
        return false;
    }

    public void execute( Map map )
        throws Exception
    {
        getLogger().info( "Context " + map.toString() );

        TestObject object = (TestObject) map.get( "test-object" );

        if ( object == null )
        {
            throw new Exception( "Missing object from the context 'test-object'." );
        }

        getLogger().info( "Storing '" + object.getName() + "'." );
    }
}
