package org.codehaus.plexus.graph.visualization.touchgraph;

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
import org.codehaus.plexus.graph.Named;
import org.codehaus.plexus.graph.Vertex;
import org.codehaus.plexus.graph.WeightedGraph;
import org.codehaus.plexus.graph.visualization.StaticGraphVisualizer;
import org.codehaus.plexus.graph.visualization.VisualizationException;
import org.codehaus.plexus.graph.visualization.decorators.DecoratedGraph;
import org.codehaus.plexus.graph.visualization.decorators.DecoratedVertex;
import org.codehaus.plexus.graph.visualization.decorators.VertexDecorator;
import org.codehaus.plexus.graph.visualization.util.ColorUtil;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * TouchgraphStaticGraphVisualizer - generate TouchGraph suitable XML.
 * 
 * See http://www.touchgraph.com/
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class TouchgraphStaticGraphVisualizer extends AbstractLogEnabled implements StaticGraphVisualizer
{
    private static List outputFormats;

    static
    {
        outputFormats = new ArrayList();
        outputFormats.add( "xml" );
    }

    private Random random = new Random();

    public String[] getOutputFormats()
    {
        return (String[]) outputFormats.toArray();
    }

    public boolean supportsOutputFormat( String format )
    {
        return outputFormats.contains( format );
    }

    public void render( DecoratedGraph graph, File outputFile ) throws IOException, VisualizationException
    {
        render( graph, outputFile, null );
    }

    public void render( DecoratedGraph graph, File outputFile, Dimension maxSize )
        throws IOException, VisualizationException
    {
        if ( !( graph instanceof DirectedGraph ) )
        {
            throw new VisualizationException( this.getClass().getName()
                            + " only supports DecoratedGraphs which implements DirectedGraph." );
        }

        FileOutputStream os = new FileOutputStream( outputFile );
        PrintWriter pw = new PrintWriter( os );
        pw.println( "<?xml version=\"1.0\"?>" );
        pw.println( "<TOUCHGRAPH_LB version=\"1.20\">" );
        writeNodeset( pw, (DirectedGraph) graph );
        writeEdgeset( pw, (DirectedGraph) graph );
        pw.println( "</TOUCHGRAPH_LB>" );
        pw.flush();
    }

    protected void writeNodeset( PrintWriter pw, DirectedGraph graph )
    {
        pw.println( "<NODESET>" );
        Iterator vertices = graph.getVertices().iterator();
        while ( vertices.hasNext() )
        {
            Vertex v = (Vertex) vertices.next();

            pw.println( "<NODE nodeID=\"" + v.toString() + "\">" );
            pw.println( "<NODE_LOCATION x=\"" + random.nextInt( 200 ) + "\" y = \"" + random.nextInt( 200 )
                            + "\" visible=\"true\" />" );

            String label;
            if ( v instanceof Named )
            {
                label = ( (Named) v ).getName();
            }
            else
            {
                label = v.toString();
            }

            pw.print( "<NODE_LABEL label=\"" + label + "\" " + "shape=\"2\"" );

            if ( v instanceof DecoratedVertex )
            {
                VertexDecorator decorator = ( (DecoratedVertex) v ).getDecorator();

                if ( decorator != null )
                {

                    if ( decorator.getBackgroundColor() != null )
                    {
                        pw.print( " backColor=\"" + ColorUtil.toCssDeclaration( decorator.getBackgroundColor() )
                                        + "\" " );
                    }

                    if ( decorator.getLabelColor() != null )
                    {
                        pw.print( " textColor=\"" + ColorUtil.toCssDeclaration( decorator.getLabelColor() ) + "\" " );
                    }

                    pw.print( " fontSize=\"" + decorator.getFontSize() + "\" " );
                }
            }

            pw.println( "/>" );

            pw.println( "</NODE>" );
        }

        pw.println( "</NODESET>" );
    }

    protected void writeEdgeset( PrintWriter pw, DirectedGraph graph )
    {
        pw.println( "<EDGESET>" );

        Iterator edges = graph.getEdges().iterator();
        while ( edges.hasNext() )
        {
            Edge next = (Edge) edges.next();

            pw.println( "<EDGE fromID=\"" + graph.getSource( next ) + "\" " + "toID=\"" + graph.getTarget( next )
                            + "\" " + "type=\"2\" " + "visible=\"true\"" );

            if ( graph instanceof WeightedGraph )
            {
                int length = new Double( ( (WeightedGraph) graph ).getWeight( next ) ).intValue();
                pw.print( " length=\"" + length + "\"" );
            }

            pw.println( "/>" );

        }
        pw.println( "</EDGESET>" );
    }

}
