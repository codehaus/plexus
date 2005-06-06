package org.codehaus.plexus.summit.view;

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
 * <p>A <code>View</code> is a container for the name of a resolved
 * view and anything else that might eventually be associated with
 * a view. </p>
 *
 * @author <a href="mailto:jvanzyl@zenplex.com">Jason van Zyl</a>
 * @version $Id$
 */
public interface View
{
    /**
     * Set the name of the view.
     */
    void setName( String name );

    /**
     * Get the name of the view.
     */
    String getName();
}
