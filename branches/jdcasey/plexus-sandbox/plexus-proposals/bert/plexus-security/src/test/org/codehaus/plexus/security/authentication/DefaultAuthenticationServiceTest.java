package org.codehaus.plexus.security.authentication;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.security.authentication.pap.PAPToken;

/**
  * 
  * <p>Created on 21/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class DefaultAuthenticationServiceTest extends PlexusTestCase
{

    /**
     * @param arg0
     */
    public DefaultAuthenticationServiceTest(String arg0)
    {
        super(arg0);
    }

    public void test() throws Exception
    {
        AuthenticationService auth = (AuthenticationService) lookup(AuthenticationService.ROLE);
        //passing authentication

        //tom
        String id = auth.authenticate(newToken("tom", "tomtheman"));
        assertEquals("Authenticatin with tom did not return the correct user id", "1", id);
        //dick
        id = auth.authenticate(newToken("dick", "dicks123password"));
        assertEquals("Authenticatin with dick did not return the correct user id", "2", id);
        //harry
        id = auth.authenticate(newToken("harry", "themaster"));
        assertEquals("Authenticatin with harry did not return the correct user id", "3", id);
        
        release( auth );
    }

    private PAPToken newToken(String userName, String password)
    {
        return new PAPToken(userName, password);
    }

}
