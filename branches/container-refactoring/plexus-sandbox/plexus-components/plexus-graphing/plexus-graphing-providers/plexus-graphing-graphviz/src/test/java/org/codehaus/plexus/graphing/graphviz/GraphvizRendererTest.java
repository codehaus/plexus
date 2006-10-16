package org.codehaus.plexus.graphing.graphviz;

/*
 * Copyright 2001-2006 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.graphing.GraphRenderer;
import org.codehaus.plexus.graphing.decorators.EdgeDecorator;
import org.codehaus.plexus.graphing.decorators.GraphDecorator;
import org.codehaus.plexus.graphing.decorators.NodeDecorator;
import org.codehaus.plexus.graphing.model.Edge;
import org.codehaus.plexus.graphing.model.Graph;
import org.codehaus.plexus.graphing.model.Node;

import java.awt.Color;
import java.io.File;

/**
 * GraphvizRendererTest 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class GraphvizRendererTest
    extends PlexusTestCase
{
    private GraphRenderer getRenderer()
        throws Exception
    {
        return (GraphRenderer) lookup( GraphRenderer.ROLE, "graphviz" );
    }

    public void testSimple()
        throws Exception
    {
        GraphRenderer renderer = getRenderer();

        Graph model = new Graph();
        GraphDecorator decorator = new GraphDecorator();
        model.setDecorator( decorator );

        decorator.setTitle( "symple" );

        model.addEdge( "main", "parse" );
        model.addEdge( "parse", "execute" );
        model.addEdge( "main", "init" );
        model.addEdge( "main", "cleanup" );
        model.addEdge( "execute", "make_string" );
        model.addEdge( "execute", "printf" );
        model.addEdge( "init", "make_string" );
        model.addEdge( "main", "printf" );
        model.addEdge( "execute", "compare" );

        File outputFile = new File( "target/graph/simple.png" );

        renderer.render( model, outputFile );

        assertTrue( outputFile.exists() );
        assertTrue( outputFile.isFile() );
    }

    public void testFancy()
        throws Exception
    {
        GraphRenderer renderer = getRenderer();

        Graph model = new Graph();
        GraphDecorator decorator = new GraphDecorator();
        model.setDecorator( decorator );

        Edge edge;

        model.addEdge( "main", "parse" );
        model.addEdge( "parse", "execute" );

        edge = model.addEdge( "main", "init" );
        edge.setDecorator( new EdgeDecorator() );
        edge.getDecorator().setStyle( EdgeDecorator.DASHED );
        edge.getDecorator().setLineColor( Color.CYAN );

        model.addEdge( "main", "cleanup" );

        Node makeString = model.addNode( "make a \nstring" );

        model.addEdge( "execute", makeString.getLabel() );
        model.addEdge( "execute", "printf" );
        model.addEdge( "init", makeString.getLabel() );

        edge = model.addEdge( "main", "printf" );
        edge.setDecorator( new EdgeDecorator() );
        edge.getDecorator().setStyle( EdgeDecorator.BOLD );
        edge.getDecorator().setLineLabel( "100 times" );

        Node compare = model.addNode( "compare" );
        compare.setDecorator( new NodeDecorator() );

        Color purple = new Color( 0.7f, 0.3f, 1.0f );

        compare.getDecorator().setBackgroundColor( purple );
        compare.getDecorator().setBorderColor( purple );
        compare.getDecorator().setLabelColor( Color.WHITE );

        edge = model.addEdge( "execute", "compare" );
        edge.setDecorator( new EdgeDecorator() );
        edge.getDecorator().setLineColor( Color.RED );

        File outputFile = new File( "target/graph/fancy.png" );

        renderer.render( model, outputFile );

        assertTrue( outputFile.exists() );
        assertTrue( outputFile.isFile() );
    }
}
