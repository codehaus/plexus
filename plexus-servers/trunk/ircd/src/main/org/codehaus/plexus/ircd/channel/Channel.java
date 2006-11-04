/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.channel;

import org.codehaus.plexus.ircd.command.Privmsg;
import org.codehaus.plexus.ircd.exception.IRCException;
import org.codehaus.plexus.ircd.properties.Config;
import org.codehaus.plexus.ircd.user.HandleUser;
import org.codehaus.plexus.ircd.user.User;
import org.codehaus.plexus.ircd.utils.IRCString;
import org.codehaus.plexus.ircd.utils.Message;
import org.codehaus.plexus.ircd.utils.Replies;
import org.codehaus.plexus.ircd.utils.Utilities;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Pattern;

public class Channel implements IRCString
{

    private static String defaultTopic;

    static
    {
        defaultTopic = Config.getValue( "channel.default.topic", VOID );
    }

    private String topic = defaultTopic;
    private String name;
    private String password;
    private ArrayList listBanMask = new ArrayList();
    private ArrayList listInvited = new ArrayList();
    private Hashtable userList = new Hashtable();
    private boolean typePrivate;
    private boolean typeSecret;
    private boolean typeIniviteOnly;
    private boolean topicSettable = true;
    private boolean typeClientInsideOnly;
    private boolean typeModerated;
    private int userLimit = Integer.MAX_VALUE;

    /**
     * @param sOperatorName operator's name
     * @param sName channel's name
     */
    public Channel( String sOperatorName, String sName )
    {
        name = sName;
        userList.put( sOperatorName, new Rights( true ) );
    }

    /**
     * to add a ban mask
     * @param sBanMask the mask to add
     */
    public void addBanMask( String sBanMask )
    {
        if ( sBanMask != null && !sBanMask.equals( VOID ) )
        {
            listBanMask.add( Utilities.replaceAll( sBanMask, STAR, DOT + STAR ) );
        }
    }

    /**
     * to add a guest
     * @param sNickName the nickname of the guest to add
     * @throws IRCException ERR_USERONCHANNEL's exception if the user is already on channel
     */
    public void addGuest( String sNickName ) throws IRCException
    {
        if ( isUserOn( sNickName ) )
        {
            throw new IRCException( Replies.ERR_USERONCHANNEL, new String[]{sNickName, getName()} );
        }
        else
        {
            listInvited.add( sNickName );
        }
    }

    /**
     * to add a user on the list
     */
    public void addUser( String sNickName )
    {
        if ( userList.get( sNickName ) == null )
        {
            userList.put( sNickName, new Rights() );
        }
    }

    /**
     * to know if the user can enter into the channel
     * @param user the user to check
     * @param sPassword the password given by the specific user
     * @throws IRCException  ERR_BANNEDFROMCHAN if the user is banned
     * ERR_BADCHANNELKEY if the password given is not correct
     * ERR_CHANNELISFULL if the user limit is already reached
     * ERR_INVITEONLYCHAN if the channel is an invite only channel and the user is not a guest
     */
    public void checkUser( User user, String sPassword ) throws IRCException
    {
        String[] sAllMasks = getBanMasks();
        if ( user != null && sAllMasks != null )
        {
            for ( int i = 0; i < sAllMasks.length; i++ )
            {
                if ( Pattern.matches( sAllMasks[i], user.getUserFullName() ) )
                {
                    throw new IRCException( Replies.ERR_BANNEDFROMCHAN, new String[]{getName()} );
                }
            }
        }
        if ( getPassword() != null && !getPassword().equals( sPassword ) )
        {
            throw new IRCException( Replies.ERR_BADCHANNELKEY, new String[]{getName()} );
        }
        if ( getUserLimit() <= getUserNumber() )
        {
            throw new IRCException( Replies.ERR_CHANNELISFULL, new String[]{getName()} );
        }
        if ( isTypeIniviteOnly() )
        {
            if ( isGuest( user ) )
            {
                removeGuest( user.getNickName() );
            }
            else
            {
                throw new IRCException( Replies.ERR_INVITEONLYCHAN, new String[]{getName()} );
            }
        }
    }

    /**
     * to know if the user is on the channel
     * @param sNickName the nickname of the user to check
     * @return true if the user is on the channel
     * @throws IRCException ERR_NOTONCHANNEL if the user is not on the channel
     */
    public boolean checkUserOn( String sNickName ) throws IRCException
    {
        return ( getRights( sNickName ) != null );
    }

    /**
     * to display all the modes of the channel
     */
    public String displayModes()
    {
        StringBuffer sb = new StringBuffer( 100 );
        sb.append( PLUS_CHAR );
        if ( isTypePrivate() )
        {
            sb.append( CHANNEL_PRIVATE_CHAR );
        }
        if ( isTypeSecret() )
        {
            sb.append( CHANNEL_SECRET_CHAR );
        }
        if ( isTypeIniviteOnly() )
        {
            sb.append( CHANNEL_INVITE_ONLY_CHAR );
        }
        if ( !isTopicSettable() )
        {
            sb.append( CHANNEL_TOPIC_SETTABLE_CHAR );
        }
        if ( isTypeClientInsideOnly() )
        {
            sb.append( CHANNEL_CLIENT_INSIDE_ONLY_CHAR );
        }
        if ( isTypeModerated() )
        {
            sb.append( CHANNEL_MODERATED_CHAR );
        }
        if ( getUserLimit() != Integer.MAX_VALUE )
        {
            sb.append( CHANNEL_USER_LIMIT + SPACE + getUserLimit() );
        }
        if ( getPassword() != null )
        {
            sb.append( CHANNEL_PASSWORD + SPACE + getPassword() );
        }
        return sb.toString();
    }

    /**
     * to get all the existing ban masks
     */
    public String[] getBanMasks()
    {
        String[] sAllMasks = new String[listBanMask.size()];
        return (String[]) listBanMask.toArray( sAllMasks );
    }

    /**
     * to get the displayed nickname
     * @param sNickName the asked nickname
     */
    private String getDisplayedValue( String sNickName )
    {
        try
        {
            Rights rights = getRights( sNickName );
            if ( rights.isOperator() )
            {
                return CHANNEL_IS_OPERATOR + sNickName;
            }
            else if ( rights.isAbleToSpeakIfModerated() )
            {
                return CHANNEL_CAN_SPEAK_ON_MODERATED + sNickName;
            }
            else
            {
                return sNickName;
            }
        }
        catch ( IRCException e )
        {
        }
        return VOID;
    }

    /**
     * to get the name of the channel
     */
    public String getName()
    {
        return name;
    }

    /**
     * to get the current password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * to get the rights on the channel of a specific user
     * @param sNickName the asked nickname
     * @return the rights if the user is on the channel
     * @throws IRCException ERR_NOTONCHANNEL if the user is not on the channel
     */
    public Rights getRights( String sNickName ) throws IRCException
    {
        if ( sNickName != null )
        {
            Rights rights = (Rights) userList.get( sNickName );
            if ( rights != null )
            {
                return rights;
            }
        }
        throw new IRCException( Replies.ERR_NOTONCHANNEL, new String[]{getName()} );
    }

    /**
     * to get the topic of the channel
     */
    public String getTopic()
    {
        return topic;
    }

    /**
     * to get the limit of user accepted in the channel
     */
    public int getUserLimit()
    {
        return userLimit;
    }

    /**
     * to get the list of users splited to be sent through IRC's messages
     */
    public String[] getUserList()
    {
        Vector vAllUsers = new Vector();
        String sCurrentUser;
        StringBuffer buffer = new StringBuffer( Message.getSizeLimit() + 20 );
        for ( Enumeration enum = userList.keys(); enum.hasMoreElements(); )
        {
            sCurrentUser = getDisplayedValue( (String) enum.nextElement() );
            if ( !sCurrentUser.equals( VOID ) )
            {
                buffer.append( sCurrentUser + SPACE );
            }
            if ( buffer.length() > Message.getSizeLimit() )
            {
                vAllUsers.addElement( buffer.toString() );
                buffer = new StringBuffer( Message.getSizeLimit() + 20 );
            }
        }
        if ( buffer.length() > 0 )
        {
            vAllUsers.addElement( buffer.toString() );
        }
        String[] sUsers = new String[vAllUsers.size()];
        return (String[]) vAllUsers.toArray( sUsers );
    }

    /**
     * to get the number of users
     */
    public int getUserNumber()
    {
        return userList.size();
    }

    /**
     * to get all the users of the channel
     */
    public User[] getUsers()
    {
        Vector vAllUser = new Vector();
        for ( Enumeration enum = userList.keys(); enum.hasMoreElements(); )
        {
            try
            {
                vAllUser.addElement( HandleUser.getUser( (String) enum.nextElement() ) );
            }
            catch ( IRCException e )
            {
            }
        }
        User[] users = new User[vAllUser.size()];
        return (User[]) vAllUser.toArray( users );
    }

    /**
     * to get the number of visible users
     */
    public int getUserVisibleNumber()
    {
        User[] users = getUsers();
        int iCount = 0;
        if ( users != null )
        {
            for ( int i = 0; i < users.length; i++ )
            {
                if ( !users[i].isInvisible() )
                {
                    iCount++;
                }
            }
        }
        return iCount;
    }

    /**
     * to know if the user is allowed to change the topic of the channel
     * @param user the user to check
     * @return true if the user can change the topic
     * @throws IRCException ERR_CHANOPRIVSNEEDED if the user is not an operator
     */
    public boolean isAllowedToChangeTopic( User user ) throws IRCException
    {
        if ( isTopicSettable() )
        {
            return true;
        }
        else
        {
            return isOperator( user );
        }
    }

    /**
     * to know if the user is allowed to speak on the channel
     * @param user the user to check
     * @return true if the user can speak
     * @throws IRCException ERR_CANNOTSENDTOCHAN if the user can not speak
     */
    public boolean isAllowedToSpeak( User user ) throws IRCException
    {
        try
        {
            Rights rights = getRights( user.getNickName() );
            if ( rights.isOperator() || !isTypeModerated() || rights.isAbleToSpeakIfModerated() )
            {
                return true;
            }
        }
        catch ( IRCException e )
        {
            if ( !isTypeClientInsideOnly() )
            {
                return true;
            }
        }
        throw new IRCException( Replies.ERR_CANNOTSENDTOCHAN, new String[]{getName()} );
    }

    /**
     * to know if a specific user is a guest of the channel
     * @param user the user to check
     * @return true if the user is in the list false otherwise
     */
    public boolean isGuest( User user )
    {
        if ( user != null )
        {
            return listInvited.indexOf( user.getNickName() ) != -1;
        }
        return false;
    }

    /**
     * to know if the user is an operator or not
     * @param user the user to check
     * @return true if the user is an operator
     * @throws IRCException ERR_CHANOPRIVSNEEDED if the user is not an operator
     */
    public boolean isOperator( User user ) throws IRCException
    {
        Rights rights = getRights( user.getNickName() );
        if ( rights.isOperator() )
        {
            return true;
        }
        else
        {
            throw new IRCException( Replies.ERR_CHANOPRIVSNEEDED, new String[]{getName()} );
        }
    }

    /**
     * to know if it's on topic settable mode or not
     */
    public boolean isTopicSettable()
    {
        return topicSettable;
    }

    /**
     * to know if it's on client inside only mode or not
     */
    public boolean isTypeClientInsideOnly()
    {
        return typeClientInsideOnly;
    }

    /**
     * to know if it's on invite only mode or not
     */
    public boolean isTypeIniviteOnly()
    {
        return typeIniviteOnly;
    }

    /**
     * to know if it's on moderated mode or not
     */
    public boolean isTypeModerated()
    {
        return typeModerated;
    }

    /**
     * to know if it's on private mode or not
     */
    public boolean isTypePrivate()
    {
        return typePrivate;
    }

    /**
     * to know if it's on secret mode or not
     */
    public boolean isTypeSecret()
    {
        return typeSecret;
    }

    /**
     * to know if the user is on the channel
     * @param sNickName the nickname of the user to check
     * @return true if the user is on the channel false otherwise
     */
    public boolean isUserOn( String sNickName )
    {
        try
        {
            return ( getRights( sNickName ) != null );
        }
        catch ( IRCException e )
        {
        }
        return false;
    }

    /**
     * to remove a ban mask
     * @param sBanMask the ban mask to remove
     */
    public void removeBanMask( String sBanMask )
    {
        if ( sBanMask != null && !sBanMask.equals( VOID ) )
        {
            listBanMask.remove( Utilities.replaceAll( sBanMask, STAR, DOT + STAR ) );
        }
    }

    /**
     * to remove a guest from the list
     * @param sNickName the nickname of the guest to remove
     */
    public void removeGuest( String sNickName )
    {
        listInvited.remove( sNickName );
    }

    /**
     * to remove the password
     * @param sPwd the last password of the channel
     */
    public void removePassword( String sPwd )
    {
        if ( getPassword() != null && getPassword().equals( sPwd ) )
        {
            setPassword( null );
        }
    }

    /**
     * to remove a user from the list
     */
    public void removeUser( String sNickName )
    {
        if ( userList.get( sNickName ) != null )
        {
            userList.remove( sNickName );
        }
    }

    /**
     * to remove the user limit
     */
    public void removeUserLimit()
    {
        setUserLimit( Integer.MAX_VALUE );
    }

    /**
     * to rename a user in the list of users
     * @param sOldNickName the old nickname
     * @param sNewNickName the new nickname
     */
    public void rename( String sOldNickName, String sNewNickName )
    {
        if ( sNewNickName != null && !sNewNickName.equals( VOID ) && sOldNickName != null && !sOldNickName.equals( VOID ) )
        {
            Object object = userList.get( sOldNickName );
            if ( object != null )
            {
                userList.put( sNewNickName, object );
                userList.remove( sOldNickName );
            }
        }
    }

    /**
     * to send a private message
     * @param sNickName the nickname of the user who sends the message
     * @param sMessage message's trailing
     * @throws IRCException ERR_NOTEXTTOSEND if the message's trailing is empty
     */
    public void sendMessage( String sNickName, String sMessage ) throws IRCException
    {
        sendMessage( sNickName, sNickName, new Privmsg().getName(), getName(), sMessage );
    }

    /**
     * to send a message to all the users of the channel
     * @param sPrefix message's prefix
     * @param sCommand message's command
     * @param sMiddle message's middle
     * @param sMessage message's trailing
     * @throws IRCException ERR_NOTEXTTOSEND if the message's trailing is empty
     */
    public void sendMessage( String sPrefix, String sCommand, String sMiddle, String sMessage ) throws IRCException
    {
        sendMessage( null, sPrefix, sCommand, sMiddle, sMessage );
    }

    /**
     * to send a message to all the users of the channel
     * @param sNickName the nickname of the user who sends the message
     * @param sPrefix message's prefix
     * @param sCommand message's command
     * @param sMiddle message's middle
     * @param sMessage message's trailing
     * @throws IRCException ERR_NOTEXTTOSEND if the message's trailing is empty
     */
    public void sendMessage( String sNickName, String sPrefix, String sCommand, String sMiddle, String sMessage ) throws IRCException
    {
        User[] users = getUsers();
        if ( users == null )
        {
            return;
        }
        if ( sMessage == null || sMessage.equals( VOID ) )
        {
            throw new IRCException( Replies.ERR_NOTEXTTOSEND, new String[]{} );
        }
        for ( int i = 0; i < users.length; i++ )
        {
            User user = users[i];
            if ( ( sNickName == null || sNickName.equals( VOID ) || !sNickName.equals( user.getNickName() ) ) &&
                sPrefix != null && sCommand != null && sMiddle != null )
            {
                user.addResponses( Message.createSingleMessage( new Message( sPrefix, VOID, sCommand, sMiddle, sMessage ) ) );
            }
        }
    }

    /**
     * to permit or not a user to speak if the channel is on a moderated mode
     * @param sNickName the nickname of the user
     * @param bCanSpeakModeratedChannel permit or not
     * @throws IRCException ERR_NOTONCHANNEL if the user is not on the channel
     */
    public void setCanSpeakModeratedChannel( String sNickName, boolean bCanSpeakModeratedChannel ) throws IRCException
    {
        Rights rights = getRights( sNickName );
        rights.setAbleToSpeakIfModerated( bCanSpeakModeratedChannel );
        userList.put( sNickName, rights );
    }

    /**
     * to set the operator's status to a specific user
     * @param sNickName the nickname of the user
     * @param isOperator give or not the status
     * @throws IRCException ERR_NOTONCHANNEL if the user is not on the channel
     */
    public void setIsOperator( String sNickName, boolean isOperator ) throws IRCException
    {
        Rights rights = getRights( sNickName );
        rights.setOperator( isOperator );
        userList.put( sNickName, rights );
    }

    /**
     * to set the name of the channel
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /**
     * to set the password
     */
    public void setPassword( String password )
    {
        this.password = password;
    }

    /**
     * to set the topic of the channel
     */
    public void setTopic( String topic )
    {
        this.topic = topic;
    }

    /**
     * to set the topic settable mode known as 't'
     */
    public void setTopicSettable( boolean topicSettable )
    {
        this.topicSettable = topicSettable;
    }

    /**
     * to set the "client inside only"'s mode known as 'n'
     */
    public void setTypeClientInsideOnly( boolean isClientInsideOnly )
    {
        this.typeClientInsideOnly = isClientInsideOnly;
    }

    /**
     * to set the "invite only"'s mode known as 'i'
     */
    public void setTypeIniviteOnly( boolean typeIniviteOnly )
    {
        this.typeIniviteOnly = typeIniviteOnly;
    }

    /**
     * to set the modarated's mode known as 'm'
     */
    public void setTypeModerated( boolean typeModerated )
    {
        this.typeModerated = typeModerated;
    }

    /**
     * to set the private's mode known as 'p'
     */
    public void setTypePrivate( boolean typePrivate )
    {
        this.typePrivate = typePrivate;
    }

    /**
     * to set the secret's mode known as 's'
     */
    public void setTypeSecret( boolean typeSecret )
    {
        this.typeSecret = typeSecret;
    }

    /**
     * to set the limit of users known as 'l'
     * @param userLimit the limit of users accepted in the channel
     */
    public void setUserLimit( int userLimit )
    {
        this.userLimit = userLimit;
    }
}
