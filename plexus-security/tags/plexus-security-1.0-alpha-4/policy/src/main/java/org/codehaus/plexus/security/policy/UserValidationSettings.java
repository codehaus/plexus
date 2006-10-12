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
     * Get the flag indicating if a new users require email validation or not.
     * 
     * @return
     */
    public boolean isEmailValidationRequired();
    
    /**
     * Gets the number of minutes until the email validation message key
     * should expire.
     *  
     * @return the email validation timeout (in minutes).
     */
    public int getEmailValidationTimeout();
    
    /**
     * Get the login path to submit the validation email to.
     * 
     * @return the login path to submit the validation email to.
     */
    public String getEmailLoginPath();
    
    /**
     * Get the subject line for the validation email.
     * 
     * @return the subject line for the validation email.
     */
    public String getEmailSubject();
}
