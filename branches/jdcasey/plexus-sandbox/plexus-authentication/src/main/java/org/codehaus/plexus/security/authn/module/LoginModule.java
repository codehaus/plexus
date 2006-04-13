/* Created on Sep 29, 2004 */
package org.codehaus.plexus.security.authn.module;

import java.util.Map;

import org.codehaus.plexus.security.authn.LoginRequest;
import org.codehaus.plexus.security.authn.LoginResponse;

/**
 * @author jdcasey
 */
public interface LoginModule
{
    
    public static final String ROLE = LoginModule.class.getName();
    
    LoginModuleResponse login(LoginRequest request);
    
}
