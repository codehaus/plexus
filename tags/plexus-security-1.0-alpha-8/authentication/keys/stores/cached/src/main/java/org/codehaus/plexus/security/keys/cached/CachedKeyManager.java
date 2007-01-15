package org.codehaus.plexus.security.keys.cached;

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

import net.sf.ehcache.Element;

import org.codehaus.plexus.ehcache.EhcacheComponent;
import org.codehaus.plexus.ehcache.EhcacheUtils;
import org.codehaus.plexus.security.keys.AbstractKeyManager;
import org.codehaus.plexus.security.keys.AuthenticationKey;
import org.codehaus.plexus.security.keys.KeyManager;
import org.codehaus.plexus.security.keys.KeyManagerException;
import org.codehaus.plexus.security.keys.KeyNotFoundException;

import java.util.List;

/**
 * CachedKeyManager 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="org.codehaus.plexus.security.keys.KeyManager" role-hint="cached"
 */
public class CachedKeyManager
    extends AbstractKeyManager
    implements KeyManager
{
    /**
     * @plexus.requirement
     */
    private KeyManager keyImpl;

    /**
     * @plexus.requirement role-hint="keys"
     */
    private EhcacheComponent keysCache;

    public AuthenticationKey addKey( AuthenticationKey key )
    {
        if ( key != null )
        {
            keysCache.invalidateKey( key.getKey() );
        }
        return this.keyImpl.addKey( key );
    }

    public AuthenticationKey createKey( String principal, String purpose, int expirationMinutes )
        throws KeyManagerException
    {
        AuthenticationKey authkey = this.keyImpl.createKey( principal, purpose, expirationMinutes );
        keysCache.invalidateKey( authkey.getKey() );
        return authkey;
    }

    public void deleteKey( AuthenticationKey key )
        throws KeyManagerException
    {
        keysCache.invalidateKey( key.getKey() );
        this.keyImpl.deleteKey( key );
    }

    public void deleteKey( String key )
        throws KeyManagerException
    {
        keysCache.invalidateKey( key );
        this.keyImpl.deleteKey( key );
    }

    public void eraseDatabase()
    {
        try
        {
            this.keyImpl.eraseDatabase();
        }
        finally
        {
            EhcacheUtils.clearAllCaches( getLogger() );
        }
    }

    public AuthenticationKey findKey( String key )
        throws KeyNotFoundException, KeyManagerException
    {
        try
        {
            Element el = keysCache.getElement( key );
            if ( el != null )
            {
                AuthenticationKey authkey = (AuthenticationKey) el.getObjectValue();
                assertNotExpired( authkey );
                return authkey;
            }
            else
            {
                AuthenticationKey authkey = this.keyImpl.findKey( key );
                keysCache.putElement( new Element( key, authkey ) );
                return authkey;
            }
        }
        catch ( KeyNotFoundException knfe )
        {
            // this is done to remove keys that have been expired.
            // TODO: need to make a listener for the key manager.
            keysCache.invalidateKey( key );
            throw knfe;
        }
    }

    public List getAllKeys()
    {
        getLogger().debug( "NOT CACHED - .getAllKeys()" );
        return this.keyImpl.getAllKeys();
    }

    public String getId()
    {
        return "Cached Key Manager [" + this.keyImpl.getId() + "]";
    }

}
