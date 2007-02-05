package org.codehaus.plexus.graph.domain.dependency;

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
import org.codehaus.plexus.graph.algorithm.search.DFS;
import org.codehaus.plexus.graph.algorithm.search.Visitor;

import java.util.LinkedList;
import java.util.List;

public class DependencyVisitor
    implements Visitor
{
    private List deps = null;

    private DFS dfs = new DFS();

    public DependencyVisitor()
    {
    }

    public void discoverGraph( Graph g )
    {
    }

    public void discoverVertex( Vertex v )
    {
    }

    public void discoverEdge( Edge e )
    {
    }

    public void finishGraph( Graph g )
    {
    }

    /** Description of the Method */
    public void finishVertex( Vertex v )
    {
        if ( v instanceof DependencyVertex )
        {
            deps.add( ( (DependencyVertex) v ).getValue() );
        }
        else
        {
            deps.add( v );
        }
    }

    /** Description of the Method */
    public void finishEdge( Edge e )
    {
    }

    /** Gets the sortedDependencies attribute of the DependencyVisitor object */
    public synchronized List getSortedDependencies( DependencyGraph dg,
                                                    Vertex root )
    {
        deps = new LinkedList();

        dfs.visit( dg, root, this );

        return deps;
    }

    public synchronized List getSortedDependencies( DependencyGraph dg )
    {
        deps = new LinkedList();

        dfs.visit( dg, this );

        return deps;
    }
}

