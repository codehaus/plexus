/* Created on Oct 4, 2004 */
package org.codehaus.plexus.security.authn.token;

/**
 * @author jdcasey
 */
public interface TokenManager
{

    public static final String ROLE = TokenManager.class.getName();

    boolean isValid( Token token );

    void validate( Token token );

}