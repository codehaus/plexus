package org.codehaus.plexus.security.keys.jdo;

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

import org.codehaus.plexus.jdo.JdoFactory;
import org.codehaus.plexus.jdo.PlexusJdoUtils;
import org.codehaus.plexus.jdo.PlexusObjectNotFoundException;
import org.codehaus.plexus.jdo.PlexusStoreException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.security.keys.AbstractKeyManager;
import org.codehaus.plexus.security.keys.AuthenticationKey;
import org.codehaus.plexus.security.keys.KeyManagerException;
import org.codehaus.plexus.security.keys.KeyNotFoundException;
import org.codehaus.plexus.util.StringUtils;
import org.jpox.PersistenceManagerFactoryImpl;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import java.util.Calendar;
import java.util.List;

/**
 * JdoKeyManager
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="org.codehaus.plexus.security.keys.KeyManager"
 * role-hint="jdo"
 */
public class JdoKeyManager
    extends AbstractKeyManager
    implements Initializable
{
    /**
     * @plexus.requirement role-hint="users"
     */
    private JdoFactory jdoFactory;

    private PersistenceManagerFactory pmf;

    public AuthenticationKey createKey( String principal, String purpose, int expirationMinutes )
        throws KeyManagerException
    {
        JdoAuthenticationKey authkey = new JdoAuthenticationKey();
        authkey.setKey( super.generateUUID() );
        authkey.setForPrincipal( principal );
        authkey.setPurpose( purpose );

        Calendar now = getNowGMT();
        authkey.setDateCreated( now.getTime() );

        if ( expirationMinutes >= 0 )
        {
            Calendar expiration = getNowGMT();
            expiration.add( Calendar.MINUTE, expirationMinutes );
            authkey.setDateExpires( expiration.getTime() );
        }

        return addKey( authkey );
    }

    public AuthenticationKey addKey( AuthenticationKey key )
    {
        return (AuthenticationKey) PlexusJdoUtils.addObject( getPersistenceManager(), key );
    }

    public void eraseDatabase()
    {
        PlexusJdoUtils.removeAll( getPersistenceManager(), JdoAuthenticationKey.class );
        PlexusJdoUtils.removeAll( getPersistenceManager(), PlexusSecurityKeyManagementJdoModelloMetadata.class );
    }

    public AuthenticationKey findKey( String key )
        throws KeyNotFoundException, KeyManagerException
    {
        if ( StringUtils.isEmpty( key ) )
        {
            throw new KeyNotFoundException( "Empty key not found." );
        }

        try
        {
            JdoAuthenticationKey authkey = (JdoAuthenticationKey) PlexusJdoUtils
                .getObjectById( getPersistenceManager(), JdoAuthenticationKey.class, key );

            if ( authkey == null )
            {
                throw new KeyNotFoundException( "Key [" + key + "] not found." );
            }
            assertNotExpired( authkey );

            return authkey;
        }
        catch ( PlexusObjectNotFoundException e )
        {
            throw new KeyNotFoundException( e.getMessage() );
        }
        catch ( PlexusStoreException e )
        {
            throw new KeyManagerException(
                "Unable to get " + JdoAuthenticationKey.class.getName() + "', key '" + key + "' from jdo store." );
        }
    }

    public void deleteKey( AuthenticationKey authkey )
        throws KeyManagerException
    {
        PlexusJdoUtils.removeObject( getPersistenceManager(), authkey );
    }

    public void deleteKey( String key )
        throws KeyManagerException
    {
        try
        {
            AuthenticationKey authkey = findKey( key );
            PlexusJdoUtils.removeObject( getPersistenceManager(), authkey );
        }
        catch ( KeyNotFoundException e )
        {
            // not found? nothing to do.
        }
    }

    public List getAllKeys()
    {
        return PlexusJdoUtils.getAllObjectsDetached( getPersistenceManager(), JdoAuthenticationKey.class, null, null );
    }

    public void initialize()
        throws InitializationException
    {
        pmf = jdoFactory.getPersistenceManagerFactory();

        if ( pmf instanceof PersistenceManagerFactoryImpl )
        {
            PersistenceManagerFactoryImpl jpoxpmf = (PersistenceManagerFactoryImpl) pmf;
            if ( !StringUtils.equals( "JDK_DEFAULT_TIMEZONE", jpoxpmf.getDateTimezone() ) )
            {
                throw new InitializationException( "The JdoFactory property 'org.jpox.rdbms.dateTimezone' MUST BE " +
                    "Set to 'JDK_DEFAULT_TIMEZONE' in order for jpox and JdoKeyManager to operate correctly." );
            }
        }
    }

    private PersistenceManager getPersistenceManager()
    {
        PersistenceManager pm = pmf.getPersistenceManager();

        pm.getFetchPlan().setMaxFetchDepth( 5 );

        return pm;
    }

    public String getId()
    {
        return "JDO Key Manager - " + this.getClass().getName();
    }

}
