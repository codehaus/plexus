package org.codehaus.plexus.graph.domain.statemachine;

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

import org.codehaus.plexus.graph.Named;
import org.codehaus.plexus.graph.Vertex;

/** Description of the Class */
public class State
    implements Vertex, Named
{
    private String name;
    private StateMachine subMachine = null;

    /**
     * Constructor for the State object
     *
     * @param name
     */
    public State( String name )
    {
        this.name = name;
    }

    /** Gets the name attribute of the State object */
    public String getName()
    {
        return name;
    }

    /** Sets the submachine attribute of the State object */
    public void setSubmachine( StateMachine subMachine )
    {
        this.subMachine = subMachine;
    }

    /** Gets the submachine attribute of the State object */
    public StateMachine getSubmachine()
    {
        return this.subMachine;
    }

}
