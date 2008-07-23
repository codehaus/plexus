package org.codehaus.plexus.graph.visualization.test;

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

import org.codehaus.plexus.graph.DirectedGraph;
import org.codehaus.plexus.graph.Edge;
import org.codehaus.plexus.graph.Vertex;
import org.codehaus.plexus.graph.exception.GraphException;
import org.codehaus.plexus.graph.visualization.DecoratedDirectedGraph;
import org.codehaus.plexus.graph.visualization.decorators.DecoratedGraph;

import java.util.HashMap;
import java.util.Map;

/**
 * TestableDirectedGraph 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class TestableDirectedGraph extends DecoratedDirectedGraph implements DirectedGraph, DecoratedGraph
{
    private Map vertexMap = new HashMap();

    private Vertex findVertex( String label )
    {
        Vertex ret = (Vertex) vertexMap.get( label );

        if ( ret == null )
        {
            ret = new TestableVertex( label );
            vertexMap.put( label, ret );
        }

        return ret;
    }

    public Vertex addVertex( String label )
    {
        Vertex vertex = findVertex( label );
        addVertex( vertex );
        return vertex;
    }

    public Edge addEdge( String labelStart, String labelEnd ) throws GraphException
    {
        Vertex source = findVertex( labelStart );
        Vertex target = findVertex( labelEnd );
        Edge edge = new TestableEdge();

        super.addEdge( edge, source, target );

        return edge;
    }

}
