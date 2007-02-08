/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.token;

import org.codehaus.plexus.ircd.exception.FatalException;

import java.util.regex.Pattern;

public class ModeToken extends IRCToken
{

    private boolean addMode;
    private char firstMode;
    private char secondMode;
    private char thirdMode;

    /**
     * @param token content of the token
     */
    protected ModeToken( String token )
    {
        super( token );
        addMode = true;
    }

    /**
     * to know if it is on add mode
     */
    public boolean getAddMode()
    {
        return addMode;
    }

    /**
     * to get the first given mode
     */
    public char getFirstMode()
    {
        return firstMode;
    }

    /**
     * to get the second given mode
     */
    public char getSecondMode()
    {
        return secondMode;
    }

    /**
     * to get the third given mode
     */
    public char getThirdMode()
    {
        return thirdMode;
    }

    /**
     * to parse the content of the content and to get the corresponding ModeToken
     * @param sToken the token to parse
     * @return the corresponding token
     */
    public static IRCToken getToken( String sToken )
    {
        return getToken( new ModeToken( sToken ) );
    }

    /**
     * to parse the given token
     * @param sToken the token to parse
     * @throws FatalException parsing's exception
     */
    public void parseToken( String sToken ) throws FatalException
    {
        if ( sToken != null && sToken.length() >= 2 )
        {
            if ( Pattern.matches( "[+|-][o|p|s|i|t|n|m|l|b|v|k|w]{1,3}", sToken ) )
            {
                setAddMode( sToken.charAt( 0 ) == PLUS_CHAR );
                setFirstMode( sToken.charAt( 1 ) );
                if ( sToken.length() >= 3 )
                {
                    setSecondMode( sToken.charAt( 2 ) );
                }
                if ( sToken.length() >= 4 )
                {
                    setThirdMode( sToken.charAt( 3 ) );
                }
            }
            else
            {
                throw new FatalException( "The token 'modeToken' is incorrect !!" );
            }
        }
        else
        {
            throw new FatalException( "The token 'modeToken' is incomplete !!" );
        }
    }

    /**
     * to set the add mode
     */
    private void setAddMode( boolean isAddMode )
    {
        addMode = isAddMode;
    }

    /**
     * to set the first given mode
     */
    private void setFirstMode( char firstMode )
    {
        this.firstMode = firstMode;
    }

    /**
     * to set the second given mode
     */
    private void setSecondMode( char secondMode )
    {
        this.secondMode = secondMode;
    }

    /**
     * to set the third given mode
     */
    private void setThirdMode( char thirdMode )
    {
        this.thirdMode = thirdMode;
    }

    /**
     * to get the content of the token
     */
    public String toString()
    {
        StringBuffer buffer = new StringBuffer( 10 );
        buffer.append( addMode ? PLUS_CHAR : MINUS_CHAR );
        if ( firstMode != NULL_CHAR )
        {
            buffer.append( firstMode );
        }
        if ( secondMode != NULL_CHAR )
        {
            buffer.append( secondMode );
        }
        if ( thirdMode != NULL_CHAR )
        {
            buffer.append( thirdMode );
        }
        return buffer.toString();
    }
}

