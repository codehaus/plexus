/* Created on Oct 4, 2004 */
package org.codehaus.plexus.security.authn.module;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.plexus.security.authn.LoginMessage;


/**
 * @author jdcasey
 */
public class DefaultLoginModuleResponse
    implements LoginModuleResponse
{

    private boolean successful = false;
    private List messages = new LinkedList();

    public boolean wasSuccessful()
    {
        return successful;
    }
    
    public void setSuccessful()
    {
        this.successful = true;
    }
    
    public void setFailed()
    {
        this.successful = false;
    }

    public List getMessages()
    {
        return Collections.unmodifiableList(messages);
    }

    public void addMessage( LoginMessage message )
    {
        messages.add(message);
    }

}
