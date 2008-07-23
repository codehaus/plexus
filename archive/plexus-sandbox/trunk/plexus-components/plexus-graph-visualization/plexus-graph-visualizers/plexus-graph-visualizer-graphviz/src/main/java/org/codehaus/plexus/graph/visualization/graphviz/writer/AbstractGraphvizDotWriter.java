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
import org.codehaus.plexus.graph.Vertex;
import org.codehaus.plexus.graph.visualization.VisualizationException;
import org.codehaus.plexus.graph.visualization.decorators.DecoratedEdge;
import org.codehaus.plexus.graph.visualization.decorators.DecoratedGraph;
import org.codehaus.plexus.graph.visualization.decorators.DecoratedVertex;
import org.codehaus.plexus.graph.visualization.decorators.EdgeDecorator;
import org.codehaus.plexus.graph.visualization.decorators.GraphDecorator;
import org.codehaus.plexus.graph.visualization.decorators.VertexDecorator;
import org.codehaus.plexus.graph.visualization.util.ColorUtil;
import org.codehaus.plexus.graph.visualization.util.VertexUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.PrintWriter;

/**
 * AbstractGraphvizDotWriter
 * 
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public abstract class AbstractGraphvizDotWriter implements GraphvizDotWriter
{
    protected String getLineEndingName( int lineending )
    {
        switch ( lineending )
        {
            case EdgeDecorator.ARROW:
                return "normal";

            case EdgeDecorator.DOT:
                return "dot";

            case EdgeDecorator.HOLLOW_DOT:
                return "odot";

            case EdgeDecorator.INVERT_ARROW:
                return "inv";

            case EdgeDecorator.INVERT_ARROW_DOT:
                return "invdot";

            case EdgeDecorator.INVERT_ARROW_HOLLOW_DOT:
                return "invodot";

            case EdgeDecorator.NONE:
            default:
                return "none";
        }
    }

    protected void prepareDefaults( DecoratedGraph graph, PrintWriter dot )
    {
        // Graph Defaults.

        GraphDecorator decorator = graph.getDecorator();

        dot.println( "  // Graph Defaults" );
        dot.println( "  graph [" );

        if ( decorator.getBackgroundColor() != null )
        {
            dot.println( "    bgcolor=\"" + ColorUtil.toCssDeclaration( decorator.getBackgroundColor() ) + "\"," );
        }

        if ( StringUtils.isNotEmpty( decorator.getTitle() ) )
        {
            dot.println( "    fontname=\"Helvetica\"," );
            dot.println( "    fontsize=\"" + decorator.getFontSize() + "\"," );
            dot.println( "    label=\"" + StringUtils.escape( decorator.getTitle() ) + "\"," );
            dot.println( "    labeljust=\"l\"" );
        }

        if ( decorator.getTitleColor() != null )
        {
            dot.println( "    fontcolor=\"" + ColorUtil.toCssDeclaration( decorator.getTitleColor() ) + "\"," );
        }

        switch ( decorator.getOrientation() )
        {
            case GraphDecorator.LEFT_TO_RIGHT:
                dot.println( "    rankdir=\"LR\"" );
                break;
            case GraphDecorator.TOP_TO_BOTTOM:
            default:
                dot.println( "    rankdir=\"TB\"" );
                break;
        }

        dot.println( "  ];" );

        // Node Defaults.

        VertexDecorator nodeDecorator = new VertexDecorator();

        dot.println( "" );
        dot.println( "  // Node Defaults." );
        dot.println( "  node [" );
        dot.println( "    fontname=\"Helvetica\"," );
        dot.println( "    fontsize=\"" + nodeDecorator.getFontSize() + "\"," );
        dot.println( "    shape=\"box\"" );
        dot.println( "  ];" );

        // Edge Defaults.

        VertexDecorator edgeDecorator = new VertexDecorator();

        dot.println( "" );
        dot.println( "  // Edge Defaults." );
        dot.println( "  edge [" );
        dot.println( "    arrowsize=\"0.8\"" );
        dot.println( "    fontsize=\"" + edgeDecorator.getFontSize() + "\"," );
        dot.println( "  ];" );
    }

    protected void writeVertex( PrintWriter dot, Vertex vertex ) throws VisualizationException
    {
        String label = VertexUtils.getLabel( vertex );

        if ( StringUtils.isEmpty( label ) )
        {
            throw new VisualizationException( "Vertex (name||toString) cannot be empty." );
        }

        dot.println( "" );
        dot.println( "  // Node" );
        dot.println( "  \"" + VertexUtils.toId( vertex ) + "\" [" );

        if ( StringUtils.isNotEmpty( label ) )
        {
            dot.println( "    label=\"" + StringUtils.escape( label ) + "\"," );
        }

        if ( vertex instanceof DecoratedVertex )
        {
            VertexDecorator decorator = ( (DecoratedVertex) vertex ).getDecorator();

            if ( decorator != null )
            {
                if ( decorator.getBorderColor() != null )
                {
                    dot.println( "    color=\"" + ColorUtil.toCssDeclaration( decorator.getBorderColor() ) + "\"," );
                }
                if ( decorator.getBackgroundColor() != null )
                {
                    dot.println( "    style=filled," );
                    dot.println( "    fillcolor=\"" + ColorUtil.toCssDeclaration( decorator.getBackgroundColor() )
                                    + "\"," );
                }
                if ( decorator.getLabelColor() != null )
                {
                    dot.println( "    fontcolor=\"" + ColorUtil.toCssDeclaration( decorator.getLabelColor() ) + "\"," );
                }
                if ( decorator.getFontSize() > 0 )
                {
                    dot.println( "    fontsize=\"" + decorator.getFontSize() + "\"," );
                }

                if ( StringUtils.isNotEmpty( decorator.getGroupName() ) )
                {
                    dot.println( "    group=\"" + StringUtils.escape( decorator.getGroupName() ) + "\"," );
                }
            }
        }

        dot.println( "    shape=box" );
        dot.println( "  ];" );
    }

    protected void writeEdge( PrintWriter dot, Edge edge, Vertex from, Vertex to )
    {
        dot.println( "" );
        dot.println( "  // Edge" );

        dot.println( "  \"" + VertexUtils.toId( from ) + "\" -> \"" + VertexUtils.toId( to ) + "\" [" );

        if ( edge instanceof DecoratedEdge )
        {

            EdgeDecorator decorator = ( (DecoratedEdge) edge ).getDecorator();
            if ( decorator != null )
            {
                switch ( decorator.getStyle() )
                {
                    case EdgeDecorator.BOLD:
                        dot.println( "    style=\"bold\"," );
                        break;
                    case EdgeDecorator.DASHED:
                        dot.println( "    style=\"dotted\"," );
                        break;
                }

                if ( decorator.getLineColor() != null )
                {
                    dot.println( "    color=\"" + ColorUtil.toCssDeclaration( decorator.getLineColor() ) + "\"," );
                }

                if ( StringUtils.isNotEmpty( decorator.getLineLabel() ) )
                {
                    dot.println( "    label=\"" + StringUtils.escape( decorator.getLineLabel() ) + "\"," );
                    dot.println( "    fontname=\"Helvetica\"," );
                    if ( decorator.getFontSize() > 0 )
                    {
                        dot.println( "    fontsize=\"" + decorator.getFontSize() + "\"," );
                    }
                }

                dot.println( "    arrowtail=" + getLineEndingName( decorator.getLineTail() ) + "," );
                dot.println( "    arrowhead=" + getLineEndingName( decorator.getLineHead() ) );
            }
        }

        dot.println( "  ];" );
    }
}
