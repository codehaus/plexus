/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.channel;

import org.codehaus.plexus.ircd.command.Join;
import org.codehaus.plexus.ircd.command.Mode;
import org.codehaus.plexus.ircd.command.Nick;
import org.codehaus.plexus.ircd.exception.FatalException;
import org.codehaus.plexus.ircd.exception.IRCException;
import org.codehaus.plexus.ircd.token.MiddleToken;
import org.codehaus.plexus.ircd.token.ModeToken;
import org.codehaus.plexus.ircd.token.UserToken;
import org.codehaus.plexus.ircd.user.User;
import org.codehaus.plexus.ircd.utils.IRCString;
import org.codehaus.plexus.ircd.utils.Replies;

import java.util.Enumeration;
import java.util.Hashtable;

public class HandleChannel implements IRCString
{

    private static Hashtable _hAllChannels = new Hashtable();

    /**
     * to add a user on a specific channel. If the channel exists the user is just added else
     * the channel is created and the user is the operator
     * @param user the user to add
     * @param sNameChannel the name of the channel
     * @throws IRCException ERR_NONICKNAMEGIVEN the channel's name is empty
     * @throws FatalException if the channel did not be created
     */
    public synchronized static void addUser( User user, String sNameChannel ) throws IRCException, FatalException
    {
        if ( sNameChannel != null && user != null )
        {
            Channel channel = null;
            try
            {
                channel = getChannel( sNameChannel );
            }
            catch ( IRCException e )
            {
            }
            if ( channel == null )
            {
                channel = new Channel( user.getNickName(), sNameChannel );
            }
            else
            {
                channel.addUser( user.getNickName() );
            }
            user.addChannel( sNameChannel );
            udateChannel( channel );
            channel.sendMessage( user.getUserFullName(), new Join().getName(), VOID, sNameChannel );
        }
    }

    /**
     * to change a single mode of a specific channel
     * @param bIsAddMode to tell if the mode has to be added or not
     * @param mode the resuested mode
     * @param channel the channel which has to be changed
     * @param mParams the given parameters
     * @param index the index of the next parameter
     * @return the index of the next parameter
     * @throws IRCException ERR_NEEDMOREPARAMS if not enough parameter has been given
     * ERR_UNKNOWNMODE if the resuested mode is unkown
     */
    private static int changeMode( boolean bIsAddMode, char mode, Channel channel, MiddleToken[] mParams, int index ) throws IRCException
    {
        if ( mode != NULL_CHAR && channel != null )
        {
            switch ( mode )
            {
                case CHANNEL_OPERATOR_CHAR:
                    {
                        String value = getMiddleValue( mParams, index, bIsAddMode, mode );
                        UserToken userToken = (UserToken) UserToken.getToken( value );
                        channel.setIsOperator( userToken.getUserName(), bIsAddMode );
                        index++;
                        break;
                    }
                case CHANNEL_PRIVATE_CHAR:
                    {
                        channel.setTypePrivate( bIsAddMode );
                        if ( channel.isTypeSecret() )
                        {
                            channel.setTypeSecret( false );
                        }
                        break;
                    }
                case CHANNEL_SECRET_CHAR:
                    {
                        channel.setTypeSecret( bIsAddMode );
                        if ( channel.isTypePrivate() )
                        {
                            channel.setTypePrivate( false );
                        }
                        break;
                    }
                case CHANNEL_INVITE_ONLY_CHAR:
                    {
                        channel.setTypeIniviteOnly( bIsAddMode );
                        break;
                    }
                case CHANNEL_TOPIC_SETTABLE_CHAR:
                    {
                        channel.setTopicSettable( !bIsAddMode );
                        break;
                    }
                case CHANNEL_CLIENT_INSIDE_ONLY_CHAR:
                    {
                        channel.setTypeClientInsideOnly( bIsAddMode );
                        break;
                    }
                case CHANNEL_MODERATED_CHAR:
                    {
                        channel.setTypeModerated( bIsAddMode );
                        break;
                    }
                case CHANNEL_USER_LIMIT_CHAR:
                    {
                        if ( bIsAddMode )
                        {
                            String sParam = getMiddleValue( mParams, index, bIsAddMode, mode );
                            try
                            {
                                channel.setUserLimit( Integer.parseInt( sParam ) );
                                index++;
                            }
                            catch ( NumberFormatException e )
                            {
                            }
                        }
                        else
                        {
                            channel.removeUserLimit();
                        }
                        break;
                    }
                case CHANNEL_BAN_MASK_CHAR:
                    {
                        String sParam = getMiddleValue( mParams, index, bIsAddMode, mode );
                        if ( bIsAddMode )
                        {
                            channel.addBanMask( sParam );
                        }
                        else
                        {
                            channel.removeBanMask( sParam );
                        }
                        index++;
                        break;
                    }
                case CHANNEL_SPEAK_ON_MODERATED_CHAR:
                    {
                        String sParam = getMiddleValue( mParams, index, bIsAddMode, mode );
                        UserToken userToken = (UserToken) UserToken.getToken( sParam );
                        channel.setCanSpeakModeratedChannel( userToken.getUserName(), bIsAddMode );
                        index++;
                        break;
                    }
                case CHANNEL_PASSWORD_CHAR:
                    {
                        String sParam = getMiddleValue( mParams, index, bIsAddMode, mode );
                        if ( bIsAddMode )
                        {
                            channel.setPassword( sParam );
                        }
                        else
                        {
                            channel.removePassword( sParam );
                        }
                        index++;
                        break;
                    }
                default :
                    {
                        throw new IRCException( Replies.ERR_UNKNOWNMODE, new String[]{Character.toString( mode )} );
                    }
            }
            try
            {
                udateChannel( channel );
            }
            catch ( FatalException e )
            {
            }
        }
        return index;
    }

    /**
     * to change several modes of a specific channel
     * @param user the user who preforms modification
     * @param sChannelName the name of the channel to modify
     * @param modeToken the token corresponding to the requested modes
     * @param mParams the given parameters
     * @throws IRCException ERR_NEEDMOREPARAMS if not enough parameter has been given
     * ERR_UNKNOWNMODE if the resuested mode is unkown
     */
    public static void changeMode( User user, String sChannelName, ModeToken modeToken, MiddleToken[] mParams ) throws IRCException
    {
        Channel channel = getChannel( sChannelName );
        if ( channel != null && modeToken != null && channel.isOperator( user ) )
        {
            int index = 2;
            index = changeMode( modeToken.getAddMode(), modeToken.getFirstMode(), channel, mParams, index );
            index = changeMode( modeToken.getAddMode(), modeToken.getSecondMode(), channel, mParams, index );
            index = changeMode( modeToken.getAddMode(), modeToken.getThirdMode(), channel, mParams, index );
        }
    }

    /**
     * to delete a channel from the list
     * @param sName the name of the channel to delele
     */
    public synchronized static void deleteChannel( String sName )
    {
        if ( sName != null )
        {
            _hAllChannels.remove( sName );
        }
    }

    /**
     * to get the list of existing channel
     */
    public synchronized static Enumeration getAllChannels()
    {
        return _hAllChannels.elements();
    }

    /**
     * to get a channel from the list
     * @param sName the name of the channel asked
     * @return the channel if it exists
     * @throws IRCException ERR_NOSUCHCHANNEL if the channel does not exist
     */
    public synchronized static Channel getChannel( String sName ) throws IRCException
    {
        if ( sName != null )
        {
            Channel channel = (Channel) _hAllChannels.get( sName );
            if ( channel != null )
            {
                return channel;
            }
        }
        throw new IRCException( Replies.ERR_NOSUCHCHANNEL, new String[]{sName} );
    }

    /**
     * to get a specific parameter from the given index
     * @param mParams all the given parameters
     * @param index the index of the requested parameter
     * @param bIsAddMode to tell if the mode has to be added or not
     * @param mode the resuested mode
     * @return the requested parameter
     * @throws IRCException ERR_NEEDMOREPARAMS if not enough parameter has been given
     */
    private static String getMiddleValue( MiddleToken[] mParams, int index, boolean bIsAddMode, char mode ) throws IRCException
    {
        if ( mParams != null && mParams.length > index )
        {
            MiddleToken param = mParams[index];
            if ( param != null )
            {
                String middleValue = param.getMiddleValue();
                if ( middleValue != null && !middleValue.equals( VOID ) )
                {
                    return middleValue;
                }
            }
        }
        throw new IRCException( Replies.ERR_NEEDMOREPARAMS, new String[]{new Mode().getName(), Boolean.toString( bIsAddMode ), Character.toString( mode )} );
    }

    /**
     * to remove a user from a channel and if needed to send a message to the other members
     * of the channel
     * @param user the user to remove
     * @param sNameChannel the name of the channel
     * @param message tells if a message has to be send
     * @param sPrefix message's prefix
     * @param sCommand message's command
     * @param sMiddle message's middle
     * @param sMessage message's trailing
     * @throws IRCException ERR_NOSUCHCHANNEL if the channel does not exist
     * ERR_NOTEXTTOSEND if the message's trailing is empty
     * ERR_NONICKNAMEGIVEN if the user has no nickname
     * ERR_NOTONCHANNEL if the user is not on the channel
     */
    public synchronized static void removeUser( User user, String sNameChannel, boolean message, String sPrefix, String sCommand, String sMiddle, String sMessage ) throws IRCException
    {
        Channel channel = getChannel( sNameChannel );
        if ( channel != null && user != null )
        {
            if ( channel.isUserOn( user.getNickName() ) )
            {
                if ( message )
                {
                    channel.sendMessage( sPrefix, sCommand, sMiddle, sMessage );
                }
                channel.removeUser( user.getNickName() );
                user.removeChannel( sNameChannel );
                if ( channel.getUserNumber() != 0 )
                {
                    try
                    {
                        udateChannel( channel );
                    }
                    catch ( FatalException e )
                    {
                    }
                }
                else
                {
                    deleteChannel( sNameChannel );
                }
            }
            else
            {
                throw new IRCException( Replies.ERR_NOTONCHANNEL, new String[]{sNameChannel} );
            }
        }
    }

    /**
     * to rename a user in a specific channel and to notify the members
     * @param sOldNickName the old nickname of the user
     * @param sNewNickName the new nickname of the user
     * @param sNameChannel the name of the channel
     * @throws IRCException ERR_NOSUCHCHANNEL if the channel does not exist
     */
    public synchronized static void renameUser( String sOldNickName, String sNewNickName, String sNameChannel ) throws IRCException
    {
        if ( sOldNickName != null && !sOldNickName.equals( VOID ) && sNewNickName != null && !sNewNickName.equals( VOID ) )
        {
            Channel channel = getChannel( sNameChannel );
            if ( channel.isUserOn( sOldNickName ) )
            {
                channel.rename( sOldNickName, sNewNickName );
                channel.sendMessage( sOldNickName, new Nick().getName(), VOID, sNewNickName );
            }
        }
    }

    /**
     * to put the channel in the list
     * @param channel the channel to insert
     * @throws IRCException ERR_NONICKNAMEGIVEN if the channel has no name
     * @throws FatalException if the channel is null
     */
    public synchronized static void udateChannel( Channel channel ) throws IRCException, FatalException
    {
        if ( channel == null )
        {
            throw new FatalException( "The channel object is null !!" );
        }
        else
        {
            String sName = channel.getName();
            if ( sName == null || sName.equals( VOID ) )
            {
                throw new IRCException( Replies.ERR_NONICKNAMEGIVEN );
            }
            else
            {
                _hAllChannels.put( sName, channel );
            }
        }
    }
}
