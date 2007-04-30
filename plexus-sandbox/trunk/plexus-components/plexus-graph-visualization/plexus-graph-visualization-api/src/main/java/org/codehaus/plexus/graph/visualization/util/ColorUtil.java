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

import java.awt.Color;

/**
 * ColorUtil 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class ColorUtil
{
    public static Color toColor( String hexcolor )
    {
        if ( hexcolor == null )
        {
            return null;
        }
        
        if ( hexcolor.trim().length() <= 0 )
        {
            return null;
        }

        if ( !hexcolor.startsWith( "#" ) )
        {
            return null;
        }

        hexcolor = hexcolor.substring( 1 );

        try
        {
            int colorvalue = Integer.parseInt( hexcolor, 16 );
            return new Color( colorvalue );
        }
        catch ( NumberFormatException e )
        {
            return null;
        }
    }

    public static String toCssDeclaration( Color color )
    {
        if ( color == null )
        {
            return "";
        }

        return "#" + toHex( color.getRed() ) + toHex( color.getGreen() ) + toHex( color.getBlue() );
    }

    public static String toHex( int value )
    {
        String ret = Integer.toHexString( value & 0xFF );
        if ( ret.length() == 1 )
        {
            ret = "0" + ret;
        }
        return ret;
    }
}
