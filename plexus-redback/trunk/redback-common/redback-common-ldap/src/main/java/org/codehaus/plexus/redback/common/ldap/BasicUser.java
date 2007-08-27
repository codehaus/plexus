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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.directory.Attributes;

public class BasicUser
    implements User
{

    private String key;
    private String username;
    private String fullName;
    private String email;
    private String encodedPassword;

    private List<String> previousEncodedPasswords;

    private boolean locked = false;
    private boolean requiresPasswordChange = false;
    private boolean permanent = true;
    private boolean valid = true;

    private Date creationDate;

    private int failedLoginAttempts;
    private Date lastLoginDate = new Date();
    private Date lastPasswordChange = new Date();

    // DO NOT STORE AS SUCH!!!
    private String newPassword;

    private Attributes originalAttributes;

    public BasicUser( String username )
    {
        key = username;
        this.username = username;
        previousEncodedPasswords = new ArrayList<String>();
        failedLoginAttempts = 0;
        creationDate = new Date();
    }

    public BasicUser( String username, String fullName, String email )
    {
        this( username );
        this.fullName = fullName;
        this.email = email;
    }

    public BasicUser()
    {
        previousEncodedPasswords = new ArrayList<String>();
        failedLoginAttempts = Integer.MIN_VALUE;
    }

    public void addPreviousEncodedPassword( String encodedPassword )
    {
        previousEncodedPasswords.add( encodedPassword );
    }

    public Date getAccountCreationDate()
    {
        return creationDate;
    }

    public int getCountFailedLoginAttempts()
    {
        return failedLoginAttempts;
    }

    public String getEmail()
    {
        return email;
    }

    public String getEncodedPassword()
    {
        return encodedPassword;
    }

    public String getFullName()
    {
        return fullName;
    }

    public Date getLastLoginDate()
    {
        return lastLoginDate;
    }

    public Date getLastPasswordChange()
    {
        return lastPasswordChange;
    }

    public String getPassword()
    {
        return newPassword;
    }

    @SuppressWarnings("unchecked")
    public List getPreviousEncodedPasswords()
    {
        return previousEncodedPasswords;
    }

    public Object getPrincipal()
    {
        return key;
    }

    public String getUsername()
    {
        return username;
    }

    public boolean isLocked()
    {
        return locked;
    }

    public boolean isPasswordChangeRequired()
    {
        return requiresPasswordChange;
    }

    public boolean isPermanent()
    {
        return permanent;
    }

    public boolean isValidated()
    {
        return valid;
    }

    public void setCountFailedLoginAttempts( int count )
    {
        failedLoginAttempts = count;
    }

    public void setEmail( String address )
    {
        email = address;
    }

    public void setEncodedPassword( String encodedPassword )
    {
        this.encodedPassword = encodedPassword;
    }

    public void setFullName( String name )
    {
        fullName = name;
    }

    public void setLastLoginDate( Date date )
    {
        lastLoginDate = date;
    }

    public void setLastPasswordChange( Date passwordChangeDate )
    {
        lastPasswordChange = passwordChangeDate;
    }

    public void setLocked( boolean locked )
    {
        this.locked = locked;
    }

    public void setPassword( String rawPassword )
    {
        newPassword = rawPassword;
    }

    public void setPasswordChangeRequired( boolean changeRequired )
    {
        requiresPasswordChange = changeRequired;
    }

    public void setPermanent( boolean permanent )
    {
        this.permanent = permanent;
    }

    @SuppressWarnings("unchecked")
    public void setPreviousEncodedPasswords( List encodedPasswordList )
    {
        previousEncodedPasswords = new ArrayList<String>( encodedPasswordList );
    }

    public void setUsername( String name )
    {
        username = name;
    }

    public void setValidated( boolean valid )
    {
        this.valid = valid;
    }

    public Attributes getOriginalAttributes()
    {
        return originalAttributes;
    }

    public void setOriginalAttributes( Attributes originalAttributes )
    {
        this.originalAttributes = originalAttributes;
    }

}
