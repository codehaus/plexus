package org.codehaus.plexus.summit.resolver;

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

/**
 * <p>A resolver is a strategy for determining how target view will be
 * rendered. The target view may have sibling views which are to be
 * rendered and there may be <code>Modules</code> that must be
 * executed in order to populate the <code>ViewContext</code>.</p>
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface Resolver
{
    public final static String ROLE = Resolver.class.getName();

    Resolution resolve( String view )
        throws Exception;

    String getInitialView();

    String getDefaultView();

    String getErrorView();

    String getResultMessagesView();
}
