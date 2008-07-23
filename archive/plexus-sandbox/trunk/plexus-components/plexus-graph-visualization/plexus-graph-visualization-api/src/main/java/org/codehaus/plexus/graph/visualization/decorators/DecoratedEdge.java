package org.codehaus.plexus.graph.visualization.decorators;

import org.codehaus.plexus.graph.Edge;

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

/**
 * DecoratedEdge - indicates that the edge can be decorated.
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public interface DecoratedEdge
    extends Edge
{
    /**
     * Get the EdgeDecorator for this edge.
     *  
     * @return the decorator. (can be null)
     */
    public EdgeDecorator getDecorator();

    /**
     * Set the Edge Decorator for this edge.
     * 
     * @param decorator the decorator. (can be null)
     */
    public void setDecorator( EdgeDecorator decorator );
}
