package org.codehaus.plexus.component.repository.exception;

/*
 * Copyright 2001-2006 Codehaus Foundation.
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
 * Exception that is thrown when the class(es) required for a component
 * implementation are not available.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class ComponentProfileException
    extends Exception
{
    private static final long serialVersionUID = -4099483467183306853L;

    /**
     * Construct a new <code>ComponentProfileException</code> instance.
     * @param message exception message
     */
    public ComponentProfileException( String message )
    {
        super( message );
    }

    /**
     * Construct a new <code>ComponentProfileException</code> instance.
     * @param message exception message
     * @param cause causing exception to chain
     */
    public ComponentProfileException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
