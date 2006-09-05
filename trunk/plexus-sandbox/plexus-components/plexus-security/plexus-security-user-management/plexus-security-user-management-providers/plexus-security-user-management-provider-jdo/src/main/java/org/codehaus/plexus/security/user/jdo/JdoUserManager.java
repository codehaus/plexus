package org.codehaus.plexus.security.user.jdo;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManager;
import org.codehaus.plexus.security.user.UserNotFoundException;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

/**
 * JdoUserManager 
 * 
 * @plexus.component role="org.codehaus.plexus.security.user.UserManager"
 *                   role-hint="jdo"
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class JdoUserManager
    extends AbstractLogEnabled
    implements UserManager, Initializable
{
    /**
     * @plexus.requirement
     */
    private JdoFactory jdoFactory;

    private PersistenceManagerFactory pmf;

    public void initialize()
        throws InitializationException
    {
        pmf = jdoFactory.getPersistenceManagerFactory();
    }

    private PersistenceManager getPersistenceManager()
    {
        PersistenceManager pm = pmf.getPersistenceManager();

        pm.getFetchPlan().setMaxFetchDepth( -1 );

        return pm;
    }

    public User addUser( User user )
    {
        return (User) PlexusJdoUtils.addObject( getPersistenceManager(), user );
    }

    public void deleteUser( Object principal )
    {
        try
        {
            User user = findUser( principal );

            PlexusJdoUtils.removeObject( getPersistenceManager(), user );
        }
        catch ( UserNotFoundException e )
        {
            getLogger().warn( "Unable to delete user " + principal + ", user not found.", e );
        }
    }

    public User findUser( Object principal )
        throws UserNotFoundException
    {
        User user = null;

        if ( !( principal instanceof Integer ) )
        {
            throw new UserNotFoundException( "Unable to find user based on non-Integer principal." );
        }

        int accountId = ( (Integer) principal ).intValue();
        try
        {
            user = (User) PlexusJdoUtils.getObjectById( getPersistenceManager(), User.class, accountId, null );
        }
        catch ( PlexusObjectNotFoundException e )
        {
            throw new UserNotFoundException( "Unable to find user: " + e.getMessage(), e );
        }
        catch ( PlexusStoreException e )
        {
            throw new UserNotFoundException( "Unable to find user: " + e.getMessage(), e );
        }

        return user;
    }

    public User updateUser( User user )
    {
        try
        {
            PlexusJdoUtils.updateObject( getPersistenceManager(), user );
        }
        catch ( PlexusStoreException e )
        {
            throw new RuntimeException( "Unable to update user.", e );
        }

        return user;
    }
}
