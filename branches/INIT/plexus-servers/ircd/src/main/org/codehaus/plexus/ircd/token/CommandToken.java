/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.token;

import org.codehaus.plexus.ircd.exception.FatalException;

import java.util.regex.Pattern;

public class CommandToken extends IRCToken
{

    private String letterValue;
    private String numberValue;

    /**
     * @param token content of the token
     */
    protected CommandToken( String token )
    {
        super( token );
    }

    /**
     * to get the letter value
     */
    public String getLetterValue()
    {
        return letterValue;
    }

    /**
     * to get the number value
     */
    public String getNumberValue()
    {
        return numberValue;
    }

    /**
     * to parse the content of the content and to get the corresponding CommandToken
     * @param sToken the token to parse
     * @return the corresponding token
     */
    public static IRCToken getToken( String sToken )
    {
        return getToken( new CommandToken( sToken ) );
    }

    /**
     * to parse the given token
     * @param sToken the token to parse
     * @throws FatalException parsing's exception
     */
    public void parseToken( String sToken ) throws FatalException
    {
        if ( sToken != null )
        {
            if ( Pattern.matches( "[a-zA-Z]+", sToken ) )
            {
                setLetterValue( sToken );
            }
            else if ( Pattern.matches( "\\d\\d\\d", sToken ) )
            {
                setNumberValue( sToken );
            }
            else
            {
                throw new FatalException( "The token 'commandToken' is incorrect !!" );
            }
        }
        else
        {
            throw new FatalException( "The token 'commandToken' is null !!" );
        }
    }

    /**
     * to set the letter value
     */
    private void setLetterValue( String letterValue )
    {
        this.letterValue = letterValue;
    }

    /**
     * to set the number value
     */
    private void setNumberValue( String numberValue )
    {
        this.numberValue = numberValue;
    }

    /**
     * to get the content of the token
     */
    public String toString()
    {
        if ( letterValue != null )
        {
            return letterValue;
        }
        else if ( numberValue != null )
        {
            return numberValue;
        }
        else
        {
            return VOID;
        }
    }
}
