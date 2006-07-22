package org.codehaus.plexus.security;
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
 * @author <a hrel="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id: User.java 2998 2006-01-10 14:14:18Z evenisse $
 */
public interface User
{
    void setUsername( String username );

    void setPassword( String password );

    String getUsername();

    String getPassword();

    boolean isEnabled();

    boolean isAccountNonExpired();

    boolean isAccountNonLocked();

    boolean isPasswordNonExpired();

    Object getDetails();
}
