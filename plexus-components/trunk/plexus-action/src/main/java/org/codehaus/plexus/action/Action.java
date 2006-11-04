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

import java.util.Map;
import java.util.List;

/**
 * An <code>Action</code in Plexus is meant to model the UML notion of an Action as closely
 * as possible.
 * <p/>
 * An action is an executable atomic computation. Actions may include operation calls, the
 * creation or destruction of another object, or the sending of a signal to an object.
 * <p/>
 * An action is atomic, meaning that it cannot be interrupted by an event and therefore
 * runs to completion. This is in contrast to an activity, which may be interrupted
 * by other events. 89
 *
 * @author <a href="mailto:jason@codehaus.com">Jason van Zyl</a>
 * @version $Id$
 */
public interface Action
{
    /**
     * Key for a result messages that must make its way to a user. This could be a validation
     * result message that indicates something went wrong with processing inside an action.
     */
    public static final String RESULT_MESSAGES =  "action.resultMessages";

    static String ROLE = Action.class.getName();

    public void setResultMessages( List resultMessages, Map parameters );

    public void addResultMessage( String message, Map parameters );

    public List getResultMessages( Map parameters );

    public boolean hasResultMessages( Map parameters );

    void execute( Map context )
        throws Exception;
}
