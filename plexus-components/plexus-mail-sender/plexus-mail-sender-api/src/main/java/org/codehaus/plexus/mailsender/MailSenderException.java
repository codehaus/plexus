package org.codehaus.plexus.mailsender;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class MailSenderException
	extends Exception
{
    public MailSenderException( String message )
    {
        super( message );
    }

    public MailSenderException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
