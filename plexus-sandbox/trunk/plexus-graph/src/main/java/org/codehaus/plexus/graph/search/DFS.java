package org.codehaus.plexus.graph.search;

/*
 * Licensed to the Codehaus Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * This class does a Depth First Search. It visits all of the children nodes
 * before moving to the siblling nodes.
 */

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.codehaus.plexus.graph.*;

/** Description of the Class */
public class DFS
{
    private Map colors = new HashMap();// VERTEX X COLOR
    /** Description of the Field */
    public final static String WHITE = "white";
    /** Description of the Field */
    public final static String BLACK = "black";
    /** Description of the Field */
    public final static String GRAY = "gray";

    /** Constructor for the DFS object */
    public DFS()
    {
    }

    /** Gets the color attribute of the DFS object */
    public String getColor( Vertex v )
    {
        return (String) colors.get( v );
    }


    /** Description of the Method */
    private void visitEdge( DirectedGraph graph,
                            Edge e,
                            Visitor visitor )
    {
        visitor.discoverEdge( e );

        Vertex v = graph.getTarget( e );

        if ( colors.get( v ) == WHITE )
        {
            visitVertex( graph, v, visitor );
        }

        visitor.finishEdge( e );
    }

    /** Description of the Method */
    private void visitVertex( DirectedGraph graph,
                              Vertex v,
                              Visitor visitor )
    {
        colors.remove( v );
        colors.put( v, GRAY );

        visitor.discoverVertex( v );

        Iterator edges = graph.getOutbound( v ).iterator();
        while ( edges.hasNext() )
        {
            Edge e = (Edge) edges.next();
            visitEdge( graph, e, visitor );
        }

        visitor.finishVertex( v );

        colors.remove( v );
        colors.put( v, BLACK );
    }

    /** visit - Visits the graph */
    public void visit( DirectedGraph graph,
                       Vertex root,
                       Visitor visitor )
    {
        Iterator vertices = graph.getVertices().iterator();
        while ( vertices.hasNext() )
        {
            colors.put( vertices.next(), WHITE );
        }

        visitor.discoverGraph( graph );

        visitVertex( graph, root, visitor );

        visitor.finishGraph( graph );
    }

}

