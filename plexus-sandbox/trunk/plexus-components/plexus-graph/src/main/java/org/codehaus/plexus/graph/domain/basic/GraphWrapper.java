package org.codehaus.plexus.graph.domain.basic;

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
 * GraphWrapper This is a superclass of all Wrapper implementations. It
 * basically does a redirection to the graph.
 */

import org.codehaus.plexus.graph.Edge;
import org.codehaus.plexus.graph.Graph;
import org.codehaus.plexus.graph.Vertex;

import java.util.Set;

/** Description of the Class */
public class GraphWrapper
{
    private Graph impl = null;

    /**
     * Constructor for the GraphWrapper object
     *
     * @param impl
     */
    public GraphWrapper( Graph impl )
    {
        this.impl = impl;
    }

    /** Constructor for the GraphWrapper object */
    public GraphWrapper()
    {
    }

    /** Sets the graph attribute of the GraphWrapper object */
    public void setGraph( Graph impl )
    {
        this.impl = impl;
    }

    // Graph Implementation. . .
    /** Gets the vertices attribute of the GraphWrapper object */
    public Set getVertices()
    {
        return impl.getVertices();
    }

    /** Gets the edges attribute of the GraphWrapper object */
    public Set getEdges()
    {
        return impl.getEdges();
    }

    /** Gets the vertices attribute of the GraphWrapper object */
    public Set getVertices( Edge e )
    {
        return impl.getVertices( e );
    }

    /** Gets the edges attribute of the GraphWrapper object */
    public Set getEdges( Vertex v )
    {
        return impl.getEdges( v );
    }
}
