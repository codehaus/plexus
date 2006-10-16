package org.codehaus.plexus.graphing.model.dag;

/*
 * Copyright 2001-2006 The Codehaus.
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

import org.codehaus.plexus.graphing.model.Node;

import java.util.Collection;
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
     * Perform a Depth First Search based sort against the graph.
     * 
     * @param graph the graph to sort
     * @return the sorted list of node labels
     */
    public static List sort( final Dag graph )
    {
        return sortDepthFirstSearch( graph );
    }

    /**
     * Perform a Depth First Serach based sort against the node.
     * 
     * @param node the node to search from.
     * @return the sorted list of node labels.
     */
    public static List sort( final Node node )
    {
        // we need to use addFirst method so we will use LinkedList explicitly
        final LinkedList retValue = new LinkedList();

        final Map nodeStateMap = new HashMap();

        visitDepthFirstSearch( node, nodeStateMap, retValue );

        return retValue;
    }

    private static List sortDepthFirstSearch( final Dag graph )
    {
        final Collection verticies = graph.getNodes();

        // we need to use addFirst method so we will use LinkedList explicitly
        final LinkedList retValue = new LinkedList();

        final Map nodeStateMap = new HashMap();

        for ( final Iterator iter = verticies.iterator(); iter.hasNext(); )
        {
            final Node node = (Node) iter.next();

            if ( isNotVisited( node, nodeStateMap ) )
            {
                visitDepthFirstSearch( node, nodeStateMap, retValue );
            }
        }

        return retValue;
    }

    /**
     * @param node
     * @param nodeStateMap
     * @return
     */
    private static boolean isNotVisited( final Node node, final Map nodeStateMap )
    {
        if ( !nodeStateMap.containsKey( node ) )
        {
            return true;
        }
        final Integer state = (Integer) nodeStateMap.get( node );

        return NOT_VISTITED.equals( state );
    }

    private static void visitDepthFirstSearch( final Node node, final Map nodeStateMap, final LinkedList list )
    {
        nodeStateMap.put( node, VISITING );

        final List verticies = node.getChildren();

        for ( final Iterator iter = verticies.iterator(); iter.hasNext(); )
        {
            final Node v = (Node) iter.next();

            if ( isNotVisited( v, nodeStateMap ) )
            {
                visitDepthFirstSearch( v, nodeStateMap, list );
            }
        }

        nodeStateMap.put( node, VISITED );

        list.add( node.getLabel() );
    }

}
