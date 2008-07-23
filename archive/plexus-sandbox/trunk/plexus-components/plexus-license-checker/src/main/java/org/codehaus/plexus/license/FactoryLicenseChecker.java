package org.codehaus.plexus.license;

/**
 *
 * Copyright 2007
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
 * @author John Tolentino
 */
public class FactoryLicenseChecker
{
    private static final String JAVA_EXTENSION = ".java";

    private static final String XML_EXTENSION = ".xml";

    private static final String HTML_EXTENSION = ".html";

    private static final String HTM_EXTENSION = ".htm";

    private static final String APT_EXTENSION = ".apt";

    public static AbstractLicenseChecker getLicenseChecker( String filename, String startOfLicense,
                                                            String endOfLicense )
    {
        AbstractLicenseChecker checker = null;

        if ( filename.toLowerCase().endsWith( JAVA_EXTENSION ) )
        {
            checker = new JavaLicenseChecker( startOfLicense, endOfLicense );
        }
        else if ( filename.toLowerCase().endsWith( XML_EXTENSION ) ||
            filename.toLowerCase().endsWith( HTM_EXTENSION ) || filename.toLowerCase().endsWith( HTML_EXTENSION ) )
        {
            checker = new XmlLicenseChecker( startOfLicense, endOfLicense );
        }
        else if ( filename.toLowerCase().endsWith( APT_EXTENSION ) )
        {
            checker = new AptLicenseChecker( startOfLicense, endOfLicense );
        }
        return checker;
    }
}
