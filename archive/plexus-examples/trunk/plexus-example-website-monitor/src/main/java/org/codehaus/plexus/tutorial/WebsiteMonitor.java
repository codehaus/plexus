/**
 * ========================================================================
 * 
 * Copyright 2006 Rahul Thakur.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ========================================================================
 */
package org.codehaus.plexus.tutorial;

import java.util.List;

/**
 * Plexus Component role that is expected to be implemented by all provider that
 * can monitor websites.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public interface WebsiteMonitor
{

    /**
     * Role used to register component implementations with the container.
     */
    String ROLE = WebsiteMonitor.class.getName();

    /**
     * Monitor the specified website at the specified time intervals.
     * 
     * @throws Exception
     *             error encountered while performing the monitoring request.
     */
    public void monitor()
        throws Exception;

    /**
     * Specify a list of websites that can be monitored by this component.
     * 
     * @param websites List of websites
     */
    void addWebsites( List websites );

    /**
     * Determines if the website monitor component was properly initialized.
     *  
     * @return <code>true</code> if initiailized, else <code>false</code>.
     */
    boolean isInitialized();
}
