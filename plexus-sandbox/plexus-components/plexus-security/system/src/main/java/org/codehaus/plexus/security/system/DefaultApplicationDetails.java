package org.codehaus.plexus.security.system;

/*
 * Copyright 2001-2006 The Codehaus.
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
 * DefaultApplicationDetails 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.codehaus.plexus.security.system.ApplicationDetails"
 */
public class DefaultApplicationDetails
    implements ApplicationDetails
{
    /**
     * @plexus.configuration default-value="Unconfigured Application Name"
     */
    private String applicationName;
    
    /**
     * @plexus.configuration default-value="http://localhost/"
     */
    private String applicationUrl;
    
    /**
     * @plexus.configuration default-value="EEE, d MMM yyyy HH:mm:ss Z"
     */
    private String timestampFormat;

    public String getApplicationName()
    {
        return applicationName;
    }

    public String getApplicationUrl()
    {
        return applicationUrl;
    }

    public String getTimestampFormat()
    {
        return timestampFormat;
    }
}
