/* Created on Oct 4, 2004 */
package org.codehaus.plexus.security.authn;

import org.codehaus.plexus.security.authn.token.Token;

/**
 * @author jdcasey
 */
public interface LoginEngine
{
    
    public static final String ROLE = LoginEngine.class.getName();

    LoginResponse login( LoginRequest request );

    boolean isLoginValid( Token token );

}