package org.codehaus.plexus.graph.visualization.wrappers;

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
import org.codehaus.plexus.graph.domain.basic.DirectedGraphWrapper;
import org.codehaus.plexus.graph.visualization.decorators.DecoratedGraph;
import org.codehaus.plexus.graph.visualization.decorators.GraphDecorator;

import java.util.Set;

/**
 * DecoratedDirectedGraphWrapper 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class DecoratedDirectedGraphWrapper extends DirectedGraphWrapper implements DecoratedGraph
{
    private GraphDecorator decorator;

    public DecoratedDirectedGraphWrapper()
    {
        super();
    }

    public DecoratedDirectedGraphWrapper( DirectedGraph graph )
    {
        super( graph );
    }

    public Set getInbound( Vertex v )
    {
        return super.getInbound( v );
    }

    public Set getOutbound( Vertex v )
    {
        return super.getOutbound( v );
    }

    public Vertex getSource( Edge e )
    {
        return super.getSource( e );
    }

    public Vertex getTarget( Edge e )
    {
        return super.getTarget( e );
    }

    public void setDirGraph( DirectedGraph graph )
    {
        super.setDirGraph( graph );
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
