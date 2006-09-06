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
import org.codehaus.plexus.security.user.UserManagerException;
import org.codehaus.plexus.security.user.UserNotFoundException;

import java.util.Collection;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

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

    // ------------------------------------------------------------------

    public User createUser( String username, String fullname, String email )
    {
        User user = new JdoUser();
        user.setUsername( username );
        user.setFullName( fullname );
        user.setEmail( email );

        return user;
    }

    public List getUsers()
    {
        return getAllObjectsDetached( JdoUser.class );
    }

    public User addUser( User user )
    {
        if ( !( user instanceof JdoUser ) )
        {
            throw new UserManagerException( "Unable to Add User. User object " + user.getClass().getName()
                + " is not an instance of " + JdoUser.class.getName() );
        }
        return (User) PlexusJdoUtils.addObject( getPersistenceManager(), user );
    }

    public void deleteUser( Object principal )
    {
        try
        {
            User user = findUser( principal );

            PlexusJdoUtils.removeObject( getPersistenceManager(), (JdoUser) user );
        }
        catch ( UserNotFoundException e )
        {
            getLogger().warn( "Unable to delete user " + principal + ", user not found.", e );
        }
    }

    public void deleteUser( String username )
    {
        try
        {
            User user = findUser( username );

            PlexusJdoUtils.removeObject( getPersistenceManager(), (JdoUser) user );
        }
        catch ( UserNotFoundException e )
        {
            getLogger().warn( "Unable to delete user " + username + ", user not found.", e );
        }
    }

    public User findUser( Object principal )
        throws UserNotFoundException
    {
        if ( !( principal instanceof Integer ) )
        {
            throw new UserManagerException( "Unable to find user based on non-Integer principal." );
        }

        int accountId = ( (Integer) principal ).intValue();
        try
        {
            return (User) PlexusJdoUtils.getObjectById( getPersistenceManager(), JdoUser.class, accountId, null );
        }
        catch ( PlexusObjectNotFoundException e )
        {
            throw new UserNotFoundException( "Unable to find user: " + e.getMessage(), e );
        }
        catch ( PlexusStoreException e )
        {
            throw new UserNotFoundException( "Unable to find user: " + e.getMessage(), e );
        }
    }

    public User findUser( String username )
        throws UserNotFoundException
    {
        PersistenceManager pm = getPersistenceManager();

        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            Extent extent = pm.getExtent( JdoUser.class, true );

            Query query = pm.newQuery( extent );

            query.declareImports( "import java.lang.String" ); //$NON-NLS-1$

            query.declareParameters( "String username" ); //$NON-NLS-1$

            query.setFilter( "this.username == username" ); //$NON-NLS-1$

            Collection result = (Collection) query.execute( username );

            if ( result.size() == 0 )
            {
                tx.commit();

                throw new UserNotFoundException( "User (username:" + username + ") not found." );
            }

            Object object = pm.detachCopy( result.iterator().next() );

            tx.commit();

            return (User) object;
        }
        finally
        {
            rollback( tx );
        }
    }

    public User updateUser( User user )
    {
        if ( !( user instanceof JdoUser ) )
        {
            throw new UserManagerException( "Unable to Update User. User object " + user.getClass().getName()
                + " is not an instance of " + JdoUser.class.getName() );
        }

        try
        {
            PlexusJdoUtils.updateObject( getPersistenceManager(), (JdoUser) user );
        }
        catch ( PlexusStoreException e )
        {
            throw new UserManagerException( "Unable to update user.", e );
        }

        return user;
    }

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

    private List getAllObjectsDetached( Class clazz )
    {
        return getAllObjectsDetached( clazz, null );
    }

    private List getAllObjectsDetached( Class clazz, String fetchGroup )
    {
        return getAllObjectsDetached( clazz, null, fetchGroup );
    }

    private List getAllObjectsDetached( Class clazz, String ordering, String fetchGroup )
    {
        return PlexusJdoUtils.getAllObjectsDetached( getPersistenceManager(), clazz, ordering, fetchGroup );
    }

    private void rollback( Transaction tx )
    {
        PlexusJdoUtils.rollbackIfActive( tx );
    }
}
