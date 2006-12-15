/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.token;

import org.codehaus.plexus.ircd.exception.FatalException;

import java.util.Vector;
import java.util.regex.Pattern;

public class TargetToken extends IRCToken
{

    private ToToken[] toTokens;

    /**
     * @param token content of the token
     */
    protected TargetToken( String token )
    {
        super( token );
    }

    /**
     * to parse the content of the content and to get the corresponding TargetToken
     * @param sToken the token to parse
     * @return the corresponding token
     */
    public static IRCToken getToken( String sToken )
    {
        return getToken( new TargetToken( sToken ) );
    }

    /**
     * to get the list of content token
     */
    public ToToken[] getToTokens()
    {
        return toTokens;
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
            String[] sAllToken = Pattern.compile( "," ).split( sTokens );
            if ( sAllToken != null )
            {
                Vector vAllToken = new Vector();
                for ( int i = 0; i < sAllToken.length; i++ )
                {
                    IRCToken currentIRCToken = ToToken.getToken( sAllToken[i] );
                    if ( currentIRCToken != null )
                    {
                        vAllToken.addElement( currentIRCToken );
                    }
                }
                if ( vAllToken.size() != 0 )
                {
                    ToToken[] toTokens = new ToToken[vAllToken.size()];
                    setToTokens( (ToToken[]) vAllToken.toArray( toTokens ) );
                }
            }
        }
        else
        {
            throw new FatalException( "The token 'targetToken' is null !!" );
        }
    }

    /**
     * to set the list of content token
     */
    private void setToTokens( ToToken[] toTokens )
    {
        this.toTokens = toTokens;
    }

    /**
     * to get the content of the token
     */
    public String toString()
    {
        StringBuffer buffer = new StringBuffer( 100 );
        if ( toTokens != null )
        {
            for ( int i = 0; i < toTokens.length; i++ )
            {
                if ( i > 0 )
                {
                    buffer.append( ',' );
                }
                buffer.append( toTokens[i].toString() );
            }
        }
        return buffer.toString();
    }
}
