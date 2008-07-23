package org.codehaus.plexus.graph.algorithm.search;
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

/**
 * @author dixon-pd
 *         <p/>
 *         This is a do-nothing implementation of the
 *         Visitor class.  Inherit from here if you would
 *         like to implement Visitor, but really have no
 *         need for some of the methods.  (Or if you have
 *         no need for another superclass. . .)
 */
public class VisitorAdapter
    implements Visitor
{

    public VisitorAdapter()
    {
    }

    /** @see org.codehaus.plexus.graph.algorithm.search.Visitor#discoverGraph(Graph) */
    public void discoverGraph( Graph graph )
    {
    }

    /** @see org.codehaus.plexus.graph.algorithm.search.Visitor#discoverVertex(Vertex) */
    public void discoverVertex( Vertex vertex )
    {
    }

    /** @see org.codehaus.plexus.graph.algorithm.search.Visitor#discoverEdge(Edge) */
    public void discoverEdge( Edge edge )
    {
    }

    /** @see org.codehaus.plexus.graph.algorithm.search.Visitor#finishEdge(Edge) */
    public void finishEdge( Edge edge )
    {
    }

    /** @see org.codehaus.plexus.graph.algorithm.search.Visitor#finishVertex(Vertex) */
    public void finishVertex( Vertex vertex )
    {
    }

    /** @see org.codehaus.plexus.graph.algorithm.search.Visitor#finishGraph(Graph) */
    public void finishGraph( Graph graph )
    {
    }

}
