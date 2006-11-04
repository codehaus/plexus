package org.codehaus.plexus.installer;

/*
 * Copyright 2005 The Apache Software Foundation.
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

import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * Exception when no installer exists.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class NoSuchInstallerException
    extends NoSuchArchiverException
{
    private final String installerName;

    /**
     *
     * @param installerName
     */
    public NoSuchInstallerException( String installerName )
    {
        super( "No such installer: '" + installerName + "'." );

        this.installerName = installerName;
    }

    public NoSuchInstallerException( String installerName, ComponentLookupException e )
    {
        super( "No such installer: " + installerName + ". Error: " + e.getMessage() );
        initCause( e );

        this.installerName = installerName;
    }

    /**
     * Return the installer name
     *
     * @return the installer name
     */
    public String getInstallerName()
    {
        return installerName;
    }
}
