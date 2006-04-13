/* Created on Oct 4, 2004 */
package org.codehaus.plexus.security.authn.token;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;


/**
 * @author jdcasey
 */
public class DefaultTokenGenerator
    implements TokenGenerator
{
    
    private static final byte[] DEFAULT_TOKEN_NAMESPACE = "ns-monolithic".getBytes();

    private byte[] tokenNamespace = DEFAULT_TOKEN_NAMESPACE;

    public Token nextToken()
    {
        // not generating string tokens, because I don't want to deal with gobs of String instances
        // for expired tokens floating around.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream daos = new DataOutputStream(baos);
        
        try
        {
            daos.write(tokenNamespace);
            daos.writeLong(System.currentTimeMillis());
        }
        catch ( IOException e )
        {
            throw new RuntimeException("Failed to generate login token.", e);
        }
        
        return new DefaultToken(baos.toByteArray());
    }
    
    public void setTokenNamespace(String tokenNamespace) {
        this.tokenNamespace = tokenNamespace.getBytes();
    }

}
