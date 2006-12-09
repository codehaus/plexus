package org.codehaus.plexus.xwork;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * ComponentNotFoundException - Component Lookup Failed due to lack of matching implementation for specified role/class.  
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class ComponentNotFoundException
    extends ComponentLookupException
{

    public ComponentNotFoundException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public ComponentNotFoundException( String message )
    {
        super( message );
    }

}
