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
 * EmailSettings 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public interface EmailSettings
{
    public static final String ROLE = EmailSettings.class.getName();
    
    /**
     * Get the Feedback to use for any outgoing emails.
     * 
     * NOTE: if feedback starts with a "/" it is appended to the end of the value provided in 
     * {@link ApplicationDetails#getApplicationUrl()}, otherwise it it used as is.
     * 
     * This value can be in the format/syntax of <code>"/feedback.action"</code> or 
     * even <code>"mailto:feedback@application.com"</code>
     * 
     * @return the feedback 
     */
    public String getFeedback();    
    
    /**
     * Get the email from address.
     * 
     * @return the address to put in the from portion of the email.
     */
    public String getFromAddress();

    /**
     * Get the email from user name.
     * 
     * @return the user name to put in the from portion of the email.
     */
    public String getFromUsername();
}
