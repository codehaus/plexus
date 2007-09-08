package org.codehaus.plexus.redback.xwork.filter.authentication.digest;

/*
 * Copyright 2005-2006 The Codehaus.
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

/**
 * Hex
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @todo should probably move this to plexus-utils or plexus-security-common
 */
public class Hex
{
    private static final byte[] DIGITS = "0123456789abcdef".getBytes();

    public static String encode( byte[] data )
    {
        int l = data.length;

        byte[] raw = new byte[l * 2];

        for ( int i = 0, j = 0; i < l; i++ )
        {
            raw[j++] = DIGITS[( 0xF0 & data[i] ) >>> 4];
            raw[j++] = DIGITS[0x0F & data[i]];
        }

        return new String( raw );
    }

    public static String encode( String raw )
    {
        return encode( raw.getBytes() );
    }
}
