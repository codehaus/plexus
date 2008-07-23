/*
 * Copyright (C) MX4J.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */
package org.codehaus.plexus.httpd;

/**
 * Utility methods for the HTTP adaptor
 *
 * @author <a href="mailto:tibu@users.sourceforge.org">Carlos Quiroz</a>
 * @version $Revision$
 */
public class HttpUtil
{

    /**
     * Gets a message apropriate for a give HTTP code
     *
     * @param code  Reference Code
     * @return      The result message
     * @see HttpConstants
     */
    public static String getCodeMessage( int code )
    {
        switch ( code )
        {
            case HttpConstants.STATUS_OKAY:
                return "OK";
            case HttpConstants.STATUS_NO_CONTENT:
                return "No Content";
            case HttpConstants.STATUS_MOVED_PERMANENTLY:
                return "Moved Permanently";
            case HttpConstants.STATUS_MOVED_TEMPORARILY:
                return "Moved Temporarily";
            case HttpConstants.STATUS_BAD_REQUEST:
                return "Bad Request";
            case HttpConstants.STATUS_FORBIDDEN:
                return "Forbidden";
            case HttpConstants.STATUS_NOT_FOUND:
                return "Not Found";
            case HttpConstants.STATUS_NOT_ALLOWED:
                return "Method Not Allowed";
            case HttpConstants.STATUS_INTERNAL_ERROR:
                return "Internal Server Error";
            case HttpConstants.STATUS_AUTHENTICATE:
                return "Authentication requested";
            case HttpConstants.STATUS_NOT_IMPLEMENTED:
                return "Not Implemented";
            default:
                return "Unknown Code (" + code + ")";
        }
    }


    /**
     * Makes a path canonical
     *
     * @param path  Target path
     * @return The canonicalized path
     */
    public static String canonicalizePath( String path )
    {
        char[] chars = path.toCharArray();
        int length = chars.length;
        int idx;
        int odx = 0;
        while ( ( idx = indexOf( chars, length, '/', odx ) ) < length - 1 )
        {
            int ndx = indexOf( chars, length, '/', idx + 1 );
            int kill = -1;
            if ( ndx == idx + 1 )
            {
                kill = 1;
            }
            else if ( ( ndx >= idx + 2 ) && ( chars[idx + 1] == '.' ) )
            {
                if ( ndx == idx + 2 )
                {
                    kill = 2;
                }
                else if ( ( ndx == idx + 3 ) && ( chars[idx + 2] == '.' ) )
                {
                    kill = 3;
                    while ( ( idx > 0 ) && ( chars[--idx] != '/' ) )
                    {
                        ++kill;
                    }
                }
            }
            if ( kill == -1 )
            {
                odx = ndx;
            }
            else if ( idx + kill >= length )
            {
                length = odx = idx + 1;
            }
            else
            {
                length -= kill;
                System.arraycopy( chars, idx + 1 + kill,
                                  chars, idx + 1, length - idx - 1 );
                odx = idx;
            }
        }
        return new String( chars, 0, length );
    }


    protected static int indexOf( char[] chars, int length, char chr, int from )
    {
        while ( ( from < length ) && ( chars[from] != chr ) )
        {
            ++from;
        }
        return from;
    }

    /**
     * Returns whether a boolean variable is in the variables. It tries to find
     * it. If not found the the default is used. If found is tested to check if
     * it is <code>true</code> or <code>1</code> and the answer is true.
     * Otherwise is false
     */
    public static boolean booleanVariableValue( HttpInputStream in, String variable, boolean defaultValue )
    {
        if ( in.getVariables().containsKey( variable ) )
        {
            String result = (String) in.getVariables().get( variable );
            return result.equals( "true" ) || result.equals( "1" );
        }
        return defaultValue;
    }
}
