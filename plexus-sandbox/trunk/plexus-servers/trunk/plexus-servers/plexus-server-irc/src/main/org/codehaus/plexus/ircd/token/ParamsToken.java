/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.token;

import org.codehaus.plexus.ircd.exception.FatalException;
import org.codehaus.plexus.ircd.utils.Utilities;

import java.util.Vector;

public class ParamsToken extends IRCToken
{

    private MiddleToken[] middleTokens;
    private TrailingToken trailingToken;

    /**
     * @param token content of the token
     */
    protected ParamsToken( String token )
    {
        super( token );
    }

    /**
     * to get the middle tokens
     */
    public MiddleToken[] getMiddleTokens()
    {
        return middleTokens;
    }

    /**
     * to parse the content of the content and to get the corresponding ParamsToken
     * @param sToken the token to parse
     * @return the corresponding token
     */
    public static IRCToken getToken( String sToken )
    {
        return getToken( new ParamsToken( sToken ) );
    }

    /**
     * to get the trailing token
     */
    public TrailingToken getTrailingToken()
    {
        return trailingToken;
    }

    /**
     * to parse the given tokens
     * @param sTokens the tokens to parse
     * @throws FatalException parsing's exception
     */
    public void parseToken( String sTokens ) throws FatalException
    {
        if ( sTokens != null )
        {
            Vector vAllToken = new Vector();
            sTokens = Utilities.getRest( sTokens );
            while ( sTokens != null && !sTokens.equals( VOID ) )
            {
                String sHead = Utilities.getHead( sTokens );
                if ( sHead.startsWith( DOUBLE_POINTS ) )
                {
                    setTrailingToken( (TrailingToken) TrailingToken.getToken( sTokens.substring( 1 ) ) );
                    break;
                }
                else
                {
                    IRCToken ircToken = MiddleToken.getToken( sHead );
                    if ( ircToken != null )
                    {
                        vAllToken.addElement( ircToken );
                    }
                }
                sTokens = Utilities.getRest( sTokens.substring( sHead.length() ) );
            }
            if ( vAllToken.size() != 0 )
            {
                MiddleToken[] toTokens = new MiddleToken[vAllToken.size()];
                setMiddleTokens( (MiddleToken[]) vAllToken.toArray( toTokens ) );
            }
        }
        else
        {
            throw new FatalException( "The token 'paramsToken' is null !!" );
        }
    }

    /**
     * to set the middle tokens
     */
    private void setMiddleTokens( MiddleToken[] middleTokens )
    {
        this.middleTokens = middleTokens;
    }

    /**
     * to set the trailing token
     */
    private void setTrailingToken( TrailingToken trailingToken )
    {
        this.trailingToken = trailingToken;
    }

    /**
     * to get the content of the token
     */
    public String toString()
    {
        StringBuffer buffer = new StringBuffer( 10000 );
        if ( middleTokens != null )
        {
            for ( int i = 0; i < middleTokens.length; i++ )
            {
                buffer.append( " " + middleTokens[i].toString() );
            }
        }
        if ( trailingToken != null )
        {
            buffer.append( " :" + trailingToken.toString() );
        }
        return buffer.toString();
    }
}
