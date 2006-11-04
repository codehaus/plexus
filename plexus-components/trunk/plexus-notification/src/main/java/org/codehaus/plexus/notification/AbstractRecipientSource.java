package org.codehaus.plexus.notification;

/*
 * The MIT License
 *
 * Copyright (c) 2004-2005, The Codehaus
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

import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public abstract class AbstractRecipientSource
    extends AbstractLogEnabled
    implements RecipientSource
{

    /**
     * @see org.codehaus.plexus.notification.RecipientSource#getRecipients(java.lang.String, java.lang.String, java.util.Map)
     */
    public Set getRecipients( String notifierType, String messageId, Map context )
        throws NotificationException
    {
        return getRecipients( notifierType, messageId, Collections.EMPTY_MAP, context );
    }

    /**
     * @see org.codehaus.plexus.notification.RecipientSource#getRecipients(java.lang.String, java.lang.String, java.util.Properties, java.util.Map)
     */
    public Set getRecipients( String notifierType, String messageId, Properties configuration, Map context )
        throws NotificationException
    {
        Map conf = new HashMap();

        for ( Iterator i = configuration.keySet().iterator(); i.hasNext(); )
        {
            String key = (String) i.next();

            String value = configuration.getProperty( key );

            conf.put( key, value );
        }

        if ( conf.isEmpty() )
        {
            return getRecipients( notifierType, messageId, Collections.EMPTY_MAP, context );
        }
        else
        {
            return getRecipients( notifierType, messageId, conf, context );
        }
    }
}
