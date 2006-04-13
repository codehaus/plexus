/* Created on Oct 4, 2004 */
package org.codehaus.plexus.security.authn.module;

import java.util.List;

import org.codehaus.plexus.security.authn.LoginMessage;


/**
 * @author jdcasey
 */
public interface LoginModuleResponse
{

    boolean wasSuccessful();
    
    List getMessages();
    
    void addMessage(LoginMessage message);
    
}
