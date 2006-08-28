/* Created on Sep 29, 2004 */
package org.codehaus.plexus.security.authn;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.codehaus.plexus.security.authn.module.LoginModule;
import org.codehaus.plexus.security.authn.module.LoginModuleResponse;
import org.codehaus.plexus.security.authn.token.Token;
import org.codehaus.plexus.security.authn.token.TokenGenerator;
import org.codehaus.plexus.security.authn.token.TokenManager;

/**
 * @author jdcasey
 */
public class DefaultLoginEngine
    implements LoginEngine
{

    private Set loginModules;

    private TokenManager tokenManager;

    private TokenGenerator tokenGenerator;

    public LoginResponse login( LoginRequest request )
    {
        DefaultLoginResponse response = new DefaultLoginResponse();

        for ( Iterator it = loginModules.iterator(); it.hasNext(); )
        {
            LoginModule module = (LoginModule) it.next();
            LoginModuleResponse modResponse = module.login( request );

            response.addModuleResponse( modResponse );

            /*
             * Not sure we want to make the login process give away this info...
             * Timing info can give clues about one's progress while trying to
             * compromise the system. if(!response.wasSuccessful()) { break; }
             */
        }

        if ( response.wasSuccessful() )
        {
            Token token = tokenGenerator.nextToken();
            tokenManager.validate( token );
            response.setToken( token );
        }

        return response;
    }

    public boolean isLoginValid( Token token )
    {
        return tokenManager.isValid( token );
    }

    public void setLoginModules( Set loginModules )
    {
        this.loginModules = new HashSet( loginModules );
    }

    public void setTokenManager( TokenManager tokenManager )
    {
        this.tokenManager = tokenManager;
    }

    public void setTokenGenerator( TokenGenerator tokenGenerator )
    {
        this.tokenGenerator = tokenGenerator;
    }

}