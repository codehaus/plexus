/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.user;

import org.codehaus.plexus.ircd.exception.FatalException;
import org.codehaus.plexus.ircd.exception.IRCException;
import org.codehaus.plexus.ircd.token.ModeToken;
import org.codehaus.plexus.ircd.utils.IRCString;
import org.codehaus.plexus.ircd.utils.Replies;

import java.util.Enumeration;
import java.util.Hashtable;

public class HandleUser implements IRCString
{

    private static Hashtable _hAllUsers = new Hashtable();

    /**
     * to set the away state of a specific user
     * @param sNickName the nickname of the user
     * @param sMessage the away message to set
     * @throws IRCException ERR_NOSUCHNICK if the user does not exist
     * ERR_NONICKNAMEGIVEN if the nickname is empty
     */
    public static void setAwayState( String sNickName, String sMessage ) throws IRCException
    {
        User user = getUser( sNickName );
        user.setAwayState( sMessage );
        try
        {
            udateUser( user );
        }
        catch ( FatalException e )
        {
        }
    }

    /**
     * to change a single mode of a specific user
     * @param bIsAddMode to tell if the mode is added
     * @param mode the mode to set
     * @param user the corresponding user
     * @throws IRCException ERR_UNKNOWNMODE if the mode is unknown
     */
    private static void changeMode( boolean bIsAddMode, char mode, User user ) throws IRCException
    {
        if ( mode != NULL_CHAR && user != null )
        {
            switch ( mode )
            {
                case USER_OPERATOR_CHAR:
                    {
                        if ( !bIsAddMode )
                        {
                            user.setOperator( false );
                        }
                        break;
                    }
                case USER_INVISIBLE_CHAR:
                    {
                        user.setInvisible( bIsAddMode );
                        break;
                    }
                default :
                    {
                        throw new IRCException( Replies.ERR_UNKNOWNMODE, new String[]{Character.toString( mode )} );
                    }
            }
            try
            {
                udateUser( user );
            }
            catch ( FatalException e )
            {
            }
        }
    }

    /**
     * to change serveral mode of a specific user
     * @param userFrom the user who performs the modifications
     * @param sNickName the nickname of the user who is changing his modes
     * @param modeToken the modes to set
     * @throws IRCException ERR_UNKNOWNMODE if the mode is unknown
     */
    public static void changeMode( User userFrom, String sNickName, ModeToken modeToken ) throws IRCException
    {
        User user = getUser( sNickName );
        if ( userFrom != null && modeToken != null && userFrom.isMatching( sNickName ) )
        {
            changeMode( modeToken.getAddMode(), modeToken.getFirstMode(), user );
        }
    }

    /**
     * to delete a user from the list
     * @param sLogin the login of the user to remove
     */
    public synchronized static void deleteUser( String sLogin )
    {
        if ( sLogin != null )
        {
            _hAllUsers.remove( sLogin );
        }
    }

    /**
     * to disconnect all the users connected to the server
     */
    public synchronized static void disconnectAll()
    {
        for ( Enumeration enum = _hAllUsers.elements(); enum.hasMoreElements(); )
        {
            final User user = (User) enum.nextElement();
            if ( user != null )
            {
                Thread t = new Thread( new Runnable()
                {
                    public void run()
                    {
                        try
                        {
                            user.disconnectUser();
                        }
                        catch ( Exception e )
                        {
                        }
                    }
                } );
                t.start();
            }
        }
    }

    /**
     * to get a user from his nickname
     * @param sLogin the nickname of the user to find
     * @return the requested user
     * @throws IRCException ERR_NOSUCHNICK if the user does not exist
     */
    public synchronized static User getUser( String sLogin ) throws IRCException
    {
        if ( sLogin != null )
        {
            User user = (User) _hAllUsers.get( sLogin );
            if ( user != null )
            {
                return user;
            }
        }
        throw new IRCException( Replies.ERR_NOSUCHNICK, new String[]{sLogin} );
    }

    /**
     * to know if a specific user exists
     * @param sLogin the login of the user to check
     * @return true if the user exists false otherwise
     */
    public static boolean isUserExist( String sLogin )
    {
        if ( sLogin != null )
        {
            return _hAllUsers.containsKey( sLogin );
        }
        else
        {
            return false;
        }
    }

    /**
     * to remove the away state of a specific user
     * @param sNickName the corresponding user
     * @throws IRCException ERR_NOSUCHNICK if the user does not exist
     * ERR_NONICKNAMEGIVEN if the nickname is empty
     */
    public static void removeAwayState( String sNickName ) throws IRCException
    {
        User user = getUser( sNickName );
        user.removeAwayState();
        try
        {
            udateUser( user );
        }
        catch ( FatalException e )
        {
        }
    }

    /**
     * to rename a user
     * @param sNewLogin the new login
     * @param sOldLogin the old login
     * @throws IRCException ERR_NOSUCHNICK if the user does not exist
     * ERR_NONICKNAMEGIVEN if the nickname is empty
     * ERR_NICKNAMEINUSE if the nickname is already in use
     */
    public synchronized static void renameUser( String sNewLogin, String sOldLogin ) throws IRCException
    {
        User user = getUser( sOldLogin );
        user.setNickName( sNewLogin );
        try
        {
            udateUser( sNewLogin, user );
            deleteUser( sOldLogin );
            user.rename( sOldLogin, sNewLogin );
        }
        catch ( FatalException e )
        {
        }
    }

    /**
     * to update the datas of a user
     * @param sLogin the login of the user
     * @param user the corresponding User object
     * @throws IRCException ERR_NONICKNAMEGIVEN if the nickname is empty
     * ERR_NICKNAMEINUSE if the nickname is already in use
     * @throws FatalException if the user is null
     */
    private synchronized static void udateUser( String sLogin, User user ) throws IRCException, FatalException
    {
        if ( user == null )
        {
            throw new FatalException( "The user object is null !!" );
        }
        else
        {
            if ( sLogin == null || sLogin.equals( VOID ) )
            {
                throw new IRCException( Replies.ERR_NONICKNAMEGIVEN );
            }
            else
            {
                if ( isUserExist( sLogin ) )
                {
                    throw new IRCException( Replies.ERR_NICKNAMEINUSE, new String[]{sLogin} );
                }
                else
                {
                    _hAllUsers.put( sLogin, user );
                }
            }
        }
    }

    /**
     * to update the datas of a user
     * @param user the user to update
     * @throws IRCException ERR_NONICKNAMEGIVEN if the nickname is empty
     * ERR_NICKNAMEINUSE if the nickname is already in use
     * @throws FatalException if the user is null
     */
    public synchronized static void udateUser( User user ) throws IRCException, FatalException
    {
        if ( user != null )
        {
            udateUser( user.getNickName(), user );
        }
    }
}

