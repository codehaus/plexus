package org.codehaus.plexus.summit.pull;

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
import org.codehaus.plexus.summit.view.ViewContext;

/**
 * <p>The Pull Service manages the creation of application tools that are
 * available to all templates in a Turbine application. By using the Pull
 * Service you can avoid having to make Screens to populate a context for use in
 * a particular template. The Pull Service creates a set of tools, as specified
 * in the TR. props file.</p>
 * <p/>
 * <p>These tools can have global scope, request scope, session scope (i.e.
 * stored in user temp hashmap) or persistent scope (i.e. stored in user perm
 * hashmap).</p>
 * <p/>
 * <p>The standard way of referencing these global tools is through the toolbox
 * handle. This handle is typically $toolbox, but can be specified in the TR.
 * props file.</p>
 * <p/>
 * <p>So, for example, if you had a UI Manager tool which created a set of UI
 * attributes from a properties file, and one of the properties was 'bgcolor',
 * then you could access this UI attribute with $ui.bgcolor. The identifier that
 * is given to the tool, in this case 'ui', can be specified as well.</p>
 *
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @version $Id$
 */
public interface PullService
{
    /**
     * The Avalon role
     */
    public static final String ROLE = PullService.class.getName();

    /**
     * Populate the given context with all request, session and persistent scope
     * tools (it is assumed that the context already wraps the global context,
     * and thus already contains the global tools).
     *
     * @param context a ViewContext to populate
     * @param data    a RunData object for request specific data
     */
    public void populateContext( ViewContext context, RunData data );

    /**
     * Release tool instances from the given context to the
     * object pool
     *
     * @param context a ViewContext to release tools from
     */
    public void releaseTools( ViewContext context );

}
