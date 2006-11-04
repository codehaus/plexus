package org.codehaus.bayesian;

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 This class is responsible for converting corpus data the users supplies
 into a WordList. The user will supply a file that they want added to the
 corpus (be it the ham corpus or the spam corpus). An instance of this class
 will extract the individual words from the file and update the number of
 occurrences in the table for the corpus.
 */
public class Extractor
{
    private File file;
    private File wordListFile;
    private FileReader fileInput;
    private BufferedReader fileReader;
    private WordList wordList;

    /**
     Constructor takes the WordList we want to update and the file which
     we want to update it with.
     */
    public Extractor( WordList w, File inputSource ) throws Exception
    {
        wordList = w;
        // the file has to exist because we chose it with JFileChooser!
        fileInput = new FileReader( inputSource );
        fileReader = new BufferedReader( fileInput );
    }

    /**
     This metod is for extracting information from Outlook extracted information.

     @deprecated Due to the odd way outlook exports its files this does not yield
     good results. If you can try to create a text file of your previous e-mail and
     use the extractText method. Thanks M$!
     */
    public void extractOutlook() throws Exception
    {
        // this will hold the text from the file (want to make it into a long string)
        StringBuffer buffer = new StringBuffer();

        // a counter to count how many e-mails are in the text. This is done by counting the number
        // of lines that start with "
        double counter = 0.0;

        // skip the first line (full of junk)
        String line = fileReader.readLine();
        line = fileReader.readLine();

        // fill up the buffer.
        // read in a line of text and just append it to the string buffer.
        while ( line != null )
        {
            if ( line.startsWith( "\"" ) )
            {
                counter++;
            }
            else if ( line.equals( "\r" ) )
            {
                // Nothing!
            }
            else
            {
                buffer.append( line + " " );
            }
            line = fileReader.readLine();
        }

        // convert buffer into a string
        String data = buffer.toString();

        // a new buffer to hold the parsed data
        StringBuffer parsedBuffer = new StringBuffer();

        // these mark the head and tail position of the substring I want placed into the parsed buffer
        // the string is from the end of the last match to the start of the new match.
        // Had to do it this way (replaceAll was not working!)
        int head = 0;
        int tail = 0;

        // the regular expressions to strip out all the non essential tokens and the other to find
        // all URLs in the text.
        String regexp = "(\".*\")|(BEGIN[.*]*END)|(>)|(<)|(_)|(-)|(\\*)|(#)|(<.*>)([,\\.\\=\t\\*\\+\\\\])|(\\&nbsp\\;)|(\\<.*)|(href)";
        String htmlReg = "(http:[^\\s]*\\s)|(www.[^\\s]+\\s)";


        try
        {
            Pattern p = Pattern.compile( htmlReg, Pattern.CASE_INSENSITIVE );
            Matcher m = p.matcher( data );
            while ( m.find() )
            {
                int start = m.start();
                int end = m.end();

                tail = start;
                String nonLink = data.substring( head, tail );
                nonLink = nonLink.replaceAll( regexp, "" );
                parsedBuffer.append( nonLink + " " );
                head = end;

                String match = data.substring( start, end );

                String fineTune = "[\\>\"\\<,]";
                match = match.replaceAll( fineTune, "" );

                parsedBuffer.append( getUrl( match ) + " " );
            }
        }
        catch ( Exception e )
        {
        }
        String parsedText = parsedBuffer.toString();

        // the way we count the number of emails is a bit dodgy, best if we actually divide by two
        // as this will give us a better estimate.
        wordList.update( parsedText, counter / 2.0 );
    }

    /**
     Spamarchive.org allow people to download all of the submitted
     spam that they receive. This is a great source of spam and its
     a good Idea to try and tap it.
     Their files are somewhat bizzar. It only constains information of
     text/* messages. multipart/* message only the headers are displayed.

     I decided to remove all of these blockes of headers ( making sure to take
     some info from these). Next I rean in whats left and update the corpus with it.
     */
    public void extractSpamArchive() throws Exception
    {
        StringBuffer buff = new StringBuffer();
        double counter = 0.0;

        String line = fileReader.readLine();
        while ( line != null )
        {
            if ( line.startsWith( "From:" ) )
            {
                boolean foundEnd = false;
                while ( !foundEnd )
                {
                    line = fileReader.readLine();
                    if ( line.startsWith( "to:" ) )
                    {
                        counter++;
                        foundEnd = true;
                    }
                    else if ( line.startsWith( "Subject:" ) )
                    {
                        // take First token
                        // append each token to the first one and add to the buffer.

                        // break it up into words
                        StringTokenizer st = new StringTokenizer( line );
                        // get rid of first token From:
                        st.nextToken();

                        while ( st.hasMoreTokens() )
                        {
                            String a = st.nextToken();
                            String token = "Subject:" + a;
                            buff.append( token + " " );
                        }
                    }
                }
            }
            else
            {
                buff.append( line + " " );
            }
            line = fileReader.readLine();
        }

        String buffString = buff.toString();
        StringBuffer newBuff = new StringBuffer();
        int head = 0;
        int tail = 0;

        String regexp = "([,\\-\\.\\=\t\\*\\+\\\\])|(<[^>]*>)|(\\&nbsp\\;)|(\\<.*)|(href)";
        String htmlReg = "(http:[^\\s]*\\s)|(www.[^\\s]+\\s)";
        try
        {
            Pattern p = Pattern.compile( htmlReg, Pattern.CASE_INSENSITIVE );
            Matcher m = p.matcher( buffString );
            while ( m.find() )
            {
                int start = m.start();
                int end = m.end();

                tail = start;
                String nonLink = buffString.substring( head, tail );
                nonLink = nonLink.replaceAll( regexp, "" );
                newBuff.append( nonLink + " " );
                head = end;

                String match = buffString.substring( start, end );
                String fineTune = "[\\>\"\\<,]";
                match = match.replaceAll( fineTune, "" );

                newBuff.append( getUrl( match ) + " " );
            }
        }
        catch ( Exception e )
        {
        }

        String parsedText = newBuff.toString();
        wordList.update( parsedText, counter );
    }

    /**
     This is by far the most simple case. Simply read in a line of
     text then update the corpus with this text. Repeat until there
     is no more text to be read in.
     */
    public void extractText() throws Exception
    {
        String line = fileReader.readLine();
        while ( line != null )
        {
            wordList.update( line, 1.0 );
            line = fileReader.readLine();
        }
    }

    /**
     The mailspool files I studied seen to have a delimiting string
     which started with "From " (note no ":"). I used this to break each
     message up and packed them into MailMessage objects. These could then
     be run through the parser.
     Finally I updated the corpus with the parsed text.
     */
    public void extractMailspool() throws Exception
    {
        String line = fileReader.readLine();

        while ( line != null )
        {
            if ( line.startsWith( "From " ) )
            {
                ArrayList header = new ArrayList();
                ArrayList body = new ArrayList();

                while ( !line.equals( "" ) )
                {
                    header.add( line );
                    line = fileReader.readLine();
                }

                while ( !line.startsWith( "From " ) )
                {
                    body.add( line );
                    line = fileReader.readLine();
                    if ( line == null )
                    {
                        parseUpdate( header, body );
                        break;
                    }
                }
                parseUpdate( header, body );
            }
            line = fileReader.readLine();
        }
    }


    /**
     This method is called in order to extract infromation from an Inbox
     file from Eudora.
     I notiecd that there was a delimiting string of
     From: ???@??? in the eudora files so I used this to seperate out
     each message. I next construct MailMessage objects and pack ecah
     message into an object.
     Then I run this through the parser to extract all the relevent information.
     Finally I updated the corpus with the parsed text
     */
    public void extractEudora() throws Exception
    {
        String line = fileReader.readLine();

        while ( line != null )
        {
            if ( line.startsWith( "From ???@???" ) )
            {
                ArrayList header = new ArrayList();
                ArrayList body = new ArrayList();

                while ( !line.equals( "" ) )
                {
                    header.add( line );
                    line = fileReader.readLine();
                }

                while ( !line.startsWith( "From " ) )
                {
                    body.add( line );
                    line = fileReader.readLine();
                    if ( line == null )
                    {
                        parseUpdate( header, body );
                        break;
                    }
                }
                parseUpdate( header, body );
            }
            line = fileReader.readLine();
        }
    }

    /*
        For use with extractEudora and extractMailspool method
    */
    private void parseUpdate( ArrayList header, ArrayList body )
    {
        Parser parser = new Parser();
        Message m = new Message();

        // get headers
        m.setHeader( header );
        // get body
        m.setBody( body );
        // get Date
        m.setDate( getMessageDate( header ) );
        // get From
        m.setFrom( getMessageFrom( header ) );
        // get Subject
        m.setSubject( getMessageSubject( header ) );
        // get content type
        m.setContentType( getMessageContentType( header ) );
        // get CTE
        m.setContentTransferEncoding( getMessageContentTransferEncoding( header ) );
        // get Boundary
        m.setBoundary( getMessageBoundary( header ) );

        String parsed = parser.parse( m );

        wordList.update( parsed, 1.0 );
    }

    /*
        This method will extract just the hostname from a URL
    */
    private String getUrl( String s )
    {
        // find index of "//"
        // find index of "/" or end of word
        int i = -1;
        int j = -1;

        i = s.indexOf( "//" );
        j = s.indexOf( "/", ( i + 2 ) );

        if ( i != -1 )
        {
            if ( j != -1 )
            {
                String temp = s.substring( ( i + 2 ), j );
                temp = "URL:" + temp;
                return temp;
            }
            else
            {
                String temp = s.substring( ( i + 2 ) );
                temp = "URL:" + temp;
                return temp;

            }
        }
        return "URL:" + s;
    }


    /**
     Gets the From field out of the e-mail header
     */
    private String getMessageFrom( ArrayList headers )
    {
        for ( int i = 0; i < headers.size(); i++ )
        {
            String tester = (String) headers.get( i );
            if ( tester.startsWith( "From: " ) )
            {
                return tester;
            }
        }
        return null;
    }

    /**
     Gets the MIME Content-Type from the e-mail header
     */
    private String getMessageContentType( ArrayList headers )
    {
        for ( int i = 0; i < headers.size(); i++ )
        {
            String tester = (String) headers.get( i );
            if ( tester.startsWith( "Content-Type: " ) )
            {
                int start = tester.indexOf( ":" );
                int finish = tester.indexOf( ";" );
                if ( finish == -1 )
                {
                    String tester2 = tester.substring( ( start + 2 ) );
                    //System.out.println("\t" + tester2);
                    return tester2;

                }
                else
                {
                    String tester2 = tester.substring( ( start + 2 ), finish );
                    //System.out.println("\t" + tester2);
                    return tester2;
                }
            }
        }

        // if none present its defaulted to text/plain
        return "text/plain";
    }

    /**
     Gets the Content-Transfer-Encoding field from the e-mail header.
     */
    private String getMessageContentTransferEncoding( ArrayList headers )
    {
        for ( int i = 0; i < headers.size(); i++ )
        {
            String tester = (String) headers.get( i );
            if ( tester.startsWith( "Content-Transfer-Encoding:" ) )
            {
                int start = tester.indexOf( ":" );
                tester = tester.substring( ( start + 2 ) );
                return tester;
            }
        }
        return null;
    }

    /*
        This method will take the Subject line from the header and remove
        the "Subject: " from the start of it.

        @param headers the arraylist of header fields.
        @return String the subject or null if there is no subject.
    */
    private String getMessageSubject( ArrayList headers )
    {
        for ( int i = 0; i < headers.size(); i++ )
        {
            String tester = (String) headers.get( i );
            if ( tester.startsWith( "Subject: " ) )
            {
                String theSubject = tester.substring( 9 );
                return theSubject;
            }
        }
        return null;
    }

    /*
        This method will take the Date header field from the headers and remove the
        "Date: " from the start of it.

        @param headers the ArrayList of header fields.
        @return String the Date (in text format) or null if there was no date.
    */
    private String getMessageDate( ArrayList headers )
    {
        for ( int i = 0; i < headers.size(); i++ )
        {
            String tester = (String) headers.get( i );
            if ( tester.startsWith( "Date: " ) )
            {
                return tester;
            }
        }
        return null;
    }

    /*
        Gets the MIME Boundary from the e-mail header.
    */
    private String getMessageBoundary( ArrayList headers )
    {
        for ( int i = 0; i < headers.size(); i++ )
        {
            String tester = (String) headers.get( i );
            int a = tester.indexOf( "boundary=\"" );

            if ( a != -1 )
            {
                int b = tester.indexOf( "\"", ( a + 12 ) );

                String boundary = "--" + tester.substring( ( a + 10 ), b );
                return boundary;
            }
        }
        return null;
    }

}