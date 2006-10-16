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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="michal.maczka@dimatics.com">Michal Maczka</a>
 * @version $Id$
 */
public class CycleDetector
{

    private final static Integer NOT_VISTITED = new Integer( 0 );

    private final static Integer VISITING = new Integer( 1 );

    private final static Integer VISITED = new Integer( 2 );

    public static List hasCycle( final Dag graph )
    {
        final Collection nodes = graph.getNodes();

        final Map nodeStateMap = new HashMap();

        List retValue = null;

        for ( final Iterator iter = nodes.iterator(); iter.hasNext(); )
        {
            final Node node = (Node) iter.next();

            if ( isNotVisited( node, nodeStateMap ) )
            {
                retValue = introducesCycle( node, nodeStateMap );

                if ( retValue != null )
                {
                    break;
                }
            }
        }

        return retValue;

    }

    /**
     * This method will be called when an egde leading to given node was added
     * and we want to check if introduction of this edge has not resulted
     * in apparition of cycle in the graph
     *
     * @param node
     * @param nodeStateMap
     * @return
     */
    public static List introducesCycle( final Node node, final Map nodeStateMap )
    {
        final LinkedList cycleStack = new LinkedList();

        final boolean hasCycle = dfsVisit( node, cycleStack, nodeStateMap );

        if ( hasCycle )
        {
            // we have a situation like: [b, a, c, d, b, f, g, h].
            // Label of Node which introduced  the cycle is at the first position in the list
            // We have to find second occurence of this label and use its position in the list
            // for getting the sublist of node labels of cycle paricipants
            //
            // So in our case we are seraching for [b, a, c, d, b]
            final String label = (String) cycleStack.getFirst();

            final int pos = cycleStack.lastIndexOf( label );

            final List cycle = cycleStack.subList( 0, pos + 1 );

            Collections.reverse( cycle );

            return cycle;
        }

        return null;
    }

    public static List introducesCycle( final Node node )
    {

        final Map nodeStateMap = new HashMap();

        return introducesCycle( node, nodeStateMap );

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

    /**
     * @param node
     * @param nodeStateMap
     * @return
     */
    private static boolean isVisiting( final Node node, final Map nodeStateMap )
    {
        final Integer state = (Integer) nodeStateMap.get( node );

        return VISITING.equals( state );
    }

    private static boolean dfsVisit( final Node node, final LinkedList cycle, final Map nodeStateMap )
    {
        cycle.addFirst( node.getLabel() );

        nodeStateMap.put( node, VISITING );

        final List nodes = node.getChildren();

        for ( final Iterator iter = nodes.iterator(); iter.hasNext(); )
        {
            final Node v = (Node) iter.next();

            if ( isNotVisited( v, nodeStateMap ) )
            {
                final boolean hasCycle = dfsVisit( v, cycle, nodeStateMap );

                if ( hasCycle )
                {
                    return true;
                }
            }
            else if ( isVisiting( v, nodeStateMap ) )
            {
                cycle.addFirst( v.getLabel() );

                return true;
            }
        }
        nodeStateMap.put( node, VISITED );

        cycle.removeFirst();

        return false;
    }
}