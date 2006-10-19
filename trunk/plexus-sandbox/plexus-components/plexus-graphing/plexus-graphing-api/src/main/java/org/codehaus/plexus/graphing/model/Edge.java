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

import org.codehaus.plexus.graphing.decorators.EdgeDecorator;
import org.codehaus.plexus.util.StringUtils;

/**
 * Immutable Edge Detail.
 *  
 * Informational only, not engaged in graph logic.
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class Edge
{
    private EdgeDecorator decorator;

    private Node from;

    private Node to;

    public Edge( Node from, Node to )
    {
        if ( from == null )
        {
            throw new IllegalArgumentException( "From node cannot be null." );
        }

        if ( to == null )
        {
            throw new IllegalArgumentException( "To node cannot be null." );
        }

        this.from = from;
        this.to = to;
    }

    public EdgeDecorator getDecorator()
    {
        // Lazy init of decorator.
        if ( decorator == null )
        {
            decorator = new EdgeDecorator();
        }
        return decorator;
    }

    public void setDecorator( EdgeDecorator decorator )
    {
        this.decorator = decorator;
    }

    public Node getFrom()
    {
        return from;
    }

    public Node getTo()
    {
        return to;
    }

    protected void connect()
    {
        from.addChildNode( to );
        to.addParentNode( from );
    }

    protected void disconnect()
    {
        from.removeChildNode( to );
        to.removeParentNode( from );
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "Edge[" );
        buf.append( "from=" );
        if ( from != null )
        {
            buf.append( StringUtils.escape( this.from.getLabel() ) );
        }
        else
        {
            buf.append( "<null>" );
        }
        buf.append( ",to=" );
        if ( to != null )
        {
            buf.append( StringUtils.escape( this.to.getLabel() ) );
        }
        else
        {
            buf.append( "<null>" );
        }
        buf.append( "]" );
        return buf.toString();
    }
}
