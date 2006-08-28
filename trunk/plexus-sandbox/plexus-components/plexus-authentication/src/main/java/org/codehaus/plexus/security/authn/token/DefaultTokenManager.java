/* Created on Oct 4, 2004 */
package org.codehaus.plexus.security.authn.token;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jdcasey
 */
public class DefaultTokenManager
    implements TokenManager
{

    private Map validTokens = new HashMap();

    public void validate( Token token )
    {
        validTokens.put( token, new Long( System.currentTimeMillis() ) );
    }

    public boolean isValid( Token token )
    {
        Long expiration = (Long) validTokens.get( token );

        boolean result = true;

        if ( expiration == null )
        {
            result = false;
        }
        else if ( expiration.longValue() < System.currentTimeMillis() )
        {
            result = false;
            validTokens.remove( token );
        }

        return result;
    }

}