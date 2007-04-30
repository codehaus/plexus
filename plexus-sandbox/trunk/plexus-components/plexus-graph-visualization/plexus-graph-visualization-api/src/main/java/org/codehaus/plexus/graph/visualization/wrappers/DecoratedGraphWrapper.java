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

import org.codehaus.plexus.graph.Edge;
import org.codehaus.plexus.graph.Graph;
import org.codehaus.plexus.graph.Vertex;
import org.codehaus.plexus.graph.domain.basic.GraphWrapper;
import org.codehaus.plexus.graph.visualization.decorators.DecoratedGraph;
import org.codehaus.plexus.graph.visualization.decorators.GraphDecorator;

import java.util.Set;

/**
 * DecoratedGraphWrapper 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class DecoratedGraphWrapper extends GraphWrapper implements DecoratedGraph
{
    private GraphDecorator decorator;

    public DecoratedGraphWrapper()
    {
    }

    public DecoratedGraphWrapper( Graph impl )
    {
        super( impl );
    }

    public Set getEdges()
    {
        return super.getEdges();
    }

    public Set getEdges( Vertex v )
    {
        return super.getEdges( v );
    }

    public Set getVertices()
    {
        return super.getVertices();
    }

    public Set getVertices( Edge e )
    {
        return super.getVertices( e );
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
