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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.plexus.action.Action;
import org.codehaus.plexus.action.ActionManager;
import org.codehaus.plexus.action.ActionNotFoundException;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.pipeline.valve.AbstractValve;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 * @plexus.component
 *   role="org.codehaus.plexus.summit.pipeline.valve.Valve"
 *   role-hint="org.codehaus.plexus.action.web.ActionValve"
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class ActionValve
    extends AbstractValve
{
    /**
     * @plexus.requirement
     */
    private ActionManager actionManager;

    public void invoke( RunData data )
        throws IOException, SummitException
    {
        String actionId = data.getParameters().getString( "action", "" );

        if ( !actionId.equals( "" ) )
        {
            Action action = null;

            try
            {
                action = actionManager.lookup( actionId.trim() );
            }
            catch ( ActionNotFoundException e )
            {
                getLogger().error( "Cannot find action with the id of " + actionId, e );

                return;
            }

            try
            {
                Map m = createContext( data );

                m.put( "data", data );

                action.execute( m );
            }
            catch ( Exception e )
            {
                getLogger().error( e.getMessage(), e );
            }
        }
    }

    protected Map createContext( RunData data )
    {
        // The parameter map in the request consists of an array of values for
        // the given key so this is why this is being done.
        Map m = new HashMap();

        for ( Iterator i = data.getParameters().keys(); i.hasNext(); )
        {
            String key = (String) i.next();

            m.put( key, data.getParameters().get( key ) );
        }
        return m;
    }
}
