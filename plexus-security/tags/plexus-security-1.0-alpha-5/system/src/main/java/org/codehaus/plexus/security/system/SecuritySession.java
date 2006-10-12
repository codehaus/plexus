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

import org.codehaus.plexus.security.authentication.AuthenticationResult;
import org.codehaus.plexus.security.user.User;

/**
 * @author Jason van Zyl
 */
public interface SecuritySession
{
    public static final String ROLE = SecuritySession.class.getName(); 

    public static final String USERKEY = "SecuritySessionUser";

    AuthenticationResult getAuthenticationResult();

    User getUser();
    
    boolean isAuthenticated();
}
