package org.codehaus.plexus.security.keys.memory;

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

import org.codehaus.plexus.security.keys.AbstractKeyManager;
import org.codehaus.plexus.security.keys.AuthenticationKey;
import org.codehaus.plexus.security.keys.KeyManagerException;
import org.codehaus.plexus.security.keys.KeyNotFoundException;
import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * KeyManager backed by an in-memory only store.
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="org.codehaus.plexus.security.keys.KeyManager"
 * role-hint="memory"
 */
public class MemoryKeyManager
    extends AbstractKeyManager
{
    private Map keys = new HashMap();

    public AuthenticationKey createKey( String principal, String purpose, int expirationMinutes )
        throws KeyManagerException
    {
        AuthenticationKey key = new MemoryAuthenticationKey();
        key.setKey( super.generateUUID() );
        key.setForPrincipal( principal );
        key.setPurpose( purpose );
        key.setDateCreated( new Date() );

        if ( expirationMinutes >= 0 )
        {
            Calendar expiration = Calendar.getInstance();
            expiration.add( Calendar.MINUTE, expirationMinutes );
            key.setDateExpires( expiration.getTime() );
        }

        keys.put( key.getKey(), key );

        return key;
    }

    public AuthenticationKey findKey( String key )
        throws KeyNotFoundException, KeyManagerException
    {
        if ( StringUtils.isEmpty( key ) )
        {
            throw new KeyNotFoundException( "Empty key not found." );
        }

        AuthenticationKey authkey = (AuthenticationKey) keys.get( key );

        if ( authkey == null )
        {
            throw new KeyNotFoundException( "Key [" + key + "] not found." );
        }

        assertNotExpired( authkey );

        return authkey;
    }

    public void deleteKey( AuthenticationKey authkey )
        throws KeyManagerException
    {
        keys.remove( authkey );
    }

    public void deleteKey( String key )
        throws KeyManagerException
    {
        AuthenticationKey authkey = (AuthenticationKey) keys.get( key );
        if ( authkey != null )
        {
            keys.remove( authkey );
        }
    }

    public List getAllKeys()
    {
        return new ArrayList( keys.values() );
    }

    public String getId()
    {
        return "Memory Key Manager";
    }
}
