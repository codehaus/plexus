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

import org.codehaus.plexus.redback.password.PasswordManager;
import org.codehaus.plexus.redback.password.PasswordManagerException;
import org.codehaus.plexus.redback.password.UnsupportedAlgorithmException;
import org.codehaus.plexus.redback.users.User;
import org.codehaus.plexus.redback.users.ldap.BasicUser;
import org.codehaus.plexus.util.StringUtils;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

/**
 * 
 * @author <a href="jesse@codehaus.org"> jesse
 * @version "$Id$"
 *
 * @plexus.component role="org.codehaus.plexus.redback.users.ldap.mapping.UserMapper" role-hint="basic"
 */
public class BasicUserMapper
    implements UserMapper
{

    public static final String USERNAME = "username";

    public static final String PASSWORD = "password";

    public static final String EMAIL = "email";

    public static final String NAME = "name";

//    public static final String WEBSITE = "website";

    private PasswordManager passwordManager;

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
        if ( encodePasswordIfChanged && !StringUtils.isEmpty( user.getPassword() ) )
        {
            try
            {
                userAttrs.put( getPasswordAttribute(),
                               passwordManager.encodePasswordUsingDefaultHash( user.getPassword(), null ) );
                passwordSet = true;
            }
            catch ( UnsupportedAlgorithmException e )
            {
                throw new MappingException( "Failed to encode user password: " + e.getMessage(), e );
            }
            catch ( PasswordManagerException e )
            {
                throw new MappingException( "Failed to encode user password: " + e.getMessage(), e );
            }
        }

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

//        if ( !StringUtils.isEmpty( user.getWebsite() ) )
//        {
//            if ( configuration.isWebsiteAttributeLabelUri() )
//            {
//                userAttrs.put( configuration.getWebsiteAttribute(), user.getWebsite() + " " + configuration.getWebsiteUriLabel() );
//            }
//            else
//            {
//                userAttrs.put( configuration.getWebsiteAttribute(), user.getWebsite() );
//            }
//        }

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

    public UserUpdate getUpdate( BasicUser user )
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

//        if ( !StringUtils.isEmpty( user.getWebsite() ) )
//        {
//            if ( user.getWebsite() == null )
//            {
//                if ( configuration.isWebsiteAttributeLabelUri() )
//                {
//                    addAttrs.put( configuration.getWebsiteAttribute(), user.getWebsite() + " " + configuration.getWebsiteUriLabel() );
//                }
//                else
//                {
//                    addAttrs.put( configuration.getWebsiteAttribute(), user.getWebsite() );
//                }
//            }
//            else if ( !user.getWebsite().equals( user.getWebsite() ) )
//            {
//                if ( configuration.isWebsiteAttributeLabelUri() )
//                {
//                    modAttrs.put( configuration.getWebsiteAttribute(), user.getWebsite() + " " + configuration.getWebsiteUriLabel() );
//                }
//                else
//                {
//                    modAttrs.put( configuration.getWebsiteAttribute(), user.getWebsite() );
//                }
//            }
//        }

        return null;
    }

    public BasicUser getUser( Attributes attributes )
        throws MappingException
    {
        String userIdAttribute = getUserIdAttribute();
        String emailAddressAttribute = getEmailAddressAttribute();
        String nameAttribute = getUserFullNameAttribute();
//        String websiteAttribute = getWebsiteAttribute();
//        String websiteUriLabel = getWebsiteUriLabel();
        String passwordAttribute = getPasswordAttribute();

        String userId = ( LdapUtils.getAttributeValue( attributes, userIdAttribute, "username" ) );

        BasicUser user = new BasicUser( userId );
        user.setOriginalAttributes( attributes );
        
        user.setEmail( LdapUtils.getAttributeValue( attributes, emailAddressAttribute, "email address" ) );
        user.setFullName( LdapUtils.getAttributeValue( attributes, nameAttribute, "name" ) );
        user.setEncodedPassword( LdapUtils.getAttributeValue( attributes, passwordAttribute, "password" ) );

//        if ( configuration.isWebsiteAttributeLabelUri() )
//        {
//            user.setWebsite( LdapUtils.getLabeledUriValue( attributes, websiteAttribute, websiteUriLabel, "website" ) );
//        }
//        else
//        {
//            user.setWebsite( LdapUtils.getAttributeValue( attributes, websiteAttribute, "website" ) );
//        }

        return user;
    }

    public String getUserIdAttribute()
    {
        return userIdAttribute;
    }

    public PasswordManager getPasswordManager()
    {
        return passwordManager;
    }

    public void setPasswordManager( PasswordManager passwordManager )
    {
        this.passwordManager = passwordManager;
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

    public BasicUser newUserInstance( String username, String fullName, String email )
    {
        return new BasicUser( username, fullName, email );
    }

    public BasicUser newTemplateUserInstance()
    {
        return new BasicUser();
    }

}
