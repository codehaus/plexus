package org.codehaus.plexus.graphing.model;

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

import org.codehaus.plexus.graphing.decorators.GraphDecorator;
import org.codehaus.plexus.util.StringUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Graph 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class Graph
    implements Cloneable, Serializable
{
    private GraphDecorator decorator;

    public GraphDecorator getDecorator()
    {
        // Lazy Init of decorator.
        if ( decorator == null )
        {
            decorator = new GraphDecorator();
        }
        return decorator;
    }

    public void setDecorator( GraphDecorator decorator )
    {
        this.decorator = decorator;
    }

    // Node Manipulation.
    private Map nodeMap = new HashMap();

    /**
     * Create a node with the provided label, and add it to the graph.
     * 
     * @param label the label to create if the node does not exist.
     * @return the node added.
     * @throws GraphConstraintException if there was a problem with adding this node.
     */
    public Node addNode( String label )
        throws GraphConstraintException
    {
        Node node = getNode( label );

        if ( node == null )
        {
            node = new Node( label );
            addNode( node );
        }

        return node;
    }

    /**
     * Add a Node to the graph.
     * 
     * @param node the node to add
     * @return the node added.
     * @throws GraphConstraintException if there is a problem with adding this node.
     */
    public Node addNode( Node node )
        throws GraphConstraintException
    {
        if ( node == null )
        {
            throw new GraphConstraintException( "Unable to add null node." );
        }

        if ( StringUtils.isEmpty( node.getLabel() ) )
        {
            throw new GraphConstraintException( "Unable to add node with empty label." );
        }

        nodeMap.put( node.getLabel(), node );

        assertGraphConstraints( node );

        return node;
    }

    /**
     * Returns the collection of nodes being tracked by this graph.
     * 
     * @return the collection of nodes.
     */
    public Collection getNodes()
    {
        return nodeMap.values();
    }

    /**
     * Get the node with the provided label.
     * 
     * @param label the label of the node
     * @return the node being tracked, or null if not found.
     */
    public Node getNode( String label )
    {
        return (Node) nodeMap.get( label );
    }

    /**
     * Remove the provided node from the graph.
     * 
     * @param node the node to remove.
     * @return the node that was removed, or null if nothing was removed.
     */
    public Node removeNode( Node node )
    {
        return (Node) nodeMap.remove( node );
    }

    /**
     * Remove the provided node from the graph.
     * 
     * @param node the node to remove.
     * @return the node that was removed, or null if nothing was removed.
     */
    public Node removeNode( final String label )
    {
        Node node = getNode( label );

        if ( node == null )
        {
            return null;
        }

        return (Node) nodeMap.remove( node );
    }

    /**
     * Indicates if there is at least one edge leading to or from node of given label
     * 
     * @return <code>true</true> if this node is connected with other node,<code>false</code> otherwise
     */
    public boolean isConnected( final String label )
    {
        final Node node = getNode( label );

        if ( node == null )
        {
            return false;
        }

        return node.isConnected();
    }

    public Set getNodeLabels()
    {
        return nodeMap.keySet();
    }

    // Edge Manipulation.

    private Map edgeMap = new HashMap();

    public Edge addEdge( Edge edge )
        throws GraphConstraintException
    {
        if ( edge.getFrom() == null )
        {
            throw new GraphConstraintException( "Unable to add edge with <null> from node." );
        }

        if ( edge.getTo() == null )
        {
            throw new GraphConstraintException( "Unable to add edge with <null> to node." );
        }

        edge.connect();

        String key = toEdgeKey( edge );

        edgeMap.put( key, edge );

        assertGraphConstraints( edge );

        return edge;
    }

    public Edge addEdge( Node start, Node end )
        throws GraphConstraintException
    {
        Edge edge = new Edge( start, end );

        return addEdge( edge );
    }

    public Edge addEdge( String startLabel, String endLabel )
        throws GraphConstraintException
    {
        Node start = addNode( startLabel );
        Node end = addNode( endLabel );

        return addEdge( start, end );
    }

    public Edge getEdge( Node start, Node end )
    {
        String key = toEdgeKey( start, end );
        return (Edge) edgeMap.get( key );
    }

    public Collection getEdges()
    {
        return edgeMap.values();
    }

    public boolean hasEdge( Node start, Node end )
    {
        String key = toEdgeKey( start, end );

        return edgeMap.containsKey( key );
    }

    public boolean hasEdge( String startLabel, String endLabel )
    {
        return hasEdge( getNode( startLabel ), getNode( endLabel ) );
    }

    public Edge removeEdge( Edge edge )
    {
        String key = toEdgeKey( edge );
        Edge ret = (Edge) edgeMap.remove( key );
        ret.disconnect();
        return ret;
    }

    public Edge removeEdge( Node start, Node end )
    {
        String key = toEdgeKey( start, end );
        Edge ret = (Edge) edgeMap.remove( key );
        ret.disconnect();
        return ret;
    }

    protected String toEdgeKey( Edge edge )
    {
        return toEdgeKey( edge.getFrom(), edge.getTo() );
    }

    /**
     * Generates an internal Key for the edge, used by the {@link #edgeMap}.
     * 
     * @param start the start node.
     * @param end the end node.
     * @return the key to use for this edge.
     */
    protected String toEdgeKey( Node start, Node end )
    {
        StringBuffer key = new StringBuffer();
        if ( start != null )
        {
            key.append( "[" );
            key.append( start.getLabel() );
            key.append( "]" );
        }
        else
        {
            key.append( "<null>" );
        }
        key.append( " -> " );
        if ( end != null )
        {
            key.append( "[" );
            key.append( end.getLabel() );
            key.append( "]" );
        }
        else
        {
            key.append( "<null>" );
        }
        return key.toString();
    }

    // Protected Methods for overriding classes to utilize.

    protected void assertGraphConstraints( Node node )
        throws GraphConstraintException
    {
        // Ignore
    }

    protected void assertGraphConstraints( Edge edge )
        throws GraphConstraintException
    {
        // Ignore
    }

}
