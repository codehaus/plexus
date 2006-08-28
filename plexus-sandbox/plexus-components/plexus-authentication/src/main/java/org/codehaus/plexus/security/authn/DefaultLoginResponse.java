/* Created on Oct 4, 2004 */
package org.codehaus.plexus.security.authn;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.plexus.security.authn.module.LoginModuleResponse;
import org.codehaus.plexus.security.authn.token.Token;


/**
 * @author jdcasey
 */
public class DefaultLoginResponse
    implements LoginResponse
{
    
    private List messages = new LinkedList();
    private boolean successful = true;
    private Token identityToken;

    public boolean wasSuccessful()
    {
        return successful;
    }

    public List getMessages()
    {
        return Collections.unmodifiableList(messages);
    }
    
    public void setToken(Token identityToken)
    {
        this.identityToken = identityToken;
    }

    public Token getToken()
    {
        return identityToken;
    }

    public void addModuleResponse( LoginModuleResponse response )
    {
        successful = successful && response.wasSuccessful();
        
        messages.addAll(response.getMessages());
    }

}
