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

import java.util.List;

/**
 * CycleDetectorTest 
 *
 * @author <a href="michal.maczka@dimatics.com">Michal Maczka</a>
 * @version $Id$
 */
public class CycleDetectorTest
    extends PlexusTestCase
{
    private void assertCycle( String[] expectedLabels, List cycle )
    {
        assertNotNull( "Cycle should not be null", cycle );

        assertFalse( "Cycle shold not be empty", cycle.isEmpty() );
        assertEquals( "Cycle count", expectedLabels.length, cycle.size() );

        for ( int i = 0; i < expectedLabels.length; i++ )
        {
            assertEquals( "Cycle[" + i + "]", expectedLabels[i], (String) cycle.get( i ) );
        }
    }

    public void testSimpleNoCycle()
        throws GraphConstraintException
    {
        // a --> b --> c

        try
        {
            final Dag dag1 = new Dag();

            dag1.addEdge( "a", "b" );

            dag1.addEdge( "b", "c" );

        }
        catch ( CycleDetectedException e )
        {
            fail( "Cycle should not be detected" );
        }
    }

    public void testSimpleCycle()
        throws GraphConstraintException
    {
        //  a --> b --> c
        //  ^           |
        //  |           |
        //  +-----------+

        Dag dag = new Dag();

        try
        {
            dag.addEdge( "a", "b" );

            dag.addEdge( "b", "c" );

            dag.addEdge( "c", "a" );

            fail( "Cycle should be detected" );

        }
        catch ( CycleDetectedException e )
        {
            assertCycle( new String[] { "c", "a", "b", "c" }, e.getCycle() );
            
            // Ensure that edge was removed
            assertFalse( "Edge 'c' -> 'a' should have been removed.", dag.hasEdge( "c", "a" ) );
        }
    }

    public void testSimpleSelfCycle()
        throws GraphConstraintException
    {
        //  a --> b <---+
        //        |     |
        //        |     |
        //        +-----+

        Dag dag = new Dag();

        try
        {
            dag.addEdge( "a", "b" );

            dag.addEdge( "b", "b" );

            fail( "Cycle should be detected" );
        }
        catch ( CycleDetectedException e )
        {
            assertCycle( new String[] { "b", "b" }, e.getCycle() );
            
            // Ensure that edge was removed
            assertFalse( "Edge 'b' -> 'b' should have been removed.", dag.hasEdge( "b", "b" ) );
        }
    }

    public void testComplexNoCycle()
        throws GraphConstraintException
    {
        //  a --> b --> c
        //  |     |
        //  |     v
        //  +---> d

        try
        {
            final Dag dag3 = new Dag();

            dag3.addEdge( "a", "b" );

            dag3.addEdge( "b", "c" );

            dag3.addEdge( "b", "d" );

            dag3.addEdge( "a", "d" );

        }
        catch ( CycleDetectedException e )
        {
            fail( "Cycle should not be detected" );
        }
    }

    public void testComplexWithShallowCycle()
        throws GraphConstraintException
    {
        //  +-----------+
        //  |           |
        //  v           |
        //  a --> b --> c
        //  |     |
        //  |     v
        //  +---> d

        Dag dag = new Dag();

        try
        {
            dag.addEdge( "a", "b" );

            dag.addEdge( "b", "c" );

            dag.addEdge( "b", "d" );

            dag.addEdge( "a", "d" );

            dag.addEdge( "c", "a" );

            fail( "Cycle should be detected" );

        }
        catch ( CycleDetectedException e )
        {
            assertCycle( new String[] { "c", "a", "b", "c" }, e.getCycle() );
            
            // Ensure that edge was removed
            assertFalse( "Edge 'c' -> 'a' should have been removed.", dag.hasEdge( "c", "a" ) );
        }
    }

    public void testComplexWithDeepCycle()
        throws GraphConstraintException
    {
        //        f --> g --> h
        //        |
        //        |
        //  a --> b --> c --> d
        //        ^           |
        //        |           v
        //        +---------- e

        final Dag dag = new Dag();

        try
        {

            dag.addEdge( "a", "b" );

            dag.addEdge( "b", "c" );

            dag.addEdge( "b", "f" );

            dag.addEdge( "f", "g" );

            dag.addEdge( "g", "h" );

            dag.addEdge( "c", "d" );

            dag.addEdge( "d", "e" );

            dag.addEdge( "e", "b" );

            fail( "Cycle should be detected" );

        }
        catch ( CycleDetectedException e )
        {
            assertCycle( new String[] { "e", "b", "c", "d", "e" }, e.getCycle() );
            
            // Ensure that edge was removed
            assertFalse( "Edge 'e' -> 'b' should have been removed.", dag.hasEdge( "e", "b" ) );
        }
    }
}
