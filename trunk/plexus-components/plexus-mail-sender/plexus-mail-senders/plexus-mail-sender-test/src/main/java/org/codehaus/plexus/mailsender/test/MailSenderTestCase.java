package org.codehaus.plexus.mailsender.test;

/*
 * LICENSE
 */

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class MockMailSender
	extends PlexusTestCase
{
    private SmtpServer smtpServer;

    public void setUp throws Exception
    {
        super.setUp();
        smtpServer = (SmtpServer) lookup( SmtpServer.ROLE );
        smtpServer.start();
    }

    public void tearDown() throws Exception
    {
        smtpServer.stop();
        super.tearDown();
    }

    /**
    * Get email received by this instance since start up.
    * @return List of String
    */
    public Iterator getReceivedEmail()
    {
        return smtpServer.getReceivedEmail();
    }

    /**
    * Get the number of messages received.
    * @return size of received email list
    */
    public int getReceievedEmailSize()
    {
        return smtpServer.getReceievedEmailSize();
    }
}
