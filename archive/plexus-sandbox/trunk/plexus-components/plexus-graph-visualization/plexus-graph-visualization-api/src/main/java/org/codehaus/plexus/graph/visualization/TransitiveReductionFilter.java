package org.codehaus.plexus.graph.visualization;

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

/**
 * TransitiveReductionFilter - remove the edges that are redundant or implied by transitivity.
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class TransitiveReductionFilter
{
    public DecoratedDirectedGraph reduce( DecoratedDirectedGraph graph )
    {
        // HACK: The DecoratedGraph concept should really be in plexus-graph
        // This implementation could just as well remove the existing edges 
        DecoratedDirectedGraph reduced = new DecoratedDirectedGraph( graph );

        
        
        return reduced;
    }
}
