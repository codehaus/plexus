/* Created on Oct 4, 2004 */
package org.codehaus.plexus.security.authn.token;


/**
 * @author jdcasey
 */
public interface TokenGenerator
{
    
    public static final String ROLE = TokenGenerator.class.getName();

    Token nextToken();
    
}
