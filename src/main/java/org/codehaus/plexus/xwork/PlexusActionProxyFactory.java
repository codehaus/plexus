package org.codehaus.plexus.xwork;

/**
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

import com.opensymphony.xwork.ActionProxyFactory;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.DefaultActionInvocation;

import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusActionProxyFactory
//    extends ActionProxyFactory
{/*
    public ActionInvocation createActionInvocation( ActionProxy actionProxy )
        throws Exception
    {
        createActionInvocation( actionProxy, null, false );
    }

    public ActionInvocation createActionInvocation( ActionProxy actionProxy, Map extraContext )
        throws Exception
    {
        createActionInvocation( actionProxy, extraContext, true );
    }

    public ActionInvocation createActionInvocation( ActionProxy actionProxy, Map extraContext, boolean pushAction )
        throws Exception
    {
        return new PlexusActionInvocation( actionProxy, extraContext, pushAction );
    }

    public ActionProxy createActionProxy( String namespace, String actionName, Map extraContext )
        throws Exception
    {
    }

    public ActionProxy createActionProxy( String namespace, String actionName, Map extraContext, boolean executeResult )
        throws Exception
    {
    }
*/
}
