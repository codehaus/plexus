package org.codehaus.plexus.redback.users.ldap;
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



import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.DirContext;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.redback.common.ldap.MappingException;
import org.codehaus.plexus.redback.common.ldap.UserMapper;
import org.codehaus.plexus.redback.common.ldap.connection.LdapConnection;
import org.codehaus.plexus.redback.common.ldap.connection.LdapConnectionFactory;
import org.codehaus.plexus.redback.common.ldap.connection.LdapException;
import org.codehaus.plexus.redback.users.User;
import org.codehaus.plexus.redback.users.UserManager;
import org.codehaus.plexus.redback.users.UserManagerListener;
import org.codehaus.plexus.redback.users.UserNotFoundException;
import org.codehaus.plexus.redback.users.UserQuery;
import org.codehaus.plexus.redback.users.ldap.ctl.LdapController;
import org.codehaus.plexus.redback.users.ldap.ctl.LdapControllerException;

/**
 * 
 * @author <a href="jesse@codehaus.org"> jesse
 * @version "$Id$"
 *
 * @plexus.component role="org.codehaus.plexus.redback.users.UserManager" role-hint="ldap"
 */
public class LdapUserManager
    implements UserManager, LogEnabled
{
	
	/**
	 * @plexus.requirement role-hint="configurable"
	 */
	private LdapConnectionFactory connectionFactory;
	

    private List<UserManagerListener> listeners = new ArrayList<UserManagerListener>();

    private Logger logger;

    /**
	 * @plexus.requirement role-hint="default"
	 */
    private LdapController controller;

    /**
	 * @plexus.requirement role-hint="ldap"
	 */
    private UserMapper mapper;

    public boolean isReadOnly()
    {
        return true;
    }
    
    public void addUserManagerListener( UserManagerListener listener )
    {
        if ( !listeners.contains( listener ) )
        {
            listeners.add( listener );
        }
    }

    public void removeUserManagerListener( UserManagerListener listener )
    {
        listeners.remove( listener );
    }

    protected void fireUserAdded( User addedUser )
    {
        for ( UserManagerListener listener : listeners )
        {
            try
            {
                listener.userManagerUserAdded( addedUser );
            }
            catch ( Exception e )
            {
                getLogger().debug( "Failed to fire user-added event to user-manager: " + e.getMessage(), e );
            }
        }
    }

    protected void fireUserRemoved( User removedUser )
    {
        for ( UserManagerListener listener : listeners )
        {
            try
            {
                listener.userManagerUserRemoved( removedUser );
            }
            catch ( Exception e )
            {
                getLogger().debug( "Failed to fire user-removed event to user-manager: " + e.getMessage(), e );
            }
        }
    }

    protected void fireUserUpdated( User updatedUser )
    {
        for ( UserManagerListener listener : listeners )
        {
            try
            {
                listener.userManagerUserUpdated( updatedUser );
            }
            catch ( Exception e )
            {
                getLogger().debug( "Failed to fire user-updated event to user-manager: " + e.getMessage(), e );
            }
        }
    }

    public User addUser( User user )
    {
        DirContext context = newDirContext();
        try
        {
            controller.createUser( user, context, true );
        }
        catch ( LdapControllerException e )
        {
            getLogger().error( "Error mapping user: " + user.getPrincipal() + " to LDAP attributes.", e );
        }
        catch ( MappingException e )
        {
            getLogger().error( "Error mapping user: " + user.getPrincipal() + " to LDAP attributes.", e );
        }

        return user;
    }

    public void addUserUnchecked( User user )
    {
        DirContext context = newDirContext();
        try
        {
            controller.createUser( user, context, false );
        }
        catch ( LdapControllerException e )
        {
            getLogger().error( "Error mapping user: " + user.getPrincipal() + " to LDAP attributes.", e );
        }
        catch ( MappingException e )
        {
            getLogger().error( "Error mapping user: " + user.getPrincipal() + " to LDAP attributes.", e );
        }
    }

    public User createUser( String username, String fullName, String emailAddress )
    {
        return mapper.newUserInstance( username, fullName, emailAddress );
    }

    public UserQuery createUserQuery()
    {
        // TODO Implement queries!
        return null;
    }

    public void deleteUser( Object principal )
        throws UserNotFoundException
    {
        try
        {
            controller.removeUser( principal, newDirContext() );
        }
        catch ( LdapControllerException e )
        {
            getLogger().error( "Failed to delete user: " + principal, e );
        }
    }

    public void deleteUser( String username )
        throws UserNotFoundException
    {
        try
        {
            controller.removeUser( username, newDirContext() );
        }
        catch ( LdapControllerException e )
        {
            getLogger().error( "Failed to delete user: " + username, e );
        }
    }

    public void eraseDatabase()
    {
        // TODO Implement erase!
    }

    public User findUser( String username ) throws UserNotFoundException
    {
    	try
        {
            return controller.getUser( username, newDirContext() );
        }
        catch ( LdapControllerException e )
        {
            getLogger().error( "Failed to find user: " + username, e );
            return null;
        }
        catch ( MappingException e )
        {
            getLogger().error( "Failed to map user: " + username, e );
            return null;
        }
    }

    public User findUser( Object principal )
        throws UserNotFoundException
    {
        try
        {
            return controller.getUser( principal, newDirContext() );
        }
        catch ( LdapControllerException e )
        {
            getLogger().error( "Failed to find user: " + principal, e );
            return null;
        }
        catch ( MappingException e )
        {
            getLogger().error( "Failed to map user: " + principal, e );
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List findUsersByEmailKey( String emailKey, boolean orderAscending )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("unchecked")
    public List findUsersByFullNameKey( String fullNameKey, boolean orderAscending )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("unchecked")
    public List findUsersByQuery( UserQuery query )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("unchecked")
    public List findUsersByUsernameKey( String usernameKey, boolean orderAscending )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getId()
    {
        return "LDAP User-Manager: " + getClass().getName();
    }

    public List getUsers()
    {
    	try 
    	{
    		List users = new ArrayList();
    		users.addAll( controller.getUsers( newDirContext() ) );
    		return users;
    	}
    	catch ( Exception e )
    	{
    		e.printStackTrace();
    	}
    	
        return null;
    }

    public List getUsers( boolean orderAscending )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public User updateUser( User user )
        throws UserNotFoundException
    {
        try
        {
            controller.updateUser( user, newDirContext() );
        }
        catch ( LdapControllerException e )
        {
            getLogger().error( "Failed to update user: " + user.getPrincipal(), e );
        }
        catch ( MappingException e )
        {
            getLogger().error( "Failed to update user: " + user.getPrincipal(), e );
        }

        return user;
    }

    public boolean userExists( Object principal )
    {
        DirContext context = newDirContext();
        try
        {
            return controller.userExists( principal, context );
        }
        catch ( LdapControllerException e )
        {
            getLogger().debug( "Failed to search for user: " + principal, e );
            return false;
        }
    }

    private DirContext newDirContext()
    {
    	try
    	{
    		LdapConnection connection = connectionFactory.getConnection();
    		
    		return connection.getDirContext();
    	}
    	catch ( LdapException le )
    	{
    		le.printStackTrace();
    		return null;
    	}    	       
    }

    protected Logger getLogger()
    {
        if ( logger == null )
        {
            logger = new ConsoleLogger( Logger.LEVEL_DEBUG, "internally-initialized" );
        }

        return logger;
    }


    public void enableLogging( Logger logger )
    {
        this.logger = logger;
    }

	public LdapConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public void setConnectionFactory(LdapConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
    
}
