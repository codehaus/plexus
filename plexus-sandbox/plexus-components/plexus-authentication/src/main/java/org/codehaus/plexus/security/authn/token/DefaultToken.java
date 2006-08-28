/* Created on Oct 4, 2004 */
package org.codehaus.plexus.security.authn.token;

import java.util.Arrays;


/**
 * @author jdcasey
 */
public class DefaultToken
    implements Token
{

    // not using String instances, since I don't want to deal with potential garbage collection 
    // issues...
    private final byte[] tokenId;

    public DefaultToken(byte[] tokenId)
    {
        this.tokenId = tokenId;
    }
    
    public boolean equals(Object obj) {
        if(obj instanceof DefaultToken) {
            return Arrays.equals(tokenId, ((DefaultToken)obj).tokenId);
        }
        
        return false;
    }
    
    public int hashCode() {
        return 13 * tokenId.hashCode();
    }

}
