package org.codehaus.plexus.security.simple;

import org.codehaus.plexus.security.AuthenticationResult;
import org.codehaus.plexus.security.User;
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
 * SimpleAuthenticationResult:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 */
public class SimpleAuthenticationResult
    implements AuthenticationResult
{
    private boolean isAuthentic;

    private User user;

    public void setAuthenticated( boolean isAuthenticated )
    {
        isAuthentic = isAuthenticated;
    }

    public boolean isAuthenticated()
    {
        return isAuthentic;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser( User user )
    {
        this.user = user;
    }
}
