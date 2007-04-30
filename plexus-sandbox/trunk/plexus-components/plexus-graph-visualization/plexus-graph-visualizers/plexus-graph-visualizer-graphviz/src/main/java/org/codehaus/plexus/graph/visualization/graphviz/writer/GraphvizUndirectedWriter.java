package org.codehaus.plexus.graph.visualization.graphviz.writer;

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
import org.codehaus.plexus.graph.UndirectedGraph;
import org.codehaus.plexus.graph.Vertex;
import org.codehaus.plexus.graph.visualization.VisualizationException;
import org.codehaus.plexus.graph.visualization.decorators.DecoratedGraph;
import org.codehaus.plexus.graph.visualization.decorators.GraphDecorator;
import org.codehaus.plexus.graph.visualization.util.VertexUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

/**
 * GraphvizUndirectedWriter - for writing undirected dot graphs. 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class GraphvizUndirectedWriter extends AbstractGraphvizDotWriter implements GraphvizDotWriter
{

    public void writeDot( DecoratedGraph graph, File outputFile ) throws IOException, VisualizationException
    {
        if ( !( graph instanceof UndirectedGraph ) )
        {
            throw new IllegalStateException( this.getClass().getName()
                            + " can only handle DecoratedGraph's that implement UndirectedGraph." );
        }

        FileWriter fwriter = new FileWriter( outputFile );
        PrintWriter dot = new PrintWriter( fwriter );

        GraphDecorator graphDecorator = graph.getDecorator();

        String graphId = "gid"; // default graph id.

        if ( StringUtils.isNotEmpty( graphDecorator.getTitle() ) )
        {
            graphId = VertexUtils.toId( graphId );
        }

        dot.println( "// Auto generated dot file from plexus-graph-visualizer-graphviz." );

        dot.println( "graph " + graphId + " {" );

        dot.println( "" );

        prepareDefaults( graph, dot );

        Iterator it;

        it = graph.getVertices().iterator();
        while ( it.hasNext() )
        {
            Vertex vertex = (Vertex) it.next();

            writeVertex( dot, vertex );
        }

        it = graph.getEdges().iterator();
        while ( it.hasNext() )
        {
            Edge edge = (Edge) it.next();

            Set verticies = graph.getVertices( edge );

            if ( verticies.size() != 2 )
            {
                // TODO: dump edge / verticies information in more human readable form.
                throw new VisualizationException( "Graphviz does not support HyperEdges. Edge [" + edge
                                + "], Verticies [" + verticies + "]" );
            }

            Iterator itVerticies = verticies.iterator();

            Vertex from = (Vertex) itVerticies.next();
            Vertex to = (Vertex) itVerticies.next();

            writeEdge( dot, edge, from, to );
        }

        dot.println( "}" );

        dot.flush();
        fwriter.flush();
    }
}
