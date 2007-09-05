package org.codehaus.plexus.redback.password.hash;

/*
 * Copyright 2001-2007 The Codehaus.
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

public interface PasswordHash
{
	public static final String ROLE = PasswordHash.class.getName();
	
    public String encodePassword( String password, Object salt )
        throws PasswordHashException;

    public boolean checkPassword( String encPassword, String input, Object salt )
        throws PasswordHashException;

}
