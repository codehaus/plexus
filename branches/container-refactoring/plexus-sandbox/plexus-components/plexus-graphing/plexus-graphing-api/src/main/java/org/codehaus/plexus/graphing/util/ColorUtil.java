package org.codehaus.plexus.graphing.util;

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

import java.awt.Color;

/**
 * ColorUtil 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class ColorUtil
{
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
