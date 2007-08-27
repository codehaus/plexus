package org.codehaus.plexus.redback.authentication.ldap;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.redback.authentication.Authenticator;

public class LdapBindAuthenticatorTest extends PlexusTestCase
{
    

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {        
        super.tearDown();
    }

    public void testAuthentication() throws Exception
    {
        LdapBindAuthenticator authnr = (LdapBindAuthenticator)lookup( Authenticator.ROLE, "ldap" );
        
        
        
        //authnr.authenticate( s );
    }
}
