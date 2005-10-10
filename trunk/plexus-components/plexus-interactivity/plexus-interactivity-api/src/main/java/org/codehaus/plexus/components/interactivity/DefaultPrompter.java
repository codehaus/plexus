package org.codehaus.plexus.components.interactivity;

/*
 * The MIT License
 *
 * Copyright (c) 2005, The Codehaus
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

import org.codehaus.plexus.util.StringUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Default prompter.
 *
 * @author Brett Porter
 * @version $Id$
 */
public class DefaultPrompter
    implements Prompter
{
    /**
     * @requirement
     */
    private OutputHandler outputHandler;

    /**
     * @requirement
     */
    private InputHandler inputHandler;

    public String prompt( String message )
        throws PrompterException
    {
        try
        {
            writePrompt( message );
        }
        catch ( IOException e )
        {
            throw new PrompterException( "Failed to present prompt", e );
        }

        try
        {
            return inputHandler.readLine();
        }
        catch ( IOException e )
        {
            throw new PrompterException( "Failed to read user response", e );
        }
    }

    public String prompt( String message, String defaultReply )
        throws PrompterException
    {
        try
        {
            writePrompt( formatMessage( message, null, defaultReply ) );
        }
        catch ( IOException e )
        {
            throw new PrompterException( "Failed to present prompt", e );
        }

        try
        {
            String line = inputHandler.readLine();

            if ( StringUtils.isEmpty( line ) )
            {
                line = defaultReply;
            }

            return line;
        }
        catch ( IOException e )
        {
            throw new PrompterException( "Failed to read user response", e );
        }
    }

    public String prompt( String message, List possibleValues, String defaultReply )
        throws PrompterException
    {
        String formattedMessage = formatMessage( message, possibleValues, defaultReply );

        String line;

        do
        {
            try
            {
                writePrompt( formattedMessage );
            }
            catch ( IOException e )
            {
                throw new PrompterException( "Failed to present prompt", e );
            }

            try
            {
                line = inputHandler.readLine();
            }
            catch ( IOException e )
            {
                throw new PrompterException( "Failed to read user response", e );
            }

            if ( StringUtils.isEmpty( line ) )
            {
                line = defaultReply;
            }

            if ( line != null && !possibleValues.contains( line ) )
            {
                try
                {
                    outputHandler.writeLine( "Invalid selection." );
                }
                catch ( IOException e )
                {
                    throw new PrompterException( "Failed to present feedback", e );
                }
            }
        }
        while ( line == null || !possibleValues.contains( line ) );

        return line;
    }

    public String prompt( String message, List possibleValues )
        throws PrompterException
    {
        return prompt( message, possibleValues, null );
    }

    public String promptForPassword( String message )
        throws PrompterException
    {
        try
        {
            writePrompt( message );
        }
        catch ( IOException e )
        {
            throw new PrompterException( "Failed to present prompt", e );
        }

        try
        {
            return inputHandler.readPassword();
        }
        catch ( IOException e )
        {
            throw new PrompterException( "Failed to read user response", e );
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
            formatted.append( ' ' ).append( defaultReply ).append( ": " );
        }

        return formatted.toString();
    }

    private void writePrompt( String message )
        throws IOException
    {
        outputHandler.write( message + ": " );
    }

    public void showMessage( String message )
        throws PrompterException
    {
        try
        {
            writePrompt( message );
        }
        catch ( IOException e )
        {
            throw new PrompterException( "Failed to present prompt", e );
        }

    }
}
