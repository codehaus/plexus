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
import javax.mail.FolderNotFoundException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.FolderEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class MaildirFolder extends Folder
{
    protected String maildir;
    protected String folderdir;

    protected boolean open;
    protected Vector messages;

    private long lst_mcheck;

    private static long mcheck_delay = 30000;

    protected final static char separator = '.';

    private static Pattern folderPattern;

    static
    {
        try
        {
            //folderPattern = Pattern.compile("^[-_a-zA-Z0-9]+(\\.[-_a-zA-Z0-9]+)*$");
            folderPattern = Pattern.compile( "^[^/.]+(\\.[^/.]+)*$" );
        }
        catch ( PatternSyntaxException e )
        {
            folderPattern = null;
            System.err.println( "Very serious!" );
        }
    }

    private static MailFilenameFilter mailFilter = new MailFilenameFilter();

    public MaildirFolder( MaildirStore store, String folderdir )
    {
        super( store );

        this.folderdir = folderdir;
        maildir = store.getMaildirPath();
        open = false;
    }

    public void appendMessages( Message[] msgs )
        throws MessagingException
    {
        if ( !exists() )
            throw new FolderNotFoundException();

        try
        {
            for ( int i = 0; i < msgs.length; i++ )
            {
                if ( msgs[i].isExpunged() )
                {
                    ( (MaildirStore) store ).debug( "appendMessages: ignored expunged message." );
                    continue;
                }

                Message m = msgs[i];
                String mailname = MaildirMessage.createUniqueName( m );
                File tmpfile = buildMailFile( "tmp", mailname );
                File newfile = buildMailFile( "new", mailname );
                OutputStream os = new FileOutputStream( tmpfile );
                m.writeTo( os );
                os.close();
                tmpfile.renameTo( newfile );
            }
        }
        catch ( IOException e )
        {
            throw new MessagingException( e.toString() );
        }
        if ( open )
        {
            lst_mcheck -= mcheck_delay;
            collectNewMail();
        }
    }

    protected Pattern buildFolderRE( String pattern )
        throws PatternSyntaxException
    {
        final String specials = ".*[]^\\";
        StringBuffer re = new StringBuffer( "^" );

        String fp = "." + getSubfolderPrefix() + pattern;

        int len = fp.length();
        for ( int i = 0; i < len; i++ )
        {
            char c = fp.charAt( i );
            if ( c == '%' )
            {
                re.append( "[^" );
                re.append( separator );
                re.append( "]*" );
            }
            else if ( c == '*' )
            {
                re.append( ".*" );
            }
            else if ( specials.indexOf( c ) != -1 )
            {
                re.append( '\\' );
                re.append( c );
            }
            else
            {
                re.append( c );
            }
        }
        re.append( '$' );
        String ret = re.toString();
        debug( ret );
        return Pattern.compile( ret );
    }

    protected File buildMailFile( String dir, String name )
    {
        return new File( maildir
                         + File.separatorChar
                         + folderdir
                         + File.separatorChar
                         + dir
                         + File.separatorChar
                         + name );
    }

    public void close( boolean expunge )
        throws MessagingException
    {
        if ( !open )
            throw new IllegalStateException( "Folder was already closed." );

        if ( expunge )
            expunge();

        open = false;
        messages = null;

        notifyConnectionListeners( ConnectionEvent.CLOSED );
    }

    protected void collectNewMail( String[] mail, boolean notify )
        throws MessagingException
    {
        if ( mail == null )
            return;

        int base = messages.size();
        for ( int i = 0; i < mail.length; i++ )
        {
            File src = buildMailFile( "new", mail[i] );
            File dst = buildMailFile( "cur", mail[i] );

            src.renameTo( dst );
            MaildirMessage f = new MaildirMessage( this, base + i,
                                                   dst, true );
            messages.addElement( f );
        }

        int noNewMail = messages.size() - base;
        if ( notify && noNewMail > 0 )
        {
            Message[] newMsgs = new Message[noNewMail];
            List newMsgList = messages.subList( base, messages.size() );
            newMsgList.toArray( (Object[]) newMsgs );
            notifyMessageAddedListeners( newMsgs );
        }

        lst_mcheck = System.currentTimeMillis();
    }

    protected synchronized void collectNewMail()
        throws MessagingException
    {
        collectNewMail( getNewMailFiles(), true );
    }

    public boolean create( int type )
        throws MessagingException
    {
        if ( exists() )
            return false;

        MaildirFolder parent = (MaildirFolder) getParent();

        if ( parent != null && !parent.exists() )
            if ( !parent.create( type ) )
                return false;

        String me = maildir + File.separatorChar + folderdir;

        File meDir = new File( me );
        File mfFile = new File( me, "maildirfolder" );
        File newDir = new File( me, "new" );
        File curDir = new File( me, "cur" );
        File tmpDir = new File( me, "tmp" );

        try
        {
            meDir.mkdir();
            mfFile.createNewFile();
            newDir.mkdir();
            curDir.mkdir();
            tmpDir.mkdir();
        }
        catch ( IOException e )
        {
        }

        if ( exists() )
        {
            notifyFolderListeners( FolderEvent.CREATED );
            return true;
        }
        else
        {
            recursiveFileDelete( meDir );
            return false;
        }
    }

    protected void debug( String message )
    {
        ( (MaildirStore) store ).debug( message );
    }

    public boolean delete( boolean recurse )
        throws MessagingException
    {
        if ( !exists() )
            throw new FolderNotFoundException();

        if ( open )
            throw new IllegalStateException( "Folder must be closed." );

        Folder[] subfolders = list();

        if ( !recurse && subfolders.length != 0 )
            return false;

        boolean allSubDeleted = true;

        for ( int i = 0; i < subfolders.length; i++ )
            if ( !subfolders[i].delete( true ) )
                allSubDeleted = false;

        if ( !allSubDeleted )
            return false;

        if ( recursiveFileDelete( new File( maildir, folderdir ) ) )
        {
            notifyFolderListeners( FolderEvent.DELETED );
            return true;
        }
        else
            return false;
    }

    public boolean exists()
    {
        String me = maildir + File.separatorChar + folderdir;

        File newDir = new File( me, "new" );
        File curDir = new File( me, "cur" );
        File tmpDir = new File( me, "tmp" );

        return ( new File( me ) ).isDirectory()
            && newDir.isDirectory()
            && curDir.isDirectory()
            && tmpDir.isDirectory();
    }

    public Message[] expunge()
        throws MessagingException
    {
        if ( !open )
            throw new IllegalStateException();

        collectNewMail();

        if ( mode == READ_ONLY )
            return new Message[0];

        Vector expungees = new Vector();

        int i = 0;
        int len = messages.size();
        while ( i < len )
        {
            MaildirMessage m = (MaildirMessage) messages.elementAt( i );

            try
            {
                if ( m.getFlags().contains( Flags.Flag.DELETED ) )
                {
                    messages.removeElementAt( i );
                    expungees.addElement( m );
                    m.setExpunged( true );
                    len--;
                }
                else
                {
                    m.setMessageNumber( i + 1 );
                    i++;
                }
            }
            catch ( MessagingException e )
            {
                m.setMessageNumber( i + 1 );
                i++;
            }
        }

        int no_expungees = expungees.size();

        if ( no_expungees > 0 )
        {
            Message[] ea = new Message[no_expungees];
            ea = (Message[]) expungees.toArray( (Object[]) ea );
            notifyMessageRemovedListeners( true, ea );
            return ea;
        }
        else
            return new Message[0];
    }

    public Folder getFolder( String folderName )
        throws MessagingException
    {
        if ( folderName.charAt( 0 ) == separator )
            return store.getFolder( folderName.substring( 1 ) );
        else
            return store.getFolder( getSubfolderPrefix() + folderName );
    }

    public String getFullName()
    {
        return folderdir.substring( 1 );
    }

    public Message getMessage( int n )
        throws MessagingException
    {
        if ( !open )
            throw new IllegalStateException();

        collectNewMail();

        return (Message) messages.elementAt( n - 1 );
    }

    public Message[] getMessages()
        throws MessagingException
    {
        if ( !open )
            throw new IllegalStateException();

        collectNewMail();

        Message[] ret = new Message[messages.size()];
        return (Message[]) messages.toArray( (Object[]) ret );
    }

    public Message[] getMessages( int start, int end )
        throws MessagingException
    {
        if ( !open )
            throw new IllegalStateException();

        collectNewMail();

        Message[] ret = new Message[end - start + 1];
        return (Message[]) messages.subList( start - 1, end ).toArray( (Object[]) ret );
    }

    public int getMessageCount()
        throws MessagingException
    {
        if ( open )
        {
            collectNewMail();
            return messages.size();
        }
        else
            return listMailFiles( "cur" ).length + listMailFiles( "new" ).length;
    }

    public String getName()
    {
        int lastSep = getFullName().lastIndexOf( separator );
        return getFullName().substring( lastSep + 1 );
    }

    protected String[] getNewMailFiles()
    {
        if ( System.currentTimeMillis() - lst_mcheck < mcheck_delay )
            return null;
        else
            return listMailFiles( "new" );
    }

    public int getNewMessageCount()
        throws MessagingException
    {
        if ( open )
        {
            collectNewMail();
            return super.getNewMessageCount();
        }
        else
            return listMailFiles( "new" ).length;
    }

    public Folder getParent()
        throws MessagingException
    {
        int lastSep = getFullName().lastIndexOf( separator );

        if ( lastSep == -1 )
            return null;
        else
            return store.getFolder( getFullName().substring( 0, lastSep ) );
    }

    public Flags getPermanentFlags()
    {
        Flags f = new Flags();
        f.add( Flags.Flag.ANSWERED );
        f.add( Flags.Flag.DELETED );
        f.add( Flags.Flag.DRAFT );
        f.add( Flags.Flag.FLAGGED );
        f.add( Flags.Flag.RECENT );
        f.add( Flags.Flag.SEEN );
        return f;
    }

    public char getSeparator()
    {
        return separator;
    }

    protected String getSubfolderPrefix()
    {
        return getFullName() + separator;
    }

    public int getType()
    {
        return HOLDS_MESSAGES + HOLDS_FOLDERS;
    }

    public boolean hasNewMessages()
        throws MessagingException
    {
        return getNewMessageCount() > 0;
    }

    public static boolean isFolderNameOk( String name )
    {
        Matcher m = folderPattern.matcher( name );
        return m.matches() && !"inbox".equalsIgnoreCase( name );
    }

    public boolean isOpen()
    {
        return open;
    }

    protected Folder[] list( Pattern pattern )
        throws MessagingException
    {
        File mf = new File( maildir );

        File[] list = mf.listFiles( new MaildirFolderFilter( pattern ) );

        if ( list == null )
            return new Folder[0];

        Folder[] subfolders = new Folder[list.length];

        for ( int i = 0; i < list.length; i++ )
        {
            subfolders[i] = store.getFolder( list[i].getName().substring( 1 ) );
        }
        return subfolders;
    }

    public Folder[] list( String pattern )
        throws MessagingException
    {
        try
        {
            return list( buildFolderRE( pattern ) );
        }
        catch ( PatternSyntaxException e )
        {
            ( (MaildirStore) store ).debug( "list: illegal pattern.\n" + pattern );
            return new Folder[0];
        }
    }

    protected String[] listMailFiles( String dir )
    {
        File path = new File( maildir
                              + File.separatorChar
                              + folderdir
                              + File.separatorChar
                              + dir );

        String[] ret = path.list( mailFilter );
        if ( ret == null )
        {
            debug( "Unable to list: " + path.getAbsolutePath() );
            return new String[0];
        }
        else
            return ret;
    }

    protected void notifyMessageChangedListeners( int type, Message msg )
    {
        super.notifyMessageChangedListeners( type, msg );
    }

    public void open( int mode )
        throws MessagingException
    {
        if ( open )
            throw new IllegalStateException( "Folder was already open." );

        if ( !exists() )
            throw new FolderNotFoundException();

        this.mode = mode;
        open = true;

        String[] mail_cur = listMailFiles( "cur" );
        String[] mail_new = listMailFiles( "new" );

        int no_mails = mail_cur.length + mail_new.length;

        messages = new Vector( no_mails + 100 );

        for ( int i = 0; i < mail_cur.length; i++ )
        {
            File f = buildMailFile( "cur", mail_cur[i] );
            messages.addElement( new MaildirMessage( this, i + 1, f, false ) );
        }

        collectNewMail( mail_new, false );

        notifyConnectionListeners( ConnectionEvent.OPENED );
    }

    protected static boolean recursiveFileDelete( File root )
    {
        File[] children = root.listFiles();
        if ( children != null )
        {
            for ( int i = 0; i < children.length; i++ )
            {
                if ( children[i].isDirectory() )
                    recursiveFileDelete( children[i] );
                else
                    children[i].delete();
            }
        }
        return root.delete();
    }

    public boolean renameTo( Folder f )
        throws MessagingException
    {
        if ( open )
            throw new IllegalStateException( "Folder must be closed." );

        if ( !exists() )
            throw new FolderNotFoundException();

        if ( f.exists()
            || !f.getParent().exists()
            || !isFolderNameOk( f.getFullName() )
            || f.getFullName().startsWith( getSubfolderPrefix() ) )
            return false;

        Folder[] subfolders = list();

        File src = new File( maildir, folderdir );
        File dst = new File( maildir, "." + f.getFullName() );

        if ( !src.renameTo( dst ) )
            return false;

        notifyFolderRenamedListeners( this );

        for ( int i = 0; i < subfolders.length; i++ )
        {
            Folder newsub = f.getFolder( subfolders[i].getName() );
            subfolders[i].renameTo( newsub );
        }

        return true;
    }
}
