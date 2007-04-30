package org.codehaus.plexus.graph.visualization;

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
import org.codehaus.plexus.graph.WeightedGraph;
import org.codehaus.plexus.graph.domain.basic.DefaultDirectedGraph;
import org.codehaus.plexus.graph.visualization.decorators.DecoratedGraph;
import org.codehaus.plexus.graph.visualization.decorators.GraphDecorator;

import java.util.Iterator;

/**
 * DecoratedDirectedGraph 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class DecoratedDirectedGraph extends DefaultDirectedGraph implements DirectedGraph, DecoratedGraph
{
    private GraphDecorator decorator;
    
    public DecoratedDirectedGraph()
    {
        super();
    }

    /**
     * Copy Constructor
     * 
     * @param dg the decorated directed graph to copy
     */
    public DecoratedDirectedGraph( DecoratedDirectedGraph dg )
    {
        Iterator v = dg.getVertices().iterator();
        while ( v.hasNext() )
        {
            addVertex( (Vertex) v.next() );
        }

        Iterator e = dg.getEdges().iterator();
        while ( e.hasNext() )
        {
            Edge edge = (Edge) e.next();
            addEdge( edge, dg.getSource( edge ), dg.getTarget( edge ) );

            if ( dg instanceof WeightedGraph )
            {
                setWeight( edge, ( (WeightedGraph) dg ).getWeight( edge ) );
            }
        }
    }
    
    public GraphDecorator getDecorator()
    {
        return this.decorator;
    }

    public void setDecorator( GraphDecorator decorator )
    {
        this.decorator = decorator;
    }
}
