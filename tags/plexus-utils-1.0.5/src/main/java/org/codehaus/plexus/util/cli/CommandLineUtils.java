package org.codehaus.plexus.util.cli;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l </a>
 * @version $Id$
 */
public abstract class CommandLineUtils
{
    public static class StringStreamConsumer
        implements StreamConsumer
    {
        private StringBuffer string = new StringBuffer();

        private String ls = System.getProperty( "line.separator" );

        public void consumeLine( String line )
        {
            string.append( line + ls );
        }

        public String getOutput()
        {
            return string.toString();
        }
    }

    public static int executeCommandLine( Commandline cl, StreamConsumer systemOut, StreamConsumer systemErr )
        throws CommandLineException
    {
        return executeCommandLine( cl, null, systemOut, systemErr );
    }

    public static int executeCommandLine( Commandline cl, InputStream systemIn, StreamConsumer systemOut,
                                         StreamConsumer systemErr )
        throws CommandLineException
    {
        if ( cl == null )
        {
            throw new IllegalArgumentException( "cl cannot be null." );
        }

        Process p;

        p = cl.execute();

        StreamFeeder inputFeeder = null;

        if ( systemIn != null )
        {
            inputFeeder = new StreamFeeder( systemIn, p.getOutputStream() );
        }

        StreamPumper outputPumper = new StreamPumper( p.getInputStream(), systemOut );

        StreamPumper errorPumper = new StreamPumper( p.getErrorStream(), systemErr );

        if ( inputFeeder != null )
        {
            inputFeeder.start();
        }

        outputPumper.start();

        errorPumper.start();

        try
        {
            int returnValue = p.waitFor();

            if ( inputFeeder != null )
            {
                synchronized ( inputFeeder )
                {
                    if ( !inputFeeder.isDone() )
                    {
                        inputFeeder.wait();
                    }
                }
            }

            if ( outputPumper != null )
            {
                synchronized ( outputPumper )
                {
                    if ( !outputPumper.isDone() )
                    {
                        outputPumper.wait();
                    }
                }
            }

            if ( errorPumper != null )
            {
                synchronized ( errorPumper )
                {
                    if ( !errorPumper.isDone() )
                    {
                        errorPumper.wait();
                    }
                }
            }

            return returnValue;
        }
        catch ( InterruptedException ex )
        {
            throw new CommandLineException( "Error while executing external command.", ex );
        }
        finally
        {
            if ( inputFeeder != null )
            {
                inputFeeder.close();
            }

            outputPumper.close();

            errorPumper.close();
        }
    }

    public static Properties getSystemEnvVars() 
        throws IOException
    {
        Process p = null;

        Properties envVars = new Properties();

        Runtime r = Runtime.getRuntime();

        String os = System.getProperty( "os.name" ).toLowerCase();

        //If this is windows set the shell to command.com or cmd.exe with correct arguments.
        if ( os.indexOf( "windows" ) != -1 )
        {
            if ( os.indexOf( "95" ) != -1 || os.indexOf( "98" ) != -1 || os.indexOf( "Me" ) != -1 )
            {
                p = r.exec( "command.com /c set" );
            }
            else
            {
                p = r.exec( "cmd.exe /c set" );
            }
        }
        else
        {
            p = r.exec( "env" );
        }

        BufferedReader br = new BufferedReader( new InputStreamReader( p.getInputStream() ) );

        String line;

        while ( ( line = br.readLine() ) != null )
        {
            int idx = line.indexOf( '=' );

            String key = line.substring( 0, idx );

            String value = line.substring( idx + 1 );

            envVars.setProperty( key, value );
            // System.out.println( key + " = " + value );
        }

        return envVars;
    }
}
