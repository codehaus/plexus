package org.codehaus.plexus.security.ui.web;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

import org.codehaus.plexus.security.system.SecuritySession;
import org.codehaus.plexus.security.user.User;

/**
 * WebSecurityConstants - constants for use with web applications that use plexus-security. 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class WebSecurityConstants
{
    /**
     * Key in the sessionScope for the {@link SecuritySession} object. 
     */
    public static final String SECURITY_SESSION_KEY = "securitySession";
    
    /**
     * Key in the sessionScope for the {@link User} object.
     */
    public static final String SECURITY_SESSION_USER = "securityUser";
    
    /**
     * Key in the sessionScope for the flag indicating if the authenticated or not.
     */
    public static final String SECURITY_SESSION_AUTH = "securityAuth";
}
