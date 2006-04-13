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

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.IllegalWriteException;
import javax.mail.Message;
import javax.mail.MessageRemovedException;
import javax.mail.MessagingException;
import javax.mail.event.MessageChangedEvent;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.util.Date;
import java.util.Enumeration;

public class MaildirMessage extends MimeMessage
{
    protected File file;

    private static long uniqueCount = 0;
    private static String hostname;
    private static long starttime = ( new Date().getTime() ) / 1000;

    private boolean loaded = false;
    private int hdrSize;

    public MaildirMessage( MaildirFolder folder,
                           int msgno,
                           File file,
                           boolean recent )
    {
        super( folder, msgno );

        this.file = file;

        if ( recent )
            flags.add( Flags.Flag.RECENT );

        String[] pair = splitInfo( file.getName() );

        if ( pair[1] != null )
            flags.add( getFlagsFromInfo( pair[1] ) );
    }

    public static synchronized String createUniqueName( Message m )
        throws MessagingException
    {
        uniqueCount++;

        String info = getInfoFromFlags( m.getFlags() );

        long time = ( new Date() ).getTime();

        if ( hostname == null )
        {
            try
            {
                hostname = InetAddress.getLocalHost().getHostName();
            }
            catch ( Exception e )
            {
                hostname = "unknown-host";
            }
        }

        return ( starttime + ".#" + time + '_'
            + uniqueCount + '.' + hostname + info );
    }

    protected void debug( String message )
    {
        ( (MaildirFolder) folder ).debug( message );
    }

    public Enumeration getAllHeaders()
        throws MessagingException
    {
        load();
        return super.getAllHeaders();
    }

    public Enumeration getMatchingHeaders( String[] names )
        throws MessagingException
    {
        load();
        return super.getMatchingHeaders( names );
    }

    public Enumeration getNonMatchingHeaders( String[] names )
        throws MessagingException
    {
        load();
        return super.getNonMatchingHeaders( names );
    }

    public Enumeration getAllHeaderLines()
        throws MessagingException
    {
        load();
        return super.getAllHeaderLines();
    }

    public Enumeration getMatchingHeaderLines( String[] names )
        throws MessagingException
    {
        load();
        return super.getMatchingHeaderLines( names );
    }

    public Enumeration getNonMatchingHeaderLines( String[] names )
        throws MessagingException
    {
        load();
        return super.getNonMatchingHeaderLines( names );
    }

    public String[] getHeader( String name )
        throws MessagingException
    {
        load();
        return super.getHeader( name );
    }

    public String getHeader( String name, String delimiter )
        throws MessagingException
    {
        load();
        return super.getHeader( name, delimiter );
    }

    public static Flags getFlagsFromInfo( String info )
    {
        Flags f = new Flags();

        int len = info.length();
        for ( int i = 3; i < len; i++ )
        {
            switch ( info.charAt( i ) )
            {
                case 'D':
                    f.add( Flags.Flag.DRAFT );
                    break;
                case 'F':
                    f.add( Flags.Flag.FLAGGED );
                    break;
                case 'R':
                    f.add( Flags.Flag.ANSWERED );
                    break;
                case 'S':
                    f.add( Flags.Flag.SEEN );
                    break;
                case 'T':
                    f.add( Flags.Flag.DELETED );
                    break;
            }
        }
        return f;
    }

    public static String getInfoFromFlags( Flags flags )
    {
        boolean no_flags = true;
        StringBuffer info = new StringBuffer( ":2," );

        if ( flags.contains( Flags.Flag.DRAFT ) )
        {
            info.append( 'D' );
            no_flags = false;
        }
        if ( flags.contains( Flags.Flag.FLAGGED ) )
        {
            info.append( 'F' );
            no_flags = false;
        }
        if ( flags.contains( Flags.Flag.ANSWERED ) )
        {
            info.append( 'R' );
            no_flags = false;
        }
        if ( flags.contains( Flags.Flag.SEEN ) )
        {
            info.append( 'S' );
            no_flags = false;
        }
        if ( flags.contains( Flags.Flag.DELETED ) )
        {
            info.append( 'T' );
            no_flags = false;
        }

        if ( no_flags )
            return "";
        else
            return info.toString();
    }

    protected synchronized InputStream getContentStream()
        throws MessagingException
    {
        load();
        return new ByteArrayInputStream( content, hdrSize,
                                         content.length - hdrSize );
    }

    protected synchronized void load()
        throws MessagingException
    {
        if ( !loaded )
        {
            try
            {
                RandomAccessFile f = new RandomAccessFile( file, "r" );
                content = new byte[(int) f.length()];
                f.readFully( content );
                f.close();
                ByteArrayInputStream is = new ByteArrayInputStream( content );
                headers = new InternetHeaders( is );
                hdrSize = content.length - is.available();
                loaded = true;
            }
            catch ( IOException e )
            {
                throw new MessagingException( e.toString() );
            }
        }
    }

    protected void setExpunged( boolean expunged )
    {
        debug( "I am expunged: " + msgnum );

        file.delete();
        super.setExpunged( expunged );
    }

    public synchronized void setFlags( Flags cflags, boolean set )
        throws MessagingException
    {
        if ( isExpunged() )
            throw new MessageRemovedException();

        if ( folder.getMode() == Folder.READ_ONLY )
            throw new IllegalStateException();

        debug( "in setFlags(flags, set)" );

        Flags nflags = new Flags( flags );

        Flags.Flag[] sflags = cflags.getSystemFlags();

        boolean changed = false;

        if ( set )
        {
            for ( int i = 0; i < sflags.length; i++ )
                if ( !nflags.contains( sflags[i] ) )
                {
                    nflags.add( sflags[i] );
                    changed = true;
                }
        }
        else
        {
            for ( int i = 0; i < sflags.length; i++ )
                if ( nflags.contains( sflags[i] ) )
                {
                    nflags.remove( sflags[i] );
                    changed = true;
                }
        }
        if ( changed )
            writeBackFlags( nflags );
    }

    protected void setMessageNumber( int n )
    {
        super.setMessageNumber( n );
    }

    protected static String[] splitInfo( String name )
    {
        String[] pair = new String[2];

        int infoPos = name.indexOf( ':' );
        if ( infoPos > 0 )
        {
            pair[0] = name.substring( 0, infoPos );
            pair[1] = name.substring( infoPos );
        }
        else
        {
            pair[0] = name;
            pair[1] = null;
        }
        return pair;
    }

    protected void writeBackFlags( Flags nflags )
        throws IllegalWriteException
    {
        debug( "in writeBack" );

        String[] pair = splitInfo( file.getName() );
        File nfile = new File( file.getParent()
                               + File.separatorChar
                               + pair[0]
                               + getInfoFromFlags( nflags ) );

        try
        {
            file.renameTo( nfile );
        }
        catch ( Exception e )
        {
            throw new IllegalWriteException( e.toString() );
        }

        file = nfile;
        flags = nflags;
        int type = MessageChangedEvent.FLAGS_CHANGED;
        ( (MaildirFolder) folder ).notifyMessageChangedListeners( type, this );
    }
}
