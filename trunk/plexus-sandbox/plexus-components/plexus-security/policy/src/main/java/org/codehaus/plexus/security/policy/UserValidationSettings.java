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
 * UserValidationSettings 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public interface UserValidationSettings
{
    public static final String ROLE = UserValidationSettings.class.getName();
    
    /**
     * Get the email validation url
     * 
     * @return the email validation url 
     */
    public String getEmailValidationUrl();
    
    /**
     * Set the email validation url
     * 
     * @param url the email validation url.
     */
    public void setEmailValidationUrl(String url);
    
    /**
     * Get the flag indicating if a new users require email validation or not.
     * 
     * @return
     */
    public boolean isEmailValidationRequired();
    
    /**
     * Sets the flag indicating if new users require email validation or not.
     * 
     * @param required
     */
    public void setEmailValidationRequired( boolean required );
    
    /**
     * Gets the number of minutes until the email validation message key
     * should expire.
     *  
     * @return the email validation timeout (in minutes).
     */
    public int getEmailValidationTimeout();
    
    /**
     * Sets the number of minutes until the email validation message key
     * should expire.
     * 
     * @param minutes number of minutes until the key for the email validation expires.
     */
    public void setEmailValidationTimeout(int minutes);
}
