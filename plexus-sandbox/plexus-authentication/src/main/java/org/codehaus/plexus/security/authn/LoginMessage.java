/* Created on Oct 4, 2004 */
package org.codehaus.plexus.security.authn;

import org.codehaus.plexus.security.authn.module.LoginModule;


/**
 * @author jdcasey
 */
public interface LoginMessage
{
    
    String getContent();
    
    boolean isFailureMessage();
    
    boolean isErrorMessage();
    
}
