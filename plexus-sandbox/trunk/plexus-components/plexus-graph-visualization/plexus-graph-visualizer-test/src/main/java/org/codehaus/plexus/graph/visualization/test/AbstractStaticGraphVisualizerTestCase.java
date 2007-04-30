package org.codehaus.plexus.graph.visualization.test;

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

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.graph.Edge;
import org.codehaus.plexus.graph.Vertex;
import org.codehaus.plexus.graph.visualization.StaticGraphVisualizer;
import org.codehaus.plexus.graph.visualization.decorators.DecoratedEdge;
import org.codehaus.plexus.graph.visualization.decorators.DecoratedVertex;
import org.codehaus.plexus.graph.visualization.decorators.EdgeDecorator;
import org.codehaus.plexus.graph.visualization.decorators.GraphDecorator;
import org.codehaus.plexus.graph.visualization.decorators.VertexDecorator;
import org.codehaus.plexus.graph.visualization.util.VertexUtils;

import java.awt.Color;
import java.io.File;

/**
 * AbstractVisualizerTestCase - basic tests for the visualizers.
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public abstract class AbstractStaticGraphVisualizerTestCase extends PlexusTestCase
{
    private StaticGraphVisualizer visualizer;

    public abstract String getVisualizerHint();

    protected void setUp() throws Exception
    {
        super.setUp();

        visualizer = (StaticGraphVisualizer) lookup( StaticGraphVisualizer.ROLE, getVisualizerHint() );
    }

    public void testDirectedGraphSimple() throws Exception
    {
        TestableDirectedGraph model = new TestableDirectedGraph();

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

        visualizer.render( model, outputFile );

        /* Override this test and write your own validation routine on the output files.
         * 
         * assertTrue( outputFile.exists() );
         * assertTrue( outputFile.isFile() );
         */
    }

    public void testDirectedGraphFancy() throws Exception
    {
        TestableDirectedGraph model = new TestableDirectedGraph();

        GraphDecorator decorator = new GraphDecorator();

        model.setDecorator( decorator );

        Edge edge;

        model.addEdge( "main", "parse" );
        model.addEdge( "parse", "execute" );

        edge = model.addEdge( "main", "init" );
        if ( edge instanceof DecoratedEdge )
        {
            DecoratedEdge dedge = (DecoratedEdge) edge;
            dedge.setDecorator( new EdgeDecorator() );
            dedge.getDecorator().setStyle( EdgeDecorator.DASHED );
            dedge.getDecorator().setLineColor( Color.CYAN );
        }
        else
        {
            fail( "Got edge [" + edge + "] that does not implement " + DecoratedEdge.class.getName() );
        }

        model.addEdge( "main", "cleanup" );

        Vertex makeString = model.addVertex( "make a \nstring" );

        model.addEdge( "execute", VertexUtils.getLabel( makeString ) );
        model.addEdge( "execute", "printf" );
        model.addEdge( "init", VertexUtils.getLabel( makeString ) );

        edge = model.addEdge( "main", "printf" );
        if ( edge instanceof DecoratedEdge )
        {
            DecoratedEdge dedge = (DecoratedEdge) edge;
            dedge.setDecorator( new EdgeDecorator() );
            dedge.getDecorator().setStyle( EdgeDecorator.BOLD );
            dedge.getDecorator().setLineLabel( "100 times" );
        }
        else
        {
            fail( "Got edge [" + edge + "] that does not implement " + DecoratedEdge.class.getName() );
        }

        Vertex compare = model.addVertex( "compare" );

        if ( compare instanceof DecoratedVertex )
        {
            DecoratedVertex dcompare = (DecoratedVertex) compare;
            dcompare.setDecorator( new VertexDecorator() );

            Color purple = new Color( 0.7f, 0.3f, 1.0f );

            dcompare.getDecorator().setBackgroundColor( purple );
            dcompare.getDecorator().setBorderColor( purple );
            dcompare.getDecorator().setLabelColor( Color.WHITE );
        }
        else
        {
            fail( "Got vertex [" + compare + "] that does not implement " + DecoratedVertex.class.getName() );
        }

        edge = model.addEdge( "execute", "compare" );
        
        if ( edge instanceof DecoratedEdge )
        {
            DecoratedEdge dedge = (DecoratedEdge) edge;
            dedge.setDecorator( new EdgeDecorator() );
            dedge.getDecorator().setLineColor( Color.RED );
        }
        else
        {
            fail( "Got edge [" + edge + "] that does not implement " + DecoratedEdge.class.getName() );
        }

        File outputFile = new File( "target/graph/fancy.png" );

        visualizer.render( model, outputFile );

        /* Override this test and write your own validation routine on the output files.
         * 
         * assertTrue( outputFile.exists() );
         * assertTrue( outputFile.isFile() );
         */
    }

    public void assertHasGraphFile( String name )
    {
        File graphFile = new File("target/graph/" + name);
        
        assertTrue( "Exists: " + graphFile.getAbsolutePath(), graphFile.exists() );
        assertTrue( "Is File: " + graphFile.getAbsolutePath(), graphFile.isFile() );
        assertTrue( "Length > 100 bytes: " + graphFile.getAbsolutePath(), graphFile.length() > 100 );
    }
}
