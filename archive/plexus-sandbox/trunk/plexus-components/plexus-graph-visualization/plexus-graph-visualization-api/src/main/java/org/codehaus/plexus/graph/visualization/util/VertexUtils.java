package org.codehaus.plexus.graph.visualization.util;

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

/**
 * VertexIdUtil - common Vertex utility methods. 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class VertexUtils
{
    public static String toId( String raw )
    {
        StringBuffer id = new StringBuffer();

        for ( int i = 0; i < raw.length(); i++ )
        {
            char c = raw.charAt( i );
            if ( Character.isLetterOrDigit( c ) )
            {
                id.append( Character.toUpperCase( c ) );
            }
            else if ( ( c == '-' ) || ( c == '_' ) )
            {
                id.append( "_" );
            }
        }

        return id.toString();
    }

    public static String getLabel( Vertex vertex )
    {
        if ( vertex instanceof Named )
        {
            return ( (Named) vertex ).getName();
        }

        return vertex.toString();
    }

    public static String toId( Vertex vertex )
    {
        if ( vertex == null )
        {
            return "";
        }

        String label = getLabel( vertex );

        if ( label == null )
        {
            return "";
        }

        if ( label.trim().length() <= 0 )
        {
            return "";
        }

        return toId( label );
    }
}
