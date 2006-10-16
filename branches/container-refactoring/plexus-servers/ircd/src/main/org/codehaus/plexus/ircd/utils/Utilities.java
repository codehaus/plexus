/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.utils;

import java.util.regex.Pattern;

public class Utilities
{

    /**
     * to get the content between the two hooks
     * @param sValue the source value
     * @return the content between the two hooks if it exists, the void string otherwise
     */
    private static String getContent( String sValue )
    {
        if ( sValue != null )
        {
            int index1 = sValue.indexOf( '[' );
            int index2 = sValue.indexOf( ']' );
            if ( index1 >= 0 && index2 >= 0 )
            {
                return sValue.substring( index1 + 1, index2 );
            }
        }
        return "";
    }

    /**
     * to get the content of the syntax after being formated
     * @param syntax the syntax to format
     * @param sParams the input parameters
     */
    private static String getFormatedSyntax( String syntax, String[] sParams )
    {
        if ( Pattern.matches( ".*\\{.*\\}.*", syntax ) )
        {
            if ( getTotalOccurence( syntax, '{' ) >= getLength( sParams ) )
            {
                syntax = syntax.replaceFirst( "\\{.*\\}", "" );
            }
            else
            {
                syntax = syntax.replaceFirst( "\\{", "" ).replaceFirst( "\\}", "" );
            }
        }
        else if ( Pattern.matches( ".*\\[.*\\].*", syntax ) )
        {
            int iDiff = getLength( sParams ) - getTotalOccurence( syntax, '[' );
            String sSubMessage = "";
            if ( iDiff > 0 )
            {
                String sContent = getContent( syntax );
                for ( int j = 0; j < iDiff; j++ )
                {
                    sSubMessage += sContent;
                }
            }
            syntax = syntax.replaceFirst( "\\[.*\\]", sSubMessage );
        }
        return syntax;
    }

    /**
     * to get the content before the first space
     * @param sTokens the token to treat
     */
    public static String getHead( String sTokens )
    {
        int index = sTokens.indexOf( ' ' );
        if ( index >= 0 )
        {
            return sTokens.substring( 0, index );
        }
        else
        {
            return sTokens;
        }
    }

    /**
     * to get the number of given parameters
     * @param sParams the input parameters
     */
    private static int getLength( String[] sParams )
    {
        if ( sParams != null )
        {
            return sParams.length;
        }
        else
        {
            return 0;
        }
    }

    /**
     * to get the full content of the message after formating the syntax
     * @param syntax the syntax to format
     * @param sParams the input parameters
     * @return the resulting content of the message
     */
    public static String getMessage( String syntax, String[] sParams )
    {
        int i = 0;
        String sValue,sMessage = getFormatedSyntax( syntax, sParams );

        while ( ( sValue = getValue( sParams, i++ ) ) != null )
        {
            sMessage = replaceToken( sMessage, sValue );
        }
        return sMessage.replaceAll( "<.*>", "" );
    }

    /**
     * to get the content after the first space
     * @param sTokens the token to treat
     */
    public static String getRest( String sTokens )
    {
        if ( sTokens == null || !sTokens.startsWith( " " ) )
        {
            return null;
        }
        else
        {
            byte[] buffer = sTokens.getBytes();
            int i = 0;
            for ( ; i < buffer.length; i++ )
            {
                if ( buffer[i] != '\u0020' )
                {
                    break;
                }
            }
            return sTokens.substring( i );
        }
    }

    /**
     * to get the total of occurences of the character '<' before the given character
     * @param sValue the content to treat
     * @param iChar the character limit of the treatment
     */
    private static int getTotalOccurence( String sValue, int iChar )
    {
        int indexEnd = 0;
        char[] allChar = new char[indexEnd];
        if ( sValue != null )
        {
            indexEnd = sValue.indexOf( iChar );
            if ( indexEnd >= 0 )
            {
                allChar = sValue.substring( 0, indexEnd ).toCharArray();
            }
        }
        int iCount = 0;
        for ( int i = 0; i < allChar.length; i++ )
        {
            if ( allChar[i] == '<' )
            {
                iCount++;
            }
        }
        return iCount;
    }

    /**
     * to get a specific parameter in the given list of parameters
     * @param sParams the list of parameters
     * @param index the index of the parameter to get
     * @return the requested parameter
     */
    private static String getValue( String[] sParams, int index )
    {
        if ( sParams != null && sParams.length > index )
        {
            return sParams[index];
        }
        else
        {
            return null;
        }
    }

    /**
     * to replace all the occurences the old string by the new string in the given value
     * @param sValue the value to treat
     * @param sOldString the old string to replace
     * @param sNewString the new string
     */
    public static String replaceAll( String sValue, String sOldString, String sNewString )
    {
        int index = sValue.indexOf( sOldString );
        if ( index != -1 )
        {
            return sValue.substring( 0, index ) + sNewString + replaceAll( sValue.substring( index + sOldString.length() ), sOldString, sNewString );
        }
        return sValue;
    }

    /**
     * to replace the token like <*> by the given value
     * @param sValue the value to treat
     * @param sNewValue the new value
     */
    private static String replaceToken( String sValue, String sNewValue )
    {
        if ( sValue != null )
        {
            int index1 = sValue.indexOf( '<' );
            int index2 = sValue.indexOf( '>' );
            if ( index1 >= 0 && index2 >= 0 )
            {
                return sValue.substring( 0, index1 ) + sNewValue + sValue.substring( index2 + 1 );
            }
        }
        return sValue;
    }
}

