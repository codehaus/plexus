/* Created on Sep 29, 2004 */
package org.codehaus.plexus.security.authn;

import java.util.List;
import java.util.Set;

import org.codehaus.plexus.security.authn.module.LoginModuleResponse;
import org.codehaus.plexus.security.authn.token.Token;

/**
 * @author jdcasey
 */
public interface LoginResponse
{
    
    boolean wasSuccessful();
    
    List getMessages();
    
    void addModuleResponse(LoginModuleResponse response);
    
    Token getToken();
    
}
