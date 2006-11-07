package org.codehaus.plexus.logging.console;

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

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.awt.*;

import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: ConsoleLoggerTest.java 4466 2006-10-15 20:56:54Z handyande $
 */
public class ColorConsoleLoggerTest
    extends TestCase
{
    ByteArrayOutputStream os;
    PrintStream consoleStream;

    private void resetStream()
    {
        os = new ByteArrayOutputStream();

        consoleStream = new PrintStream( os );

        System.setOut( consoleStream );
    }

    public void testConsoleLogger() {
        AbstractColorConsoleLogger logger = new ANSIColorConsoleLogger( AbstractColorConsoleLogger.LEVEL_DEBUG, "test" );

        assertTrue( logger.isDebugEnabled() );

        assertTrue( logger.isInfoEnabled() );

        assertTrue( logger.isWarnEnabled() );

        assertTrue( logger.isErrorEnabled() );

        assertTrue( logger.isFatalErrorEnabled() );

        // Save the original print stream.
        PrintStream original = System.out;

        Throwable t = new Throwable( "throwable" );

        resetStream();

        logger.debug( "debug" );

        assertEquals( "[DEBUG] debug", getMessage( consoleStream, os ) );

        logger.debug( "debug", t );

        assertEquals( "[DEBUG] debug", getMessage( consoleStream, os ) );

        resetStream();
        logger.info( "info" );

        assertEquals( logger.color( AbstractColorConsoleLogger.COLOR_INFO, "[INFO] " )
            + "info", getMessage( consoleStream, os ) );

        logger.setWholeLineColored( false );
        logger.info( "info", t );

        assertEquals( logger.color( AbstractColorConsoleLogger.COLOR_INFO, "[INFO] " )
            + "info", getMessage( consoleStream, os ) );

        resetStream();
        logger.setWholeLineColored( true );
        logger.info( "info" );

        assertEquals( logger.color( AbstractColorConsoleLogger.COLOR_INFO, "[INFO] " )
            + logger.color( AbstractColorConsoleLogger.COLOR_INFO, "info\n" ),
            getMessage( consoleStream, os ) );

/*
        os = new ByteArrayOutputStream();

        consoleStream = new PrintStream( os );

        System.setOut( consoleStream );

        logger.warn( "warn" );

        assertEquals( "[WARNING] warn", getMessage( consoleStream, os ) );

        logger.warn( "warn", t );

        assertEquals( "[WARNING] warn", getMessage( consoleStream, os ) );


        os = new ByteArrayOutputStream();

        consoleStream = new PrintStream( os );

        System.setOut( consoleStream );

        logger.error( "error" );

        assertEquals( "[ERROR] error", getMessage( consoleStream, os ) );

        logger.error( "error", t );

        assertEquals( "[ERROR] error", getMessage( consoleStream, os ) );


        os = new ByteArrayOutputStream();

        consoleStream = new PrintStream( os );

        System.setOut( consoleStream );

        logger.fatalError( "error" );

        assertEquals( "[FATAL ERROR] error", getMessage( consoleStream, os ) );

        logger.fatalError( "error", t );

        assertEquals( "[FATAL ERROR] error", getMessage( consoleStream, os ) );
*/
        // Set the original print stream.
        System.setOut( original );

        ANSIColoredString str = new ANSIColoredString(Color.RED, "this ");
        str.appendColored(Color.BLUE, "is a");
        str.append(" test");
        logger.warn(str.toString());
    }

    private String getMessage( PrintStream consoleStream, ByteArrayOutputStream os )
    {
        consoleStream.flush();

        consoleStream.close();

        return StringUtils.chopNewline( os.toString() );
    }
}
