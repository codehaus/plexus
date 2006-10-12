package org.codehaus.plexus.security.policy;

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
 * SingleSignOnSettings 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public interface SingleSignOnSettings
{
    /**
     * Gets the flag enabled or not for Single Sign On.
     * 
     * @return true if single sign on is enabled.
     */
    public boolean isEnabled();
    
    /**
     * Gets the Cookie Domain for the Single Sign On.
     *  
     * @return the domain portion of the cookie for single sign on.
     */
    public String getCookieDomain();
    
    /**
     * Gets the Cookie timeout (in minutes) for the Single Sign On.
     * 
     * @return the cookie timeout (in minutes) for the single sign on.
     */
    public int getCookieTimeout();
}
