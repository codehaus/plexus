package org.codehaus.plexus.mailsender.test;

/*
 * LICENSE
 */

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.mailsender.AbstractMailSender;
import org.codehaus.plexus.mailsender.MailMessage;
import org.codehaus.plexus.mailsender.MailSenderException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class MockMailSender
	extends AbstractMailSender
{
    private List messages = new ArrayList();

    /**
    * Get email received by this instance since start up.
    * @return List of String
    */
    public Iterator getReceivedEmail()
    {
        return messages.iterator();
    }

    /**
    * Get the number of messages received.
    * @return size of received email list
    */
    public int getReceievedEmailSize()
    {
        return messages.size();
    }

    // ----------------------------------------------------------------------
    // MailSender Implementation
    // ----------------------------------------------------------------------

    public void send( MailMessage message ) throws MailSenderException
	{
	    messages.add( message );
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
    	throws Exception
    {
        if ( getSmtpPort() == 0 )
        {
            setSmtpPort( AbstractMailSender.DEFAULT_SMTP_PORT );
        }
    }
}
