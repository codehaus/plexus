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

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class MailMessage
{
    // ----------------------------------------------------------------------
    // 
    // ----------------------------------------------------------------------

    public static final String TYPE_HTML = "html";

    public static final String TYPE_TEST = "test";

    // ----------------------------------------------------------------------
    // 
    // ----------------------------------------------------------------------

    private String fromAddress;

    private String fromName;

    private Map toAddresses = new HashMap();

    private Map ccAddresses = new HashMap();

    private Map bccAddresses = new HashMap();

    private String subject;

    private String content;

    private String contentType;

    private Map headers = new HashMap();

    private List files;

    private Date sendDate;

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
}