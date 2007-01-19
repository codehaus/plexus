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

import org.codehaus.plexus.graph.MutableDirectedGraph;
import org.codehaus.plexus.graph.contract.Contract;
import org.codehaus.plexus.graph.decorator.DDirectedGraph;
import org.codehaus.plexus.graph.exception.GraphException;
import org.codehaus.plexus.graph.factory.GraphFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * StateMachine -
 * <p/>
 * This represents a Finite State Machine.  It has a collection
 * of states and transitions which move between them.
 */
public class StateMachine
    extends DDirectedGraph
{
    private Map states = new HashMap();// NAME X STATE
    private Map transes = new HashMap();// NAME X TRANSITION
    private Set finalStates = new HashSet();
    private State startState = null;
    private MutableDirectedGraph graph = null;
    private String name;
    private GraphFactory factory = new GraphFactory();

    /**
     * Create a new StateMachine given the name.
     *
     * @param name Name (or Namespace) of StateMachine
     */
    public StateMachine( String name )
    {
        this.name = name;

        Contract[] contracts = new Contract[0];
        graph = factory.makeMutableDirectedGraph( contracts, false, null );
        setDirGraph( graph );
    }

    /** Gets the StateMachines name (or namespace) */
    public String getName()
    {
        return name;
    }

    /**
     * Adds a State to the collection of states maintained
     * by the machine.
     */
    public void addState( State state )
        throws GraphException
    {
        states.put( state.getName(), state );
        graph.addVertex( state );
    }

    /**
     * Creates a new State with name provided.  Adds it to the
     * machine.
     */
    public void addState( String name )
        throws GraphException
    {
        addState( new State( name ) );
    }

    /** Sets the startState attribute of the StateMachine object */
    public void setStartState( State state )
    {
        startState = state;
    }

    /** Adds the state to the set of Final States available. */
    public void addFinalState( State state )
    {
        finalStates.add( state );
    }

    /**
     * Add a Transition to the diagram with the name provided.
     * <p/>
     * This transition traverses from the State specified in source
     * and the target.
     *
     * @param name   Name of the Transition.
     * @param source Name of the Source State.
     * @param target Name of the Target State.
     */
    public void addTransition( String name,
                               String source,
                               String target )
        throws GraphException
    {
        addTransition( name, getState( source ), getState( target ) );
    }

    /**
     * Adds a feature to the Transition attribute of the StateMachine object.
     *
     * @param name   Name of the Transition
     * @param source Source of the Transition
     * @param target Target of the Transition
     */
    public void addTransition( String name,
                               State source,
                               State target )
        throws GraphException
    {
        Transition trans = new Transition( name, source, target );
        addTransition( trans );
    }

    /**
     * Adds a feature to the Transition attribute of the StateMachine object.
     * Transition is expected to know its source and target.
     */
    public void addTransition( Transition trans )
        throws GraphException
    {
        transes.put( trans.getName(), trans );
        graph.addEdge( trans, trans.getSource(), trans.getTarget() );
    }

    /** Gets the state attribute of the StateMachine object */
    public State getState( String name )
    {
        return (State) states.get( name );
    }

    /** Gets the Transition associated with the Name. */
    public Transition getTransition( String name )
    {
        return (Transition) transes.get( name );
    }
}








