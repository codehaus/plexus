package org.codehaus.plexus.graph.visualization.prefuse.graphml;

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
import org.codehaus.plexus.graph.visualization.VisualizationException;
import org.codehaus.plexus.graph.visualization.decorators.DecoratedGraph;
import org.codehaus.plexus.graph.visualization.util.VertexUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

/**
 * GraphMLWriter - write a graphml standard xml file of the graph.
 * 
 * See http://graphml.graphdrawing.org/
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class GraphMLWriter
{
    /**
     * Write a graphml standard xml file to the output file.
     * 
     * @param graph the graph to use as a data source.
     * @param outputFile the output file.
     * @throws IOException if there was a problem writing the file.
     * @throws VisualizationException if there was a problem parsing the graph.
     */
    public void write( DecoratedGraph graph, File outputFile ) throws IOException, VisualizationException
    {
        if ( !( graph instanceof DirectedGraph ) )
        {
            throw new VisualizationException( this.getClass().getName()
                            + " only supports DecoratedGraphs which implements DirectedGraph." );
        }

        Document doc = DocumentHelper.createDocument();
        // Setup Graph Document.
        Element root = doc.addElement( "graphml" );
        root.add( new Namespace( "xmlns", "http://graphml.graphdrawing.org/xmlns" ) );

        Element graphElem = root.addElement( "graph" );
        graphElem.addAttribute( "edgedefault", "directed" );

        // Populate Graph Document.
        addKeys( graphElem );
        addVerticies( graphElem, graph.getVertices() );
        addEdges( graphElem, (DirectedGraph) graph, graph.getEdges() );
        
        // Write Graph Document.
        OutputFormat format = OutputFormat.createPrettyPrint();
        FileOutputStream os = new FileOutputStream( outputFile );
        XMLWriter writer = new XMLWriter( os, format );
        writer.write( doc );
        
        writer.flush();
        writer.close();
    }

    private void addEdges( Element graphElem, DirectedGraph graph, Set edges )
    {
        Iterator it = edges.iterator();
        while ( it.hasNext() )
        {
            Edge edge = (Edge) it.next();
            Vertex source = graph.getSource( edge );
            Vertex target = graph.getTarget( edge );

            Element edgeElem = graphElem.addElement( "edge" );
            edgeElem.addAttribute( "source", VertexUtils.toId( source ) );
            edgeElem.addAttribute( "target", VertexUtils.toId( target ) );
        }
    }

    private void addVerticies( Element graphElem, Set vertices )
    {
        Iterator it = vertices.iterator();
        while ( it.hasNext() )
        {
            Vertex vertex = (Vertex) it.next();

            Element node = graphElem.addElement( "node" );
            node.addAttribute( "id", VertexUtils.toId( vertex ) );

            Element dataLabel = node.addElement( "data" );
            dataLabel.addAttribute( "key", "label" );
            dataLabel.setText( VertexUtils.getLabel( vertex ) );
        }
    }

    private void addKeys( Element graphElem )
    {
        Element key = graphElem.addElement( "key" );
        key.addAttribute( "id", "label" );
        key.addAttribute( "for", "node" );
        key.addAttribute( "attr.name", "label" );
        key.addAttribute( "attr.type", "string" );
    }
}
