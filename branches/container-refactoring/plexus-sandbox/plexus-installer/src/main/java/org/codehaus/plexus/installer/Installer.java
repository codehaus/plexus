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

import java.io.File;
import java.util.Map;

import org.codehaus.plexus.archiver.Archiver;

/**
 * Interface to create installer
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public interface Installer
    extends Archiver
{
    String ROLE = Installer.class.getName();

    /**
     * Generate an installer script with the Velocity component for instance.
     *
     * @throws InstallerException if any
     */
    void createInstallerScript()
        throws InstallerException;

    /**
     * Compile the generated script, ie call the third party compiler.
     *
     * @throws InstallerException if any
     */
    void createInstaller()
        throws InstallerException;

    /**
     * Specify the compiler path
     *
     * @param compilerFile
     * @throws InstallerException if any
     */
    void setCompiler( File compilerFile )
        throws InstallerException;

    /**
     * Specify an installer name, if needed
     *
     * @param installerName
     */
    void setInstallerName( String installerName );

    /**
     * Specify the product name.
     *
     * @param name
     */
    void setProductName( String name );

    /**
     * Specify the product version
     *
     * @param version
     */
    void setProductVersion( String version );

    /**
     * Specify the product company name.
     *
     * @param company
     */
    void setProductCompany( String company );

    /**
     * Specify the product URL.
     *
     * @param url
     */
    void setProductURL( String url );

    /**
     * Specify the installer/product license.
     *
     * @param license
     */
    void setProductLicense( File license );

    /**
     * Specify a template in the current class loader
     *
     * @param template
     */
    public void setTemplate( String template );

    /**
     * Specify a template as file
     *
     * @param template a user's template
     * @param templateProperties user's properties for the given template
     * @throws InstallerException if any
     */
    public void setTemplate( File template, Map templateProperties )
        throws InstallerException;
}
