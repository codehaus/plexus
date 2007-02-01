package org.codehaus.plexus.mailsender.javamail;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.plexus.mailsender.MailSenderException;

import javax.mail.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class JndiJavamailMailSender
    extends AbstractJavamailMailSender
{
    /**
     * @plexus.configuration
     */
    private String jndiSessionName;

    public Session getSession()
        throws MailSenderException
    {
        try
        {
            Context ctx = new InitialContext();
            Session s = (Session) ctx.lookup( jndiSessionName );
            Properties props = new Properties( s.getProperties() );
            if ( "smtps".equals( props.getProperty( AbstractJavamailMailSender.MAIL_TRANSPORT_PROTOCOL ) ) )
            {
                props.put( "mail.smtps.socketFactory.class", DummySSLSocketFactory.class.getName() );
            }
            return Session.getInstance( props, null );
        }
        catch ( NamingException e )
        {
            throw new MailSenderException( "Can't get mail session for component '" + jndiSessionName + "'.", e );
        }
    }

    public String getJndiSessionName()
    {
        return jndiSessionName;
    }

    public void setJndiSessionName( String jndiSessionName )
    {
        this.jndiSessionName = jndiSessionName;
    }
}
