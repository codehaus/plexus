package org.codehaus.plexus.spring;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.plexus.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOM2Utils - provides a bridge for some DOM3 methods to the DOM2 present in JDK 1.5
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class DOM2Utils
{
    /**
     * In DOM3, there is a method called Node.getTextContext() which returns the text nodes
     * of the node and all child nodes.  This is a DOM2 equivalent.
     * 
     * @param node the node to start from.
     * @return the string of all node and child node text nodes.
     */
    public static String getTextContext( Node node )
    {
        StringBuilder txt = new StringBuilder();

        appendTextNodes( node, txt );

        return txt.toString();
    }

    private static void appendTextNodes( Node node, StringBuilder txt )
    {
        if ( node.getNodeType() == Node.TEXT_NODE )
        {
            txt.append( node.getNodeValue() );
        }
        else if ( node.getNodeType() == Node.ELEMENT_NODE )
        {
            Element elem = (Element) node;
            NodeList nodes = elem.getChildNodes();
            int len = nodes.getLength();
            for ( int i = 0; i < len; i++ )
            {
                appendTextNodes( nodes.item( i ), txt );
            }
        }
    }
    
    public static String escapeAttributeValue( String value )
    {
        if ( StringUtils.isEmpty( value ) )
        {
            return null;
        }
        StringWriter writer = new StringWriter( value.length() * 2 );
        try
        {
            escapeJavaStyleString( writer, value, true );
            return writer.toString();
        }
        catch ( IOException e )
        {
            // this should never ever happen while writing to a StringWriter
            throw new RuntimeException( "error during escapeJavaStyleString " + e.getMessage(), e );
        }
    }

    public static String escapeText( String text )
    {
        return StringEscapeUtils.escapeXml( text );
    }
    
    private static void escapeJavaStyleString( Writer out, String str, boolean escapeSingleQuote )
        throws IOException
    {
        if ( out == null )
        {
            throw new IllegalArgumentException( "The Writer must not be null" );
        }
        if ( str == null )
        {
            return;
        }
        int sz;
        sz = str.length();
        for ( int i = 0; i < sz; i++ )
        {
            char ch = str.charAt( i );

            // handle unicode
            if ( ch > 0xfff )
            {
                out.write( "\\u" + hex( ch ) );
            }
            else if ( ch > 0xff )
            {
                out.write( "\\u0" + hex( ch ) );
            }
            else if ( ch > 0x7f )
            {
                out.write( "\\u00" + hex( ch ) );
            }
            else if ( ch < 32 )
            {
                switch ( ch )
                {
                    case '\b':
                        out.write( '\\' );
                        out.write( 'b' );
                        break;
                    case '\n':
                        out.write( '\\' );
                        out.write( 'n' );
                        break;
                    case '\t':
                        out.write( '\\' );
                        out.write( 't' );
                        break;
                    case '\f':
                        out.write( '\\' );
                        out.write( 'f' );
                        break;
                    case '\r':
                        out.write( '\\' );
                        out.write( 'r' );
                        break;
                    default:
                        if ( ch > 0xf )
                        {
                            out.write( "\\u00" + hex( ch ) );
                        }
                        else
                        {
                            out.write( "\\u000" + hex( ch ) );
                        }
                        break;
                }
            }
            else
            {
                switch ( ch )
                {
                    case '\'':
                        if ( escapeSingleQuote )
                        {
                            out.write( '\\' );
                        }
                        out.write( '\'' );
                        break;
                    case '"':
                        out.write( '\\' );
                        out.write( '"' );
                        break;
                    case '\\':
                        out.write( '\\' );
                        out.write( '\\' );
                        break;
                    default:
                        out.write( ch );
                        break;
                }
            }
        }
    }

    private static String hex( char ch )
    {
        return Integer.toHexString( ch ).toUpperCase();
    }
    
}
