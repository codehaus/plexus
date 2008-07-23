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

import org.codehaus.plexus.graphing.model.Edge;
import org.codehaus.plexus.graphing.model.Graph;
import org.codehaus.plexus.graphing.model.GraphConstraintException;
import org.codehaus.plexus.graphing.model.Node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Dag - Directed Acyclic Graph 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class Dag
    extends Graph
    implements Cloneable, Serializable
{
    protected void assertGraphConstraints( Edge edge )
        throws GraphConstraintException
    {
        super.assertGraphConstraints( edge );

        List cycle = CycleDetector.introducesCycle( edge.getFrom() );

        if ( cycle != null )
        {
            // remove edge which introduced cycle

            if ( removeEdge( edge ) == null )
            {
                throw new IllegalStateException( "Unable to remove edge " + edge );
            }

            final String msg = "Edge between '" + edge.getFrom().getLabel() + "' and '" + edge.getTo().getLabel()
                + "' introduces to cycle in the graph";

            throw new CycleDetectedException( msg, cycle );
        }
    }

    /**
     * Return the list of labels of successor in order decided by topological sort
     *
     * @param label The label of the node whose predessors are serched
     *
     * @return The list of labels. Returned list contains also
     *         the label passed as parameter to this method. This label should
     *         always be the last item in the list.
     */
    public List getSuccessorLabels( final String label )
    {
        final Node node = getNode( label );

        final List retValue;

        //optimization.
        if ( node.isLeaf() )
        {
            retValue = new ArrayList( 1 );

            retValue.add( label );
        }
        else
        {
            retValue = TopologicalSorter.sort( node );
        }

        return retValue;
    }

    protected Object clone()
        throws CloneNotSupportedException
    {
        return super.clone();
    }
}
