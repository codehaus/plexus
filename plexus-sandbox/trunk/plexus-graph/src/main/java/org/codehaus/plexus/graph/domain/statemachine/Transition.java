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

import org.codehaus.plexus.graph.*;

/** Description of the Class */
public class Transition
    implements Edge, Named
{
    private String name;
    private State source;
    private State target;

    private String action = null;
    private String guard = null;
    private String output = null;
    private String trigger = null;

    /** Description of the Field */
    public final static String EPSILON = "\u03B5";

    /**
     * Constructor for the Transition object
     *
     * @param name
     * @param source
     * @param target
     */
    public Transition( String name,
                       State source,
                       State target )
    {
        this.name = name;
        this.source = source;
        this.target = target;
    }

    /** Gets the name attribute of the Transition object */
    public String getName()
    {
        return name;
    }

    /** Gets the source attribute of the Transition object */
    public State getSource()
    {
        return source;
    }

    /** Gets the target attribute of the Transition object */
    public State getTarget()
    {
        return target;
    }

    /** Sets the action attribute of the Transition object */
    public void setTrigger( String trigger )
    {
        this.trigger = trigger;
    }

    /** Sets the action attribute of the Transition object */
    public void setAction( String action )
    {
        this.action = action;
    }

    /** Gets the action attribute of the Transition object */
    public String getAction()
    {
        return action;
    }

    /** Sets the guard attribute of the Transition object */
    public void setGuard( String guard )
    {
        this.guard = guard;
    }

    /** Gets the guard attribute of the Transition object */
    public String getGuard()
    {
        return guard;
    }

    /** Gets the output attribute of the Transition object */
    public String getOutput()
    {
        return output;
    }

    /** Sets the output attribute of the Transition object */
    public void setOutput( String output )
    {
        this.output = output;
    }
}

