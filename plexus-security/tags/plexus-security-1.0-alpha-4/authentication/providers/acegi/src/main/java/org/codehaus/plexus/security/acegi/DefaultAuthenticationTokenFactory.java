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

    private static final String TOKEN_PASSWORD = "password";

    private static final String TOKEN_USERNAME = "username";

    private static final String TOKEN_AUTH_TOKEN_TYPE = "authTokenType";

    private String authTokenType;

    public Authentication getAuthenticationToken( Map tokenMap )
        throws AuthenticationException
    {

        // try the configured parameter
        String tokenType = authTokenType;

        // if not configured, the check the tokenMap
        if ( tokenType == null )
        {
            tokenType = (String) tokenMap.get( TOKEN_AUTH_TOKEN_TYPE );
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
                throw new AuthenticationException( "Unsupported authentication token type " + tokenType );
            }
        }
        else
        {
            throw new AuthenticationException( "Unable to discover authentication token type" );
        }

    }

    private Authentication getUsernamePasswordAuthenticationToken( Map tokenMap )
        throws AuthenticationException
    {
        Object username = tokenMap.get( TOKEN_USERNAME );
        Object password = tokenMap.get( TOKEN_PASSWORD );

        if ( username == null )
        {
            throw new AuthenticationException( "Unable to build authentication token, missing token '" + TOKEN_USERNAME
                + "'" );
        }
        else if ( password == null )
        {
            throw new AuthenticationException( "Unable to build authentication token, missing token '" + TOKEN_PASSWORD
                + "'" );
        }
        else
        {
            return new UsernamePasswordAuthenticationToken( username, password );
        }

    }

}
