package org.codehaus.plexus.security.authentication;

import org.codehaus.plexus.security.policy.AccountLockedException;
import org.codehaus.plexus.security.policy.MustChangePasswordException;

import java.util.List;
/*
 * Copyright 2005 The Codehaus.
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
 * AuthenticationManager:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 */
public interface AuthenticationManager
{
    String ROLE = AuthenticationManager.class.getName();

    public String getId();

    public List getAuthenticators();

    public AuthenticationResult authenticate( AuthenticationDataSource source )
        throws AccountLockedException, MustChangePasswordException, AuthenticationException;
}