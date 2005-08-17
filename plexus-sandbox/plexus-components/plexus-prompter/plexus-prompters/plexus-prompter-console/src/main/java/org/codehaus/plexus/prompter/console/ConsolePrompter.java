package org.codehaus.plexus.prompter.console;

import org.codehaus.plexus.prompter.PromptException;
import org.codehaus.plexus.prompter.Prompter;
import org.codehaus.plexus.util.StringUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import jline.ConsoleReader;
import jline.SimpleCompletor;

public class ConsolePrompter
    implements Prompter
{

    /**
     * @requirement
     */
    private ConsoleWriter consoleWriter;

    public String prompt( String message )
        throws PromptException
    {
        ConsoleReader consoleReader = createConsoleReader( null );

        writePrompt( message );

        try
        {
            return consoleReader.readLine();
        }
        catch ( IOException e )
        {
            throw new PromptException( "Failed to read user response", e );
        }
    }

    public String prompt( String message, String defaultReply )
        throws PromptException
    {
        ConsoleReader consoleReader = createConsoleReader( new String[] { defaultReply } );

        writePrompt( formatMessage( message, null, defaultReply ) );

        try
        {
            String line = consoleReader.readLine();

            if ( StringUtils.isEmpty( line ) )
            {
                line = defaultReply;
            }

            return line;
        }
        catch ( IOException e )
        {
            throw new PromptException( "Failed to read user response", e );
        }
    }

    public String prompt( String message, List possibleValues )
        throws PromptException
    {
        String[] completions = null;

        if ( !possibleValues.isEmpty() )
        {
            completions = (String[]) possibleValues.toArray( new String[possibleValues.size()] );
        }

        ConsoleReader consoleReader = createConsoleReader( completions );

        String formattedMessage = formatMessage( message, possibleValues, null );

        try
        {
            String line = null;

            do
            {
                writePrompt( formattedMessage );

                line = consoleReader.readLine();

                if ( StringUtils.isEmpty( line ) )
                {
                    line = null;
                }

                if ( line != null && !possibleValues.contains( line ) )
                {
                    consoleWriter.writeLine( "Invalid selection." );
                }
            }
            while ( line == null || !possibleValues.contains( line ) );

            return line;
        }
        catch ( IOException e )
        {
            throw new PromptException( "Failed to read user response", e );
        }
    }

    public String prompt( String message, List possibleValues, String defaultReply )
        throws PromptException
    {
        String[] completions = null;

        if ( !possibleValues.isEmpty() )
        {
            completions = (String[]) possibleValues.toArray( new String[possibleValues.size()] );
        }

        ConsoleReader consoleReader = createConsoleReader( completions );

        String formattedMessage = formatMessage( message, possibleValues, defaultReply );

        try
        {
            String line = null;

            do
            {
                writePrompt( formattedMessage );

                line = consoleReader.readLine();

                if ( StringUtils.isEmpty( line ) )
                {
                    line = defaultReply;
                }

                if ( !possibleValues.contains( line ) )
                {
                    consoleWriter.writeLine( "Invalid selection." );
                }
            }
            while ( !possibleValues.contains( line ) );

            return line;
        }
        catch ( IOException e )
        {
            throw new PromptException( "Failed to read user response", e );
        }
    }

    public char[] promptForPassword( String message )
        throws PromptException
    {
        ConsoleReader consoleReader = createConsoleReader( null );

        writePrompt( message );

        try
        {
            return consoleReader.readLine( new Character( '*' ) ).toCharArray();
        }
        catch ( IOException e )
        {
            throw new PromptException( "Failed to read user response", e );
        }
    }

    private String formatMessage( String message, List possibleValues, String defaultReply )
    {
        StringBuffer formatted = new StringBuffer( message.length() * 2 );

        formatted.append( message );

        if ( possibleValues != null && !possibleValues.isEmpty() )
        {
            formatted.append( " (" );

            for ( Iterator it = possibleValues.iterator(); it.hasNext(); )
            {
                String possibleValue = (String) it.next();

                formatted.append( possibleValue );

                if ( it.hasNext() )
                {
                    formatted.append( '/' );
                }
            }

            formatted.append( ')' );
        }

        if ( defaultReply != null )
        {
            formatted.append( ' ' ).append( defaultReply ).append( ' ' );
        }

        return formatted.toString();
    }

    private ConsoleReader createConsoleReader( String[] completionCandidates )
        throws PromptException
    {
        try
        {
            ConsoleReader consoleReader = new ConsoleReader();

            if ( completionCandidates != null && completionCandidates.length > 0 )
            {
                consoleReader.addCompletor( new SimpleCompletor( completionCandidates ) );
            }
        }
        catch ( IOException e )
        {
            throw new PromptException( "Failed to initialize console reader", e );
        }

        return null;
    }

    private void writePrompt( String message )
        throws PromptException
    {
        try
        {
            consoleWriter.write( message );
        }
        catch ( IOException e )
        {
            throw new PromptException( "Failed to write user prompt", e );
        }
    }

}
