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

import org.codehaus.plexus.logging.AbstractLoggerManagerTest;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.logging.Logger;

/**
 * Test for {@link org.codehaus.plexus.logging.console.ColorConsoleLoggerManager} and
 * {@link org.codehaus.plexus.logging.console.AbstractColorConsoleLogger}.
 *
 * @author Mark H. Wilkinson
 * @version $Revision: 4466 $
 */
public final class ColorConsoleLoggerManagerTest
    extends AbstractLoggerManagerTest
{
    protected LoggerManager createLoggerManager() throws Exception
    {
        return (LoggerManager)lookup(LoggerManager.ROLE);
    }

    public void testSetAllThresholds() throws Exception
    {
        LoggerManager manager = createLoggerManager();
        manager.setThreshold( Logger.LEVEL_ERROR );

        Logger logger = manager.getLoggerForComponent( "test" );
        assertEquals( logger.getThreshold(), Logger.LEVEL_ERROR );

        manager.setThresholds( Logger.LEVEL_DEBUG );
        assertEquals( logger.getThreshold(), Logger.LEVEL_DEBUG );
    }
}
