package org.codehaus.plexus.action;

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

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * @author <a href="mailto:jason@codehaus.com">Jason van Zyl</a>
 * @version $Id$
 */
public abstract class AbstractAction
    extends AbstractLogEnabled
    implements Contextualizable, Action
{
    private PlexusContainer container;

    public void setResultMessages( List resultMessages, Map parameters )
    {
        parameters.put( RESULT_MESSAGES, resultMessages );
    }

    public List getResultMessages( Map parameters )
    {
        return (List) parameters.get( RESULT_MESSAGES );
    }

    public void addResultMessage( String message, Map parameters )
    {
        List resultMessages = (List) parameters.get( RESULT_MESSAGES );

        if ( resultMessages == null )
        {
            resultMessages = new ArrayList();

            parameters.put( RESULT_MESSAGES, resultMessages );
        }

        resultMessages.add( message );
    }

    public boolean hasResultMessages( Map parameters )
    {
        return parameters.containsKey( RESULT_MESSAGES );
    }

    public void contextualize( Context context )
        throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    protected Object lookup( String role )
        throws ComponentLookupException
    {
        return container.lookup( role );
    }

    protected Object lookup( String role, String roleHint )
        throws ComponentLookupException
    {
        return container.lookup( role, roleHint );
    }
}
