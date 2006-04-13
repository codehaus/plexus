package org.codehaus.plexus.security.authentication.pap;

import java.io.StringReader;

import junit.framework.TestCase;

import org.codehaus.plexus.configuration.XmlPullConfigurationBuilder;
import org.codehaus.plexus.logging.ConsoleLogger;
import org.codehaus.plexus.security.authentication.AuthenticationException;
import org.codehaus.plexus.util.ResourceUtils;

/**
  * 
  * <p>Created on 20/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class XMLPAPAuthenticationHandlerTest extends TestCase
{

    /**
     * 
     */
    public XMLPAPAuthenticationHandlerTest()
    {
        super();
    }

    /**
     * @param arg0
     */
    public XMLPAPAuthenticationHandlerTest(String arg0)
    {
        super(arg0);
    }

    public void test() throws Exception
    {

        String file = ResourceUtils.getRelativeResourcePath(this.getClass(),"pap-users.xml");
        String s =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><configuration><userFile>"
                + file
                + "</userFile></configuration>";
        XmlPullConfigurationBuilder builder = new XmlPullConfigurationBuilder();
        XMLPAPAuthenticationHandler handler = new XMLPAPAuthenticationHandler();
		ConsoleLogger logger = new ConsoleLogger(ConsoleLogger.LEVEL_ERROR);
		handler.enableLogging(logger);
        handler.configure(builder.parse(new StringReader(s)));

        //passing authentication

        //tom
        String id = handler.authenticate(newToken("tom", "tomtheman"));
        assertEquals("Authenticatin with tom did not return the correct user id", "1", id);
        //dick
        id = handler.authenticate(newToken("dick", "dicks123password"));
        assertEquals("Authenticatin with dick did not return the correct user id", "2", id);
        //harry
        id = handler.authenticate(newToken("harry", "themaster"));
        assertEquals("Authenticatin with harry did not return the correct user id", "3", id);

        //failiing authentication
        boolean failed = false;
        try
        {
            //try authentication with uppercase
            id = handler.authenticate(newToken("tom", "tomtheMan"));
        }
        catch (AuthenticationException e)
        {
            failed = true;
        }
        assertTrue("Authentication should of failed", failed);

        failed = false;
        try
        {
            //try authentication with incorrect username
            id = handler.authenticate(newToken("toms", "tomtheman"));
        }
        catch (AuthenticationException e)
        {
            failed = true;
        }
        assertTrue("Authentication should of failed", failed);

    }

    private PAPToken newToken(String userName, String password)
    {
        return new PAPToken(userName, password);
    }

}
