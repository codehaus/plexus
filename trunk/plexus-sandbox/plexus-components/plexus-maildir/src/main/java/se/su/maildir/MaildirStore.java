/*
 * Copyright (c) 2002-2003, Stockholms Universitet
 * (Stockholm University, Stockholm Sweden)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the university nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

/* $Id$ */

package se.su.maildir;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import java.io.File;

public class MaildirStore extends Store
{
    String maildirPath;

    public MaildirStore( Session session, URLName urlName )
    {
        super( session, urlName );

        maildirPath = computeMaildirPath();
    }

    protected String computeMaildirPath()
    {
        if ( url != null )
        {
            String file = url.getFile();
            if ( file != null && file.length() > 0 )
            {
                if ( file.charAt( 0 ) != File.separatorChar )
                    return new File( File.separator + file ).getAbsolutePath();
                else
                    return new File( file ).getAbsolutePath();
            }
        }

        String maildir = session.getProperty( "mail.maildir.directory" );
        if ( maildir != null )
            return new File( maildir ).getAbsolutePath();

        return new File( System.getProperty( "user.home" )
                         + File.separator
                         + "Maildir" ).getAbsolutePath();
    }

    public void debug( String message )
    {
        if ( debug )
            session.getDebugOut().println( message );
    }

    public Folder getDefaultFolder()
    {
        return new MaildirDefaultFolder( this );
    }

    public Folder getFolder( String name )
        throws MessagingException
    {
        if ( "inbox".equalsIgnoreCase( name ) )
            return new MaildirInboxFolder( this );

        if ( MaildirFolder.isFolderNameOk( name ) )
            return new MaildirFolder( this, "." + name );
        else
            throw new MessagingException( name + ": illegal folder name." );
    }

    public Folder getFolder( URLName urlName )
        throws MessagingException
    {
        if ( url.equals( urlName ) )
            return getDefaultFolder();
        else
            throw new MessagingException( "getFolder(URLName): This action is not supported." );
    }

    public String getMaildirPath()
    {
        return maildirPath;
    }

    protected boolean protocolConnect( String host, int port,
                                       String username, String password )
    {
        return true;
    }
}
