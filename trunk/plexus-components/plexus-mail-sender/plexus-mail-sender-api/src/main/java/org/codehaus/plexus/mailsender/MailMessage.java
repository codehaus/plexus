package org.codehaus.plexus.mailsender;

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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public class MailMessage
{
    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public static final String TYPE_HTML = "html";

    public static final String TYPE_TEXT = "text";

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public final static class Address
    {
        private String mailbox;

        private String name;

        public Address( String mailbox, String name )
            throws MailSenderException
        {
            this( mailbox );

            this.name = name;
        }

        public Address( String mailbox )
            throws MailSenderException
        {
            if ( mailbox == null || mailbox.trim().length() == 0 )
            {
                throw new MailSenderException( "The mailbox cannot be null." );
            }

            this.mailbox = mailbox;
        }

        public String getMailbox()
        {
            return mailbox;
        }

        public String getName()
        {
            return name;
        }

        public String getRfc2822Address()
        {
            if ( name == null || name.trim().length() == 0 )
            {
                return "<" + mailbox + ">";
            }

            return "\"" + name + "\" <" + mailbox + ">";
        }

        public String toString()
        {
            return getRfc2822Address();
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private Address from;

    private List toAddresses = new ArrayList();

    private List ccAddresses = new ArrayList();

    private List bccAddresses = new ArrayList();

    private String subject;

    private String content;

    private String contentType = TYPE_TEXT;

    private Map headers = new HashMap();

    private Date sendDate;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public Address getFrom()
    {
        return from;
    }

    public void setFrom( String mailbox, String name )
        throws MailSenderException
    {
        from = new Address( mailbox, name );
    }

    public void setFrom( Address from )
    {
        this.from = from;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public List getToAddresses()
    {
        return toAddresses;
    }

    public void addTo( String mailbox, String name )
        throws MailSenderException
    {
        toAddresses.add( new Address( mailbox, name ) );
    }

    public void addTo( Address to )
        throws MailSenderException
    {
        toAddresses.add( to );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public List getCcAddresses()
    {
        return ccAddresses;
    }

    public void addCc( String mailbox, String name )
        throws MailSenderException
    {
        ccAddresses.add( new Address( mailbox, name ) );
    }

    public void addCc( Address cc )
        throws MailSenderException
    {
        ccAddresses.add( cc );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public List getBccAddresses()
    {
        return bccAddresses;
    }

    public void addBcc( String mailbox, String name )
        throws MailSenderException
    {
        bccAddresses.add( new Address( mailbox, name ) );
    }

    public void addBcc( Address bcc )
        throws MailSenderException
    {
        bccAddresses.add( bcc );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public String getSubject()
    {
        return subject;
    }

    public void setSubject( String subject )
    {
        this.subject = subject;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public String getContent()
    {
        return content;
    }

    public void setContent( String content )
    {
        this.content = content;
    }

    public String getContentType()
    {
        return contentType;
    }

    public void setContentType( String contentType )
    {
        this.contentType = contentType;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public Date getSendDate()
    {
        return sendDate;
    }

    public void setSendDate( Date sendDate )
    {
        this.sendDate = sendDate;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public Map getHeaders()
    {
        return headers;
    }

    public void addHeader( String headerName, String headerValue )
    {
        headers.put( headerName, headerValue );
    }
}
