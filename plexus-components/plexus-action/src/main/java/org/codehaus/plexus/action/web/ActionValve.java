package org.codehaus.plexus.action.web;

/*
 * Copyright (c) 2004, Codehaus.org
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

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.action.Action;
import org.codehaus.plexus.action.ActionManager;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.pipeline.valve.AbstractValve;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public class ActionValve
    extends AbstractValve
{
    private ActionManager actionManager;

    public void invoke( RunData data )
        throws IOException, SummitException
    {
        String actionId = data.getRequest().getParameter( "action" );

        if ( actionId != null )
        {
            Action action = null;

            try
            {
                action = actionManager.lookup( actionId.trim() );
            }
            catch ( Exception e )
            {
                getLogger().error( "Cannot find action with the id of " + actionId );
            }

            try
            {
                // The parameter map in the request consists of an array of values for
                // the given key so this is why this is being done.

                Map m = new HashMap();

                for ( Enumeration e = data.getRequest().getParameterNames(); e.hasMoreElements(); )
                {
                    String key = (String) e.nextElement();

                    m.put( key, data.getRequest().getParameter( key ) );
                }

                m.put( "data", data );

                action.execute( m );
            }
            catch ( Exception e )
            {
                e.printStackTrace();

                getLogger().error( e.getMessage(), e );
            }
        }
    }
}
