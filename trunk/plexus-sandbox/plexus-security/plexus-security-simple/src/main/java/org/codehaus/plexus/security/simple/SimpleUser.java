package org.codehaus.plexus.security.simple;

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
 * SimpleUser:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 */
public class SimpleUser
    implements User
{
    private String username;

    private String password;

    private String fullName;

    private String email;

    public void setUsername( String username )
    {
        this.username = username;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public void setFullName( String fullName )
    {
        this.fullName = fullName;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public String getUsername()
    {
        return null;
    }

    public String getPassword()
    {
        return null;
    }

    public String getFullName()
    {
        return null;
    }

    public String getEmail()
    {
        return null;
    }

    public boolean isEnabled()
    {
        return false;
    }

    public boolean isAccountNonExpired()
    {
        return false;
    }

    public boolean isAccountNonLocked()
    {
        return false;
    }

    public boolean isPasswordNonExpired()
    {
        return false;
    }

    public Object getDetails()
    {
        return null;
    }
}
