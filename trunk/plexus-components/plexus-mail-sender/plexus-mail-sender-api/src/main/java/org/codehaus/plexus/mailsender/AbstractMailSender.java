package org.codehaus.plexus.mailsender;

/*
 * LICENSE
 */

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public abstract class AbstractMailSender extends AbstractLogEnabled
    implements MailSender, Initializable
{
    // ----------------------------------------------------------------------
    // 
    // ----------------------------------------------------------------------

    private final static int DEFAULT_SMTP_PORT = 25;

    // ----------------------------------------------------------------------
    // 
    // ----------------------------------------------------------------------

    private String fromAddress;

    private String fromName;

    private Map toAddresses;

    private Map ccAddresses;

    private Map bccAddresses;

    private String subject;

    private String content;

    private String contentType;

    private Map headers;

    private List files;

    private Date sendDate;

    private String smtpHost;

    private int smtpPort;

    private String username;

    // ----------------------------------------------------------------------
    // 
    // ----------------------------------------------------------------------

    public String getFromAddress()
    {
        return fromAddress;
    }

    public void setFromAddress( String fromAddress)
    {
        this.fromAddress = fromAddress;
    }

    public String getFromName()
    {
        return fromName;
    }

    public void setFromName( String fromName )
    {
        this.fromName = fromName;
    }

    public Map getToAddresses()
    {
        return toAddresses;
    }

    public void addTo( String toName, String toAddress )
    {
        toAddresses.put( toName, toAddress );
    }

    public Map getCcAddresses()
    {
        return ccAddresses;
    }

    public void addCc( String ccName, String ccAddress )
    {
        ccAddresses.put( ccName, ccAddress );
    }

    public Map getBccAddresses()
    {
        return bccAddresses;
    }

    public void addBcc( String bccName, String bccAddress )
    {
        bccAddresses.put( bccName, bccAddress );
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject( String subject )
    {
        this.subject = subject;
    }

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

    public Date getSendDate()
    {
        return sendDate;
    }

    public void setSendDate( Date sendDate )
    {
        this.sendDate = sendDate;
    }

    public Map getHeaders()
    {
        return headers;
    }

    public void addHeader( String headerName, String headerValue )
    {
        headers.put( headerName, headerValue );
    }

    public List getFiles()
    {
        return files;
    }

    public void addFile( File file )
    {
        files.add( file );
    }

    public String getSmtpHost()
    {
        return smtpHost;
    }

    public void setSmtpHost( String smtpHost )
    {
        this.smtpHost = smtpHost;
    }

    public int getSmtpPort()
    {
        return smtpPort;
    }

    public void setSmtpPort( int smtpPort )
    {
        this.smtpPort = smtpPort;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public void send( String subject, String content, String toAddress, String toName, String fromAddress, String fromName )
         throws MailSenderException
    {
        send( subject, content, toAddress, toName, fromAddress, fromName, new HashMap() );
    }

    public void send( String subject, String content, String toAddress, String toName, String fromAddress, String fromName, Map headers )
         throws MailSenderException
    {
        setSubject( subject );

        setContent( content );

        setFromAddress( fromAddress );

        setFromName( fromName );

        addTo( toName, toAddress );

        for( Iterator iter = headers.keySet().iterator(); iter.hasNext(); )
        {
            String key = (String) iter.next();

            addHeader( key, (String) headers.get( key ) );
        }

        send();
    }

    protected String makeEmailAddress( String address, String name )
    {
        if ( name == null || name.length() == 0 )
        {
            return "<" + address + ">";
        }

        return name + "<" + address + ">";
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
    	throws Exception
    {
        if ( smtpHost == null || smtpHost.length() == 0 )
        {
            throw new MailSenderException( "Error in configuration: Missing smtp-host." );
        }

        if ( smtpPort == 0 )
        {
            smtpPort = DEFAULT_SMTP_PORT;
        }

        if ( toAddresses == null )
        {
            toAddresses = new HashMap();
        }

        if ( ccAddresses == null )
        {
            ccAddresses = new HashMap();
        }

        if ( bccAddresses == null )
        {
            bccAddresses = new HashMap();
        }

        if ( headers == null )
        {
            headers = new HashMap();
        }
    }
}
