package org.codehaus.plexus.mailsender.test;

/**
 * License
 */

import com.dumbster.smtp.SimpleSmtpServer;

import java.util.Iterator;

public class MockSmtpServer
    implements SmtpServer, Initilizable, Startable
{
    private SimpleSmtpServer smtpServer;

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

    public void initialize()
        throws Exception
    {
        smtpServer = new SimpleSmtpServer( port );
    }

    public void start()
        throws Exception
    {
        smtpServer.start();
    }

    public void stop()
        throws Exception
    {
        smtpServer.stop();
    }
}