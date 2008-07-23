package org.codehaus.plexus.graph;

/*
 * Copyright 2001,2004 The Apache Software Foundation.
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

/**
 * WeightedGraphTest This class will test Weighted Graphs, and make sure they
 * are possible.
 */

import org.codehaus.plexus.graph.domain.basic.*;
import org.codehaus.plexus.graph.exception.*;

/**
 * Description of the Class
 */
public class WeightedGraphTest
     extends DirGraphTest
{
    /**
     * Constructor for the WeightedGraphTest object
     *
     * @param name
     */
    public WeightedGraphTest(String name)
    {
        super(name);
    }

    /**
     * 5.0
     *
     * @---------> @
     */
    public DefaultDirectedGraph makeWDirectedEdge()
        throws GraphException
    {
        DefaultDirectedGraph RC =
            (DefaultDirectedGraph) makeDirectedEdge();
        RC.setWeight(V1_V2, 5.0);
        return RC;
    }

    /**
     * A unit test for JUnit
     */
    public void testWDirectedEdge()
        throws Throwable
    {
        DefaultDirectedGraph IUT = makeWDirectedEdge();

        verifyGraph(IUT, 2, 1);
        assertEquals("Wrong Weight on V1->V2",
            5.0, IUT.getWeight(V1_V2), 0.00001);
    }

    /**
     * /----\ / 5.0 \ @ | \ / \----/
     */
    public DefaultDirectedGraph makeWSelfLoop()
        throws GraphException
    {
        DefaultDirectedGraph RC =
            (DefaultDirectedGraph) makeSelfLoop();
        RC.setWeight(V1_V1, 5.0);
        return RC;
    }

    /**
     * A unit test for JUnit
     */
    public void testWSelfLoop()
        throws Throwable
    {
        DefaultDirectedGraph IUT = makeWSelfLoop();

        verifyGraph(IUT, 1, 1);
        assertEquals("Wrong Weight on V1->V1",
            5.0, IUT.getWeight(V1_V1), 0.00001);
    }

    /**
     * v1 / ^ 2.0 / \ 1.5 v \ v2------->v3 4.0
     */
    public DefaultDirectedGraph makePositiveCycle()
        throws GraphException
    {
        DefaultDirectedGraph RC =
            (DefaultDirectedGraph) makeDirectedCycle();

        RC.setWeight(V1_V2, 2.0);
        RC.setWeight(V2_V3, 4.0);
        RC.setWeight(V3_V1, 1.5);

        return RC;
    }

    /**
     * A unit test for JUnit
     */
    public void testPositiveCycle()
        throws Throwable
    {
        DefaultDirectedGraph IUT = makePositiveCycle();
        verifyGraph(IUT, 3, 3);

        assertEquals("Wrong Weight on V1->V2",
            IUT.getWeight(V1_V2), 2.0, 0.00001);

        assertEquals("Wrong Weight on V2->V3",
            IUT.getWeight(V2_V3), 4.0, 0.00001);

        assertEquals("Wrong Weight on V1->V2",
            IUT.getWeight(V3_V1), 1.5, 0.00001);
    }

    /**
     * v1 / ^ 2.0 / \ - 1.5 v \ v2------->v3 4.0
     */
    public DefaultDirectedGraph makePositivePartNegCycle()
        throws GraphException
    {
        DefaultDirectedGraph RC =
            (DefaultDirectedGraph) makeDirectedCycle();

        RC.setWeight(V1_V2, 2.0);
        RC.setWeight(V2_V3, 4.0);
        RC.setWeight(V3_V1, -1.5);

        return RC;
    }

    /**
     * A unit test for JUnit
     */
    public void testPositivePartNegCycle()
        throws Throwable
    {
        DefaultDirectedGraph IUT = makePositivePartNegCycle();
        verifyGraph(IUT, 3, 3);

        assertEquals("Wrong Weight on V1->V2",
            IUT.getWeight(V1_V2), 2.0, 0.00001);

        assertEquals("Wrong Weight on V2->V3",
            IUT.getWeight(V2_V3), 4.0, 0.00001);

        assertEquals("Wrong Weight on V1->V2",
            IUT.getWeight(V3_V1), -1.5, 0.00001);
    }

    /**
     * v1 / ^ - 2.0 / \ - 1.5 v \ v2------->v3 - 4.0
     */
    public DefaultDirectedGraph makeNegativeCycle()
        throws GraphException
    {
        DefaultDirectedGraph RC =
            (DefaultDirectedGraph) makeDirectedCycle();

        RC.setWeight(V1_V2, -2.0);
        RC.setWeight(V2_V3, -4.0);
        RC.setWeight(V3_V1, -1.5);

        return RC;
    }

    /**
     * A unit test for JUnit
     */
    public void testNegativeCycle()
        throws Throwable
    {
        DefaultDirectedGraph IUT = makeNegativeCycle();
        verifyGraph(IUT, 3, 3);

        assertEquals("Wrong Weight on V1->V2",
            IUT.getWeight(V1_V2), -2.0, 0.00001);

        assertEquals("Wrong Weight on V2->V3",
            IUT.getWeight(V2_V3), -4.0, 0.00001);

        assertEquals("Wrong Weight on V1->V2",
            IUT.getWeight(V3_V1), -1.5, 0.00001);
    }

    /**
     * v1 / ^ - 2.0 / \ 1.5 v \ v2------->v3 - 4.0
     */
    public DefaultDirectedGraph makeNegativePartPosCycle()
        throws GraphException
    {
        DefaultDirectedGraph RC =
            (DefaultDirectedGraph) makeDirectedCycle();

        RC.setWeight(V1_V2, -2.0);
        RC.setWeight(V2_V3, -4.0);
        RC.setWeight(V3_V1, 1.5);

        return RC;
    }

    /**
     * A unit test for JUnit
     */
    public void testNegativePartPosCycle()
        throws Throwable
    {
        DefaultDirectedGraph IUT = makeNegativePartPosCycle();
        verifyGraph(IUT, 3, 3);

        assertEquals("Wrong Weight on V1->V2",
            IUT.getWeight(V1_V2), -2.0, 0.00001);

        assertEquals("Wrong Weight on V2->V3",
            IUT.getWeight(V2_V3), -4.0, 0.00001);

        assertEquals("Wrong Weight on V1->V2",
            IUT.getWeight(V3_V1), 1.5, 0.00001);
    }

    /**
     * 1.5 3.5 v1 ---> v2 --->v3
     */
    public DefaultDirectedGraph makePositivePipe()
        throws GraphException
    {
        DefaultDirectedGraph RC =
            (DefaultDirectedGraph) makePipe();

        RC.setWeight(V1_V2, 1.5);
        RC.setWeight(V2_V3, 3.5);

        return RC;
    }

    /**
     * A unit test for JUnit
     */
    public void testPositivePipe()
        throws Throwable
    {
        DefaultDirectedGraph IUT = makePositivePipe();
        verifyGraph(IUT, 3, 2);

        assertEquals("Wrong Weight on V1->V2",
            IUT.getWeight(V1_V2), 1.5, 0.00001);

        assertEquals("Wrong Weight on V2->V3",
            IUT.getWeight(V2_V3), 3.5, 0.00001);
    }

    /**
     * -1.5 3.5 v1 ---> v2 --->v3
     */
    public DefaultDirectedGraph makePositivePartNegPipe()
        throws GraphException
    {
        DefaultDirectedGraph RC =
            (DefaultDirectedGraph) makePipe();

        RC.setWeight(V1_V2, -1.5);
        RC.setWeight(V2_V3, 3.5);

        return RC;
    }

    /**
     * A unit test for JUnit
     */
    public void testPositivePartNegPipe()
        throws Throwable
    {
        DefaultDirectedGraph IUT = makePositivePartNegPipe();
        verifyGraph(IUT, 3, 2);

        assertEquals("Wrong Weight on V1->V2",
            IUT.getWeight(V1_V2), -1.5, 0.00001);

        assertEquals("Wrong Weight on V2->V3",
            IUT.getWeight(V2_V3), 3.5, 0.00001);
    }


    /**
     * -1.5 -3.5 v1 ---> v2 --->v3
     */
    public DefaultDirectedGraph makeNegativePipe()
        throws GraphException
    {
        DefaultDirectedGraph RC =
            (DefaultDirectedGraph) makePipe();

        RC.setWeight(V1_V2, -1.5);
        RC.setWeight(V2_V3, -3.5);

        return RC;
    }

    /**
     * A unit test for JUnit
     */
    public void testNegativePipe()
        throws Throwable
    {
        DefaultDirectedGraph IUT = makeNegativePipe();
        verifyGraph(IUT, 3, 2);

        assertEquals("Wrong Weight on V1->V2",
            IUT.getWeight(V1_V2), -1.5, 0.0001);

        assertEquals("Wrong Weight on V2->V3",
            IUT.getWeight(V2_V3), -3.5, 0.0001);
    }

    /**
     * 1.5 -3.5 v1 ---> v2 --->v3
     */
    public DefaultDirectedGraph makeNegativePartPosPipe()
        throws GraphException
    {
        DefaultDirectedGraph RC =
            (DefaultDirectedGraph) makePipe();

        RC.setWeight(V1_V2, 1.5);
        RC.setWeight(V2_V3, -3.5);

        return RC;
    }

    /**
     * A unit test for JUnit
     */
    public void testNegativePartPosPipe()
        throws Throwable
    {
        DefaultDirectedGraph IUT = makeNegativePartPosPipe();
        verifyGraph(IUT, 3, 2);

        assertEquals("Wrong Weight on V1->V2",
            IUT.getWeight(V1_V2), 1.5, 0.0001);

        assertEquals("Wrong Weight on V2->V3",
            IUT.getWeight(V2_V3), -3.5, 0.0001);
    }


    /**
     * v1 1.5 / \ 2.5 v v v2 v3 1.5 \ / 2.5 vv v4
     */

    public DefaultDirectedGraph makeMultiplePathL()
        throws GraphException
    {
        DefaultDirectedGraph RC =
            (DefaultDirectedGraph) makeDiamond();

        RC.setWeight(V1_V2, 1.5);
        RC.setWeight(V2_V4, 1.5);

        RC.setWeight(V1_V3, 2.5);
        RC.setWeight(V3_V4, 3.5);

        return RC;
    }

    /**
     * A unit test for JUnit
     */
    public void testMultiplePathL()
        throws Throwable
    {
        DefaultDirectedGraph IUT = makeMultiplePathL();
        verifyGraph(IUT, 4, 4);

        assertEquals("Wrong Weight on V1->V2",
            IUT.getWeight(V1_V2), 1.5, 0.0001);

        assertEquals("Wrong Weight on V2->V4",
            IUT.getWeight(V2_V4), 1.5, 0.0001);

        assertEquals("Wrong Weight on V1->V3",
            IUT.getWeight(V1_V3), 2.5, 0.0001);

        assertEquals("Wrong Weight on V3->V4",
            IUT.getWeight(V3_V4), 3.5, 0.0001);
    }


    /**
     * v1 2.5 / \ 1.5 v v v2 v3 2.5 \ / 1.5 vv v4
     */

    public DefaultDirectedGraph makeMultiplePathR()
        throws GraphException
    {
        DefaultDirectedGraph RC =
            (DefaultDirectedGraph) makeDiamond();

        RC.setWeight(V1_V2, 3.5);
        RC.setWeight(V2_V4, 2.5);

        RC.setWeight(V1_V3, 1.5);
        RC.setWeight(V3_V4, 1.5);

        return RC;
    }

    /**
     * A unit test for JUnit
     */
    public void testMultiplePathR()
        throws Throwable
    {
        DefaultDirectedGraph IUT = makeMultiplePathR();
        verifyGraph(IUT, 4, 4);

        assertEquals("Wrong Weight on V1->V2",
            IUT.getWeight(V1_V2), 3.5, 0.0001);

        assertEquals("Wrong Weight on V2->V4",
            IUT.getWeight(V2_V4), 2.5, 0.0001);

        assertEquals("Wrong Weight on V1->V3",
            IUT.getWeight(V1_V3), 1.5, 0.0001);

        assertEquals("Wrong Weight on V3->V4",
            IUT.getWeight(V3_V4), 1.5, 0.0001);
    }


    /**
     * v1 10.0 / \ 0.5 v v v2 v3 10.0 \ / 10.5 vv v4
     */

    public DefaultDirectedGraph makeMultiplePathEarlyLow()
        throws GraphException
    {
        DefaultDirectedGraph RC =
            (DefaultDirectedGraph) makeDiamond();

        RC.setWeight(V1_V2, 10.0);
        RC.setWeight(V2_V4, 10.0);

        RC.setWeight(V1_V3, 0.5);
        RC.setWeight(V3_V4, 10.5);

        return RC;
    }

    /**
     * A unit test for JUnit
     */
    public void testMultiplePathEarlyLow()
        throws Throwable
    {
        DefaultDirectedGraph IUT = makeMultiplePathEarlyLow();
        verifyGraph(IUT, 4, 4);

        assertEquals("Wrong Weight on V1->V2",
            IUT.getWeight(V1_V2), 10.0, 0.0001);

        assertEquals("Wrong Weight on V2->V4",
            IUT.getWeight(V2_V4), 10.0, 0.0001);

        assertEquals("Wrong Weight on V1->V3",
            IUT.getWeight(V1_V3), 0.5, 0.0001);

        assertEquals("Wrong Weight on V3->V4",
            IUT.getWeight(V3_V4), 10.5, 0.0001);
    }


    /**
     * v1 10.0 / \ 10.5 v v v2 v3 10.0 \ / 0.5 vv v4
     */

    public DefaultDirectedGraph makeMultiplePathEarlyHigh()
        throws GraphException
    {
        DefaultDirectedGraph RC =
            (DefaultDirectedGraph) makeDiamond();

        RC.setWeight(V1_V2, 10.0);
        RC.setWeight(V2_V4, 10.0);

        RC.setWeight(V1_V3, 10.5);
        RC.setWeight(V3_V4, 0.5);

        return RC;
    }


    /**
     * A unit test for JUnit
     */
    public void testMultiplePathEarlyHigh()
        throws Throwable
    {
        DefaultDirectedGraph IUT = makeMultiplePathEarlyHigh();
        verifyGraph(IUT, 4, 4);

        assertEquals("Wrong Weight on V1->V2",
            IUT.getWeight(V1_V2), 10.0, 0.0001);

        assertEquals("Wrong Weight on V2->V4",
            IUT.getWeight(V2_V4), 10.0, 0.0001);

        assertEquals("Wrong Weight on V1->V3",
            IUT.getWeight(V1_V3), 10.5, 0.0001);

        assertEquals("Wrong Weight on V3->V4",
            IUT.getWeight(V3_V4), 0.5, 0.0001);
    }

}






