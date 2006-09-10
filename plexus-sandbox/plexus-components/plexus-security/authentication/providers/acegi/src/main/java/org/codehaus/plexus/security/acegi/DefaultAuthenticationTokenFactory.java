package org.codehaus.plexus.security.acegi;

import org.acegisecurity.Authentication;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.codehaus.plexus.security.authentication.AuthenticationException;

import java.util.Map;

/**
 * DefaultAuthenticationTokenFactory:
 *
 * The authTokenType variable can be configured through the configuration section in the components.xml file or
 * barring that can be placed in the tokenMap that is passed in.  If neither of these are used the factory will throw
 * an AuthenticationException.
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 *
 * @plexus.component role="org.codehaus.plexus.security.acegi.AuthenticationTokenFactory"
 */
public class DefaultAuthenticationTokenFactory
    implements AuthenticationTokenFactory
{

    private String authTokenType;

    public Authentication getAuthenticationToken( Map tokenMap )
        throws AuthenticationException
    {

        // try the configured parameter
        String tokenType = authTokenType;

        // if not configured, the check the tokenMap
        if ( tokenType == null )
        {
            tokenType = (String) tokenMap.get( "authTokenType" );
        }

        // if tokenType is still null then throw exception
        if ( tokenType != null )
        {
            if ( UsernamePasswordAuthenticationToken.class.getName().equals( tokenType ) )
            {
                return getUsernamePasswordAuthenticationToken( tokenMap );
            }
            else
            {
                throw new AuthenticationException( "unsupported authentication token type " + tokenType );
            }
        }
        else
        {
            throw new AuthenticationException( "unable to discover authentication token type" );
        }

    }

    private Authentication getUsernamePasswordAuthenticationToken( Map tokenMap )
        throws AuthenticationException
    {
        Object username = tokenMap.get( "username" );
        Object password = tokenMap.get( "password" );

        if ( username == null )
        {
            throw new AuthenticationException( "unable to build authentication token, username missing" );
        }
        else if ( password == null )
        {
            throw new AuthenticationException( "unable to build authentication token, password missing" );
        }
        else
        {
            return new UsernamePasswordAuthenticationToken( username, password );
        }

    }

}
