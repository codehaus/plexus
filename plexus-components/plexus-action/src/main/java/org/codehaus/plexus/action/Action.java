package org.codehaus.plexus.action;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Map;

/**
 * An <code>Action</code in Plexus is meant to model the UML notion of an Action as closely
 * as possible.
 *
 * An action is an executable atomic computation. Actions may include operation calls, the
 * creation or destruction of another object, or the sending of a signal to an object.
 *
 * An action is atomic, meaning that it cannot be interrupted by an event and therefore
 * runs to completion. This is in contrast to an activity, which may be interrupted
 * by other events. 89
 *
 * @author <a href="mailto:jason@codehaus.com">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface Action
{
    static String ROLE = Action.class.getName();

    void execute( Map context )
        throws Exception;
}
