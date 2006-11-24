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
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.security.policy.UserSecurityPolicy;
import org.codehaus.plexus.security.user.AbstractUserManager;
import org.codehaus.plexus.security.user.PermanentUserException;
import org.codehaus.plexus.security.user.User;
import org.codehaus.plexus.security.user.UserManagerException;
import org.codehaus.plexus.security.user.UserNotFoundException;
import org.codehaus.plexus.util.StringUtils;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.util.List;

/**
 * JdoUserManager
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @plexus.component role="org.codehaus.plexus.security.user.UserManager"
 * role-hint="jdo"
 */
public class JdoUserManager
    extends AbstractUserManager
    implements Initializable
{
    /**
     * @plexus.requirement role-hint="users"
     */
    private JdoFactory jdoFactory;

    /**
     * @plexus.requirement
     */
    private UserSecurityPolicy userSecurityPolicy;

    private PersistenceManagerFactory pmf;

    public String getId()
    {
        return "JDO UserManager - " + this.getClass().getName();
    }

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

    public List getUsers( boolean orderAscending )
    {
        String ordering = orderAscending ? "username ascending" : "username descending";

        return getAllObjectsDetached( JdoUser.class, ordering, null );
    }

    public List findUsersByUsernameKey( String usernameKey, boolean orderAscending )
    {
        return findUsers( "username", usernameKey, orderAscending );
    }

    public List findUsersByFullNameKey( String fullNameKey, boolean orderAscending )
    {
        return findUsers( "fullName", fullNameKey, orderAscending );
    }

    public List findUsersByEmailKey( String emailKey, boolean orderAscending )
    {
        return findUsers( "email", emailKey, orderAscending );
    }


    private List findUsers( String searchField, String searchKey, boolean ascendingUsername )
    {
        PersistenceManager pm = getPersistenceManager();

        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            Extent extent = pm.getExtent( JdoUser.class, true );

            Query query = pm.newQuery( extent );

            String ordering = ascendingUsername ? "username ascending" : "username descending";

            query.setOrdering( ordering );

            query.declareImports( "import java.lang.String" );

            query.declareParameters( "String searchKey" );

            query.setFilter( "this." + searchField + ".toLowerCase().indexOf(searchKey.toLowerCase()) > -1" );

            List result = (List) query.execute( searchKey );

            result = (List) pm.detachCopyAll( result );

            tx.commit();

            return result;
        }
        finally
        {
            rollback( tx );
        }
    }

    public User addUser( User user )
    {
        if ( !( user instanceof JdoUser ) )
        {
            throw new UserManagerException( "Unable to Add User. User object " + user.getClass().getName() +
                " is not an instance of " + JdoUser.class.getName() );
        }

        if ( StringUtils.isEmpty( user.getUsername() ) )
        {
            throw new IllegalStateException(
                Messages.getString( "user.manager.cannot.add.user.without.username" ) ); //$NON-NLS-1$
        }

        userSecurityPolicy.extensionChangePassword( user );

        fireUserManagerUserAdded( user );

        return (User) addObject( user );
    }

    public void deleteUser( Object principal )
    {
        try
        {
            User user = findUser( principal );

            if ( user.isPermanent() )
            {
                throw new PermanentUserException( "Cannot delete permanent user [" + user.getUsername() + "]." );
            }

            fireUserManagerUserRemoved( user );

            removeObject( (JdoUser) user );
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

            if ( user.isPermanent() )
            {
                throw new PermanentUserException( "Cannot delete permanent user [" + user.getUsername() + "]." );
            }

            fireUserManagerUserRemoved( user );

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
        if ( principal == null )
        {
            throw new UserNotFoundException( "Unable to find user based on null principal." );
        }

        try
        {
            return (User) PlexusJdoUtils.getObjectById( getPersistenceManager(), JdoUser.class, principal.toString(),
                                                        null );
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
        if ( StringUtils.isEmpty( username ) )
        {
            throw new UserNotFoundException( "User with empty username not found." );
        }

        return (User) getObjectById( JdoUser.class, username );
    }


    public boolean userExists( Object principal )
    {
        try
        {
            findUser( principal );
            return true;
        }
        catch ( UserNotFoundException ne )
        {
            return false;
        }
    }

    public User updateUser( User user )
        throws UserNotFoundException
    {
        if ( !( user instanceof JdoUser ) )
        {
            throw new UserManagerException( "Unable to Update User. User object " + user.getClass().getName() +
                " is not an instance of " + JdoUser.class.getName() );
        }

        // If password is supplied, assume changing of password.
        // TODO: Consider adding a boolean to the updateUser indicating a password change or not.
        if ( StringUtils.isNotEmpty( user.getPassword() ) )
        {
            userSecurityPolicy.extensionChangePassword( user );
        }

        updateObject( (JdoUser) user );

        fireUserManagerUserUpdated( user );

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

        triggerInit();

        return pm;
    }

    // ----------------------------------------------------------------------
    // jdo utility methods
    // ----------------------------------------------------------------------

    private Object addObject( Object object )
    {
        return PlexusJdoUtils.addObject( getPersistenceManager(), object );
    }

    private Object getObjectById( Class clazz, String id )
        throws UserNotFoundException, UserManagerException
    {
        return getObjectById( clazz, id, null );
    }

    private Object getObjectById( Class clazz, String id, String fetchGroup )
        throws UserNotFoundException, UserManagerException
    {
        try
        {
            return PlexusJdoUtils.getObjectById( getPersistenceManager(), clazz, id, fetchGroup );
        }
        catch ( PlexusObjectNotFoundException e )
        {
            throw new UserNotFoundException( e.getMessage() );
        }
        catch ( PlexusStoreException e )
        {
            throw new UserManagerException( "Unable to get object '" + clazz.getName() + "', id '" + id +
                "', fetch-group '" + fetchGroup + "' from jdo store." );
        }
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

    private Object removeObject( Object o )
    {
        if ( o == null )
        {
            throw new UserManagerException( "Unable to remove null object '" + o.getClass().getName() + "'" );
        }

        PlexusJdoUtils.removeObject( getPersistenceManager(), o );
        return o;
    }

    private Object updateObject( Object object )
        throws UserNotFoundException, UserManagerException
    {
        try
        {
            return PlexusJdoUtils.updateObject( getPersistenceManager(), object );
        }
        catch ( PlexusStoreException e )
        {
            throw new UserManagerException(
                "Unable to update the '" + object.getClass().getName() + "' object in the jdo database.", e );
        }
    }

    private void rollback( Transaction tx )
    {
        PlexusJdoUtils.rollbackIfActive( tx );
    }

    private boolean hasTriggeredInit = false;

    public void triggerInit()
    {
        if ( !hasTriggeredInit )
        {
            hasTriggeredInit = true;

            List users = getAllObjectsDetached( JdoUser.class );

            fireUserManagerInit( users.isEmpty() );
        }
    }
}
