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
 * ApplicationDetails 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public interface ApplicationDetails
{
    public static final String ROLE = ApplicationDetails.class.getName();

    /**
     * Get the Application Name for this application.
     * 
     * @return the name of this application.
     */
    public String getApplicationName();

    /**
     * Get the url to the top level access for this application.
     * 
     * @return the application url.
     */
    public String getApplicationUrl();

    /**
     * Get the timestamp format to use within the entire applicaiton.
     * 
     * @return the timestamp format.
     */
    public String getTimestampFormat();
}
