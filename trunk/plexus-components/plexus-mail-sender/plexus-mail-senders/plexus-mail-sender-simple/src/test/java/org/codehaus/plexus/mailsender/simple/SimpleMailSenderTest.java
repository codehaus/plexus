package org.codehaus.plexus.mailsender.simple;

/*
 * LICENSE
 */

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.mailsender.MailSender;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class SimpleMailSenderTest
	extends PlexusTestCase
{
    public void testDefaultComponentConfiguration()
    	throws Exception
    {
        MailSender mailSender = (MailSender) lookup( MailSender.ROLE );

        assertNotNull( mailSender );

        assertTrue( mailSender instanceof SimpleMailSender );

        SimpleMailSender simpleMailSender = (SimpleMailSender) mailSender;

        assertEquals( "localhost", simpleMailSender.getSmtpHost() );

        assertEquals( 25, simpleMailSender.getSmtpPort() );
    }
}
