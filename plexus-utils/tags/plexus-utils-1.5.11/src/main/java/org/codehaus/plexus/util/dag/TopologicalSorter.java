package org.codehaus.plexus.util.dag;

/*
 * Copyright The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="michal.maczka@dimatics.com">Michal Maczka</a>
 * @version $Id$
 */
public class TopologicalSorter
{

    private final static Integer NOT_VISTITED = new Integer( 0 );

    private final static Integer VISITING = new Integer( 1 );

    private final static Integer VISITED = new Integer( 2 );

    /**
     * @param graph
     * @return List of String (vertex labels)
     */

    public static List sort( final DAG graph )
    {
        return dfs( graph );
    }

    public static List sort( final Vertex vertex )
    {
        // we need to use addFirst method so we will use LinkedList explicitly
        final LinkedList retValue = new LinkedList();

        final Map vertexStateMap = new HashMap();

        dfsVisit( vertex, vertexStateMap, retValue );

        return retValue;
    }


    private static List dfs( final DAG graph )
    {
        final List verticies = graph.getVerticies();

        // we need to use addFirst method so we will use LinkedList explicitly
        final LinkedList retValue = new LinkedList();

        final Map vertexStateMap = new HashMap();

        for ( final Iterator iter = verticies.iterator(); iter.hasNext(); )
        {
            final Vertex vertex = ( Vertex ) iter.next();

            if ( isNotVisited( vertex, vertexStateMap ) )
            {
                dfsVisit( vertex, vertexStateMap, retValue );
            }
        }

        return retValue;
    }

    /**
     * @param vertex
     * @param vertexStateMap
     * @return
     */
    private static boolean isNotVisited( final Vertex vertex, final Map vertexStateMap )
    {
        if ( !vertexStateMap.containsKey( vertex ) )
        {
            return true;
        }
        final Integer state = ( Integer ) vertexStateMap.get( vertex );

        return NOT_VISTITED.equals( state );
    }


    private static void dfsVisit( final Vertex vertex, final Map vertexStateMap, final LinkedList list )
    {
        vertexStateMap.put( vertex, VISITING );

        final List verticies = vertex.getChildren();

        for ( final Iterator iter = verticies.iterator(); iter.hasNext(); )
        {
            final Vertex v = ( Vertex ) iter.next();

            if ( isNotVisited( v, vertexStateMap ) )
            {
                dfsVisit( v, vertexStateMap, list );
            }
        }

        vertexStateMap.put( vertex, VISITED );

        list.add( vertex.getLabel() );
    }

}

