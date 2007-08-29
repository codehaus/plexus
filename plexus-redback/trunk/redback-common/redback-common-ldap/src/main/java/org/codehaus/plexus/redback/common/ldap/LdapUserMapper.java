package org.codehaus.plexus.redback.common.ldap;

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
import org.codehaus.plexus.util.StringUtils;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

/**
 * 
 * @author <a href="jesse@codehaus.org"> jesse
 * @version "$Id: BasicUserMapper.java 6784 2007-08-23 19:21:13Z jesse $"
 *
 * @plexus.component role="org.codehaus.plexus.redback.users.ldap.mapping.UserMapper" role-hint="ldap"
 */
public class LdapUserMapper
    implements UserMapper
{
    /**
     * @plexus.configuration default-value="email"
     */
    String emailAttribute;

    /**
     * @plexus.configuration default-value="givenName"
     */
    String fullNameAttribute;

    /**
     * @plexus.configuration default-value="userPassword"
     */
    String passwordAttribute;

    /**
     * @plexus.configuration default-value="cn"
     */
    String userIdAttribute;

    /**
     * @plexus.configuration default-value=""
     */
    String userBaseDn;

    /**
     * @plexus.configuration default-value="inetOrgPerson"
     */
    String userObjectClass;

    public Attributes getCreationAttributes( User user, boolean encodePasswordIfChanged )
        throws MappingException
    {
        Attributes userAttrs = new BasicAttributes();

        boolean passwordSet = false;

        if ( !passwordSet && ( user.getEncodedPassword() != null ) )
        {
            userAttrs.put( getPasswordAttribute(), user.getEncodedPassword() );
        }

        if ( !StringUtils.isEmpty( user.getFullName() ) )
        {
            userAttrs.put( getUserFullNameAttribute(), user.getFullName() );
        }

        if ( !StringUtils.isEmpty( user.getEmail() ) )
        {
            userAttrs.put( getEmailAddressAttribute(), user.getEmail() );
        }

        return userAttrs;
    }

    public String getEmailAddressAttribute()
    {
        return emailAttribute;
    }

    public String getUserFullNameAttribute()
    {
        return fullNameAttribute;
    }

    public String getPasswordAttribute()
    {
        return passwordAttribute;
    }

    public String[] getUserAttributeNames()
    {
        return new String[] { emailAttribute, fullNameAttribute, passwordAttribute, userIdAttribute };
    }

    public UserUpdate getUpdate( LdapUser user )
        throws MappingException
    {

        Attributes addAttrs = new BasicAttributes();

        Attributes modAttrs = new BasicAttributes();

        if ( !StringUtils.isEmpty( user.getFullName() ) )
        {
            if ( user.getFullName() == null )
            {
                addAttrs.put( getUserFullNameAttribute(), user.getFullName() );
            }
            else if ( !user.getFullName().equals( user.getFullName() ) )
            {
                modAttrs.put( getUserFullNameAttribute(), user.getFullName() );
            }
        }

        if ( !StringUtils.isEmpty( user.getEmail() ) )
        {
            if ( user.getEmail() == null )
            {
                addAttrs.put( getEmailAddressAttribute(), user.getEmail() );
            }
            else if ( !user.getEmail().equals( user.getEmail() ) )
            {
                modAttrs.put( getEmailAddressAttribute(), user.getEmail() );
            }
        }
        
        return null;
    }

    public LdapUser getUser( Attributes attributes )
        throws MappingException
    {
        String userIdAttribute = getUserIdAttribute();
        String emailAddressAttribute = getEmailAddressAttribute();
        String nameAttribute = getUserFullNameAttribute();
        String passwordAttribute = getPasswordAttribute();

        String userId = ( LdapUtils.getAttributeValue( attributes, userIdAttribute, "username" ) );

        LdapUser user = new LdapUser( userId );
        user.setOriginalAttributes( attributes );
        
        user.setEmail( LdapUtils.getAttributeValue( attributes, emailAddressAttribute, "email address" ) );
        user.setFullName( LdapUtils.getAttributeValue( attributes, nameAttribute, "name" ) );
        
        String encodedPassword = LdapUtils.getAttributeValueFromByteArray( attributes, passwordAttribute, "password" );
        
        if ( encodedPassword != null && encodedPassword.startsWith( "{" ) )
        {
            encodedPassword = encodedPassword.substring( encodedPassword.indexOf( "}" ) + 1 );
        }
        
        user.setEncodedPassword( encodedPassword );

        return user;
    }

    public String getUserIdAttribute()
    {
        return userIdAttribute;
    }

    public String getEmailAttribute()
    {
        return emailAttribute;
    }

    public void setEmailAttribute( String emailAttribute )
    {
        this.emailAttribute = emailAttribute;
    }

    public String getFullNameAttribute()
    {
        return fullNameAttribute;
    }

    public void setFullNameAttribute( String fullNameAttribute )
    {
        this.fullNameAttribute = fullNameAttribute;
    }

    public String getUserBaseDn()
    {
        return userBaseDn;
    }

    public void setUserBaseDn( String userBaseDn )
    {
        this.userBaseDn = userBaseDn;
    }

    public String getUserObjectClass()
    {
        return userObjectClass;
    }

    public void setUserObjectClass( String userObjectClass )
    {
        this.userObjectClass = userObjectClass;
    }

    public void setPasswordAttribute( String passwordAttribute )
    {
        this.passwordAttribute = passwordAttribute;
    }

    public void setUserIdAttribute( String userIdAttribute )
    {
        this.userIdAttribute = userIdAttribute;
    }

    public LdapUser newUserInstance( String username, String fullName, String email )
    {
        return new LdapUser( username, fullName, email );
    }

    public LdapUser newTemplateUserInstance()
    {
        return new LdapUser();
    }

}
