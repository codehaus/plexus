package org.codehaus.plexus.graph;

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

import java.util.Set;

public interface Graph
{
    /** getVertices - Returns the total set of Vertices in the graph. */
    public Set getVertices();

    /** getEdges - Returns the total set of Edges in the graph. */
    public Set getEdges();

    /**
     * getEdges( Vertex ) - This method will return all edges which touch this
     * vertex.
     */
    public Set getEdges( Vertex v );

    /**
     * getVertices( Edge ) - This method will return the set of Verticies on
     * this Edge. (2 for normal edges, > 2 for HyperEdges.)
     */
    public Set getVertices( Edge e );
}

