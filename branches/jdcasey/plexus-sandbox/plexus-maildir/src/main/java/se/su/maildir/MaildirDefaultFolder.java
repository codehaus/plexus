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
import javax.mail.URLName;
import java.util.regex.Pattern;

public class MaildirDefaultFolder extends MaildirFolder
{
    protected MaildirDefaultFolder( MaildirStore store )
    {
        super( store, "" );
    }

    public boolean create( int type )
    {
        return false;
    }

    public boolean delete( boolean recurse )
    {
        return false;
    }

    public String getFullName()
    {
        return "Maildir";
    }

    public Folder getParent()
    {
        return null;
    }

    public String getName()
    {
        return "Maildir";
    }

    public int getType()
    {
        return HOLDS_FOLDERS;
    }

    protected Folder[] list( Pattern re ) throws MessagingException
    {
        Folder[] ret = super.list( re );

        if ( re.matcher( ".INBOX" ).matches() || re.matcher( ".inbox" ).matches() )
        {
            Folder[] tmp = ret;
            ret = new Folder[tmp.length + 1];
            for ( int i = 0; i < tmp.length; i++ )
            {
                ret[i] = tmp[i];
            }
            ret[tmp.length] = store.getFolder( "INBOX" );
        }
        return ret;
    }

    protected String getSubfolderPrefix()
    {
        return "";
    }

    public URLName getURLName()
    {
        return null;
    }

    public boolean renameTo( Folder f )
    {
        return false;
    }
}
