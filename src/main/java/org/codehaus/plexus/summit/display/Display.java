package org.codehaus.plexus.summit.display;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
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

import org.codehaus.plexus.summit.rundata.RunData;

/**
 * A <code>Display</code> uses a previously computed <code>Resolution</code>
 * to render the target view along with any of its sibling views and the
 * execution of any accompanying <code>Modules</code>.
 *
 * @author <a href="mailto:jvanzyl@zenplex.com">Jason van Zyl</a>
 * @version $Id$
 * @todo Determine whether we actually need RunData here and if it would
 * simply suffice to pass in the Resolution as a parameter.
 */
public interface Display
{
    public final static String ROLE = Display.class.getName();

    void render( RunData data )
        throws Exception;
}
