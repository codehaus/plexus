package org.codehaus.plexus.lifecycle;

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

import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class DefaultLifecycleHandlerManager
    implements LifecycleHandlerManager
{
    private List lifecycleHandlers = null;

    private String defaultLifecycleHandlerId = "plexus";

    public void initialize()
        throws Exception
    {
        for ( Iterator iterator = lifecycleHandlers.iterator(); iterator.hasNext(); )
        {
            LifecycleHandler lifecycleHandler = (LifecycleHandler) iterator.next();

            lifecycleHandler.initialize();
        }
    }

    public LifecycleHandler getLifecycleHandler( String id )
        throws UndefinedLifecycleHandlerException
    {
        LifecycleHandler lifecycleHandler = null;

        for ( Iterator iterator = lifecycleHandlers.iterator(); iterator.hasNext(); )
        {
            lifecycleHandler = (LifecycleHandler) iterator.next();

            if ( id.equals( lifecycleHandler.getId() ) )
            {
                return lifecycleHandler;
            }
        }

        throw new UndefinedLifecycleHandlerException( "Specified lifecycle handler cannot be found: " + id );
    }

    public LifecycleHandler getDefaultLifecycleHandler()
        throws UndefinedLifecycleHandlerException
    {
        return getLifecycleHandler( defaultLifecycleHandlerId );
    }
}
