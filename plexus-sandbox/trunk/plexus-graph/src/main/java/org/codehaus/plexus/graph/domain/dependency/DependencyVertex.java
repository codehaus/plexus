package org.codehaus.plexus.graph.domain.dependency;

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

import org.codehaus.plexus.graph.*;

/** Description of the Class */
public class DependencyVertex
    implements Vertex
{
    private Object value;

    /**
     * Constructor for the DependencyVertex object
     *
     * @param value
     */
    public DependencyVertex( Object value )
    {
        this.value = value;
    }

    /** Gets the value attribute of the DependencyVertex object */
    public Object getValue()
    {
        return value;
    }
}
