package org.codehaus.plexus.graphing.model.dag;

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
import org.codehaus.plexus.graphing.model.GraphConstraintException;
import org.codehaus.plexus.graphing.model.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * DagTest 
 *
 * @author <a href="michal.maczka@dimatics.com">Michal Maczka</a>
 * @version $Id$
 */
public class DagTest
    extends PlexusTestCase
{
    public void testDag()
        throws GraphConstraintException
    {
        final Dag dag = new Dag();

        // Simple node add
        dag.addNode( "a" );

        assertEquals( 1, dag.getNodes().size() );

        assertEquals( "a", dag.getNode( "a" ).getLabel() );

        // Add the same node
        dag.addNode( "a" );

        assertEquals( 1, dag.getNodes().size() );

        assertEquals( "a", dag.getNode( "a" ).getLabel() );

        // Add a new node
        dag.addNode( "b" );

        assertEquals( 2, dag.getNodes().size() );

        // No edges (yet).
        assertFalse( dag.hasEdge( "a", "b" ) );

        assertFalse( dag.hasEdge( "b", "a" ) );

        // Get the nodes
        final Node a = dag.getNode( "a" );

        final Node b = dag.getNode( "b" );

        assertEquals( "a", a.getLabel() );

        assertEquals( "b", b.getLabel() );

        // Create the first edge
        dag.addEdge( "a", "b" );

        assertTrue( a.getChildren().contains( b ) );

        assertTrue( b.getParents().contains( a ) );

        assertTrue( dag.hasEdge( "a", "b" ) );

        assertFalse( dag.hasEdge( "b", "a" ) );
        
        // Create a new edge (with lazy node creation)

        dag.addEdge( "c", "d" );

        assertEquals( 4, dag.getNodes().size() );

        final Node c = dag.getNode( "c" );

        final Node d = dag.getNode( "d" );

        assertEquals( "a", a.getLabel() );

        assertEquals( "b", b.getLabel() );

        assertEquals( "c", c.getLabel() );

        assertEquals( "d", d.getLabel() );

        assertFalse( dag.hasEdge( "b", "a" ) );

        assertFalse( dag.hasEdge( "a", "c" ) );

        assertFalse( dag.hasEdge( "a", "d" ) );

        assertTrue( dag.hasEdge( "c", "d" ) );

        assertFalse( dag.hasEdge( "d", "c" ) );

        // The labels
        final Set labels = dag.getNodeLabels();

        assertEquals( 4, labels.size() );

        assertTrue( labels.contains( "a" ) );

        assertTrue( labels.contains( "b" ) );

        assertTrue( labels.contains( "c" ) );

        assertTrue( labels.contains( "d" ) );

        dag.addEdge( "a", "d" );

        assertTrue( a.getChildren().contains( d ) );

        assertTrue( d.getParents().contains( a ) );

        // "b" and "d" are children of "a"
        assertEquals( 2, a.getChildren().size() );

        assertTrue( a.getChildLabels().contains( "b" ) );

        assertTrue( a.getChildLabels().contains( "d" ) );

        // "a" and "c" are parents of "d"
        assertEquals( 2, d.getParents().size() );

        assertTrue( d.getParentLabels().contains( "a" ) );

        assertTrue( d.getParentLabels().contains( "c" ) );
    }

    public void testGetPredessors()
        throws GraphConstraintException
    {
        Dag dag = new Dag();

        //  a --> b --> c --> e
        //        |     |     |
        //        |     v     v
        //        +---> d <-- f --> g
        //
        // result d, g, f, c, b, a
        
        dag.addEdge( "a", "b" );

        //force order of nodes

        dag.addNode( "c" );

        dag.addNode( "d" );

        dag.addEdge( "a", "b" );

        dag.addEdge( "b", "c" );

        dag.addEdge( "b", "d" );

        dag.addEdge( "c", "d" );

        dag.addEdge( "c", "e" );

        dag.addEdge( "f", "d" );

        dag.addEdge( "e", "f" );

        dag.addEdge( "f", "g" );

        final List actual = dag.getSuccessorLabels( "b" );

        final List expected = new ArrayList();

        expected.add( "d" );

        expected.add( "g" );

        expected.add( "f" );

        expected.add( "e" );

        expected.add( "c" );

        expected.add( "b" );

        assertEquals( expected, actual );
    }

}
