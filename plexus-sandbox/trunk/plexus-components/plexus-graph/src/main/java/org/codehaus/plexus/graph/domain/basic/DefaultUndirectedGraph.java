package org.codehaus.plexus.graph.domain.basic;

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

import org.codehaus.plexus.graph.Edge;
import org.codehaus.plexus.graph.MutableGraph;
import org.codehaus.plexus.graph.UndirectedGraph;
import org.codehaus.plexus.graph.Vertex;
import org.codehaus.plexus.graph.WeightedGraph;
import org.codehaus.plexus.graph.exception.GraphException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/** Description of the Class */
public class DefaultUndirectedGraph
    implements UndirectedGraph, WeightedGraph, MutableGraph, InvocationHandler
{
    private Set vertices = new HashSet();
    private Set edges = new HashSet();

    private Map edgeVerts = new HashMap();// EDGE X SET( VERTS )
    private Map vertEdges = new HashMap();// VERTEX X SET( EDGE )
    private Map edgeWeights = new HashMap(); // EDGE X WEIGHT

    /** Constructor for the DefaultUndirectedGraph object */
    public DefaultUndirectedGraph()
    {
    }

    /** Adds a feature to the Vertex attribute of the DefaultUndirectedGraph object */
    public void addVertex( Vertex v )
        throws GraphException
    {
        vertices.add( v );
    }

    public void removeVertex( Vertex v )
        throws GraphException
    {
        vertices.remove( v );
    }

    public void removeEdge( Edge e )
        throws GraphException
    {
        edges.remove( e );
    }

    public void addEdge( Edge e )
        throws GraphException
    {
        edges.add( e );
    }

    public void disconnect( Edge e,
                            Vertex v )
    {
        if ( edgeVerts.containsKey( e ) )
        {
            ( (Set) edgeVerts.get( e ) ).remove( v );
        }

        if ( vertEdges.containsKey( v ) )
        {
            ( (Set) vertEdges.get( v ) ).remove( e );
        }
    }

    public void connect( Edge e,
                         Vertex v )
    {
        Set verts = null;
        if ( !edgeVerts.containsKey( e ) )
        {
            verts = new HashSet();
            edgeVerts.put( e, verts );
        }
        else
        {
            verts = (Set) edgeVerts.get( e );
        }

        verts.add( v );

        Set edges = null;
        if ( !vertEdges.containsKey( v ) )
        {
            edges = new HashSet();
            vertEdges.put( v, edges );
        }
        else
        {
            edges = (Set) vertEdges.get( v );
        }

        edges.add( e );

    }

    /** Adds a feature to the Edge attribute of the DefaultUndirectedGraph object */
    public void addEdge( Edge e,
                         Set vertices )
        throws GraphException
    {
        addEdge( e );

        Iterator verts = vertices.iterator();
        while ( verts.hasNext() )
        {
            connect( e, (Vertex) verts.next() );
        }
    }

    // Interface Methods
    /** Gets the vertices attribute of the DefaultUndirectedGraph object */
    public Set getVertices()
    {
        return new HashSet( vertices );
    }

    /** Gets the vertices attribute of the DefaultUndirectedGraph object */
    public Set getVertices( Edge e )
    {
        if ( edgeVerts.containsKey( e ) )
        {
            return new HashSet( (Set) edgeVerts.get( e ) );
        }
        else
        {
            return new HashSet();
        }
    }

    /** Gets the edges attribute of the DefaultUndirectedGraph object */
    public Set getEdges()
    {
        return new HashSet( edges );
    }

    /** Gets the edges attribute of the DefaultUndirectedGraph object */
    public Set getEdges( Vertex v )
    {
        if ( vertEdges.containsKey( v ) )
        {
            return new HashSet( (Set) vertEdges.get( v ) );
        }
        else
        {
            return new HashSet();
        }
    }

    public void setWeight( Edge e,
                           double w )
    {
        if ( edgeWeights.containsKey( e ) )
        {
            edgeWeights.remove( e );
        }

        edgeWeights.put( e, new Double( w ) );
    }

    public double getWeight( Edge e )
    {
        if ( edgeWeights.containsKey( e ) )
        {
            return ( (Double) edgeWeights.get( e ) ).doubleValue();
        }
        else
        {
            return 1.0;
        }
    }

    /** Description of the Method */
    public Object invoke( Object proxy,
                          Method method,
                          Object args[] )
        throws Throwable
    {
        try
        {
            return method.invoke( this, args );
        }
        catch ( InvocationTargetException ex )
        {
            throw ex.getTargetException();
        }
    }


}









