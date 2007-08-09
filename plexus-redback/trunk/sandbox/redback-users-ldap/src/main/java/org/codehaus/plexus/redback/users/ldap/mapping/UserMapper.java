package org.codehaus.plexus.redback.users.ldap.mapping;

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

import org.codehaus.plexus.redback.users.User;
import org.codehaus.plexus.redback.users.ldap.BasicUser;

import javax.naming.directory.Attributes;

public interface UserMapper
{

    BasicUser getUser( Attributes attributes )
        throws MappingException;

    Attributes getCreationAttributes( User user, boolean encodePasswordIfChanged )
        throws MappingException;

    UserUpdate getUpdate( BasicUser user )
        throws MappingException;

    String[] getUserAttributeNames();

    String getEmailAddressAttribute();

    String getUserFullNameAttribute();

    String getPasswordAttribute();

    String getUserIdAttribute();

    String getEmailAttribute();

    String getUserBaseDn();

    String getUserObjectClass();

    BasicUser newUserInstance( String username, String fullName, String email );

    BasicUser newTemplateUserInstance();

}
