/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.utils;

public class Replies
{

    // IRC Messages
    public static final Replies RPL_TRACELINK = new Replies( 200, "Link <version & debug level> <destination> <next server>" );
    public static final Replies RPL_TRACECONNECTING = new Replies( 201, "Try. <class> <server>" );
    public static final Replies RPL_TRACEHANDSHAKE = new Replies( 202, "H.S. <class> <server>" );
    public static final Replies RPL_TRACEUNKNOWN = new Replies( 203, "???? <class> <client IP address in dot form>" );
    public static final Replies RPL_TRACEOPERATOR = new Replies( 204, "Oper <class> <nick>" );
    public static final Replies RPL_TRACEUSER = new Replies( 205, "User <class> <nick>" );
    public static final Replies RPL_TRACESERVER = new Replies( 206, "Serv <class> <int>S <int>C <server> <nick!user|*!*>@<host|server>" );
    public static final Replies RPL_TRACENEWTYPE = new Replies( 208, "<newtype> 0 <client name>" );
    public static final Replies RPL_STATSLINKINFO = new Replies( 211, "<linkname> <sendq> <sent messages> <sent bytes> <received messages> <received bytes> <time open>" );
    public static final Replies RPL_STATSCOMMANDS = new Replies( 212, "<command> <count>" );
    public static final Replies RPL_STATSCLINE = new Replies( 213, "C <host> * <name> <port> <class>" );
    public static final Replies RPL_STATSNLINE = new Replies( 214, "N <host> * <name> <port> <class>" );
    public static final Replies RPL_STATSILINE = new Replies( 215, "I <host> * <host> <port> <class>" );
    public static final Replies RPL_STATSKLINE = new Replies( 216, "K <host> * <username> <port> <class>" );
    public static final Replies RPL_STATSYLINE = new Replies( 218, "Y <class> <ping frequency> <connect frequency> <max sendq>" );
    public static final Replies RPL_ENDOFSTATS = new Replies( 219, "<stats letter> :End of /STATS report" );
    public static final Replies RPL_UMODEIS = new Replies( 221, "<user mode string>" );
    public static final Replies RPL_STATSLLINE = new Replies( 241, "L <hostmask> * <servername> <maxdepth>" );
    public static final Replies RPL_STATSUPTIME = new Replies( 242, ":Server Up %d days %d:%02d:%02d" );
    public static final Replies RPL_STATSOLINE = new Replies( 243, "O <hostmask> * <name>" );
    public static final Replies RPL_STATSHLINE = new Replies( 244, "H <hostmask> * <servername>" );
    public static final Replies RPL_LUSERCLIENT = new Replies( 251, ":There are <integer> users and <integer> invisible on <integer> servers" );
    public static final Replies RPL_LUSEROP = new Replies( 252, "<integer> :operator(s) online" );
    public static final Replies RPL_LUSERUNKNOWN = new Replies( 253, "<integer> :unknown connection(s)" );
    public static final Replies RPL_LUSERCHANNELS = new Replies( 254, "<integer> :channels formed" );
    public static final Replies RPL_LUSERME = new Replies( 255, ":I have <integer> clients and <integer> servers" );
    public static final Replies RPL_ADMINME = new Replies( 256, "<server> :Administrative info" );
    public static final Replies RPL_ADMINLOC1 = new Replies( 257, ":<admin info>" );
    public static final Replies RPL_ADMINLOC2 = new Replies( 258, ":<admin info>" );
    public static final Replies RPL_ADMINEMAIL = new Replies( 259, ":<admin info>" );
    public static final Replies RPL_TRACELOG = new Replies( 261, "File <logfile> <debug level>" );
    public static final Replies RPL_NONE = new Replies( 300, "" );
    public static final Replies RPL_AWAY = new Replies( 301, "<nick> :<away message>" );
    public static final Replies RPL_USERHOST = new Replies( 302, ":<reply>{ <reply>}" );
    public static final Replies RPL_ISON = new Replies( 303, ":<nick>{ <nick>}" );
    public static final Replies RPL_UNAWAY = new Replies( 305, ":You are no longer marked as being away" );
    public static final Replies RPL_NOWAWAY = new Replies( 306, ":You have been marked as being away" );
    public static final Replies RPL_WHOISUSER = new Replies( 311, "<nick> <user> <host> * :<real name>" );
    public static final Replies RPL_WHOISSERVER = new Replies( 312, "<nick> <server> :<server info>" );
    public static final Replies RPL_WHOISOPERATOR = new Replies( 313, "<nick> :is an IRC operator" );
    public static final Replies RPL_WHOWASUSER = new Replies( 314, "<nick> <user> <host> * :<real name>" );
    public static final Replies RPL_ENDOFWHO = new Replies( 315, "<name> :End of /WHO list" );
    public static final Replies RPL_WHOISIDLE = new Replies( 317, "<nick> <integer> :seconds idle" );
    public static final Replies RPL_ENDOFWHOIS = new Replies( 318, "<nick> :End of /WHOIS list" );
    public static final Replies RPL_WHOISCHANNELS = new Replies( 319, "<nick> :{<@|+channel> }" );
    public static final Replies RPL_LISTSTART = new Replies( 321, "Channel :Users  Name" );
    public static final Replies RPL_LIST = new Replies( 322, "<channel> <# visible> :<topic>" );
    public static final Replies RPL_LISTEND = new Replies( 323, ":End of /LIST" );
    public static final Replies RPL_CHANNELMODEIS = new Replies( 324, "<channel> <mode> <mode params>" );
    public static final Replies RPL_NOTOPIC = new Replies( 331, "<channel> :No topic is set" );
    public static final Replies RPL_TOPIC = new Replies( 332, "<channel> :<topic>" );
    public static final Replies RPL_INVITING = new Replies( 341, "<channel> <nick>" );
    public static final Replies RPL_SUMMONING = new Replies( 342, "<user> :Summoning user to IRC" );
    public static final Replies RPL_VERSION = new Replies( 351, "<version>.<debuglevel> <server> :<comments>" );
    public static final Replies RPL_WHOREPLY = new Replies( 352, "<channel> <user> <host> <server> <nick> <H|G*@|+> :<hopcount> <real name>" );
    public static final Replies RPL_NAMREPLY = new Replies( 353, "= <channel> :[<@|+nick> ]" );
    public static final Replies RPL_LINKS = new Replies( 364, "<mask> <server> :<hopcount> <server info>" );
    public static final Replies RPL_ENDOFLINKS = new Replies( 365, "<mask> :End of /LINKS list" );
    public static final Replies RPL_ENDOFNAMES = new Replies( 366, "<channel> :End of /NAMES list" );
    public static final Replies RPL_BANLIST = new Replies( 367, "<channel> <banid>" );
    public static final Replies RPL_ENDOFBANLIST = new Replies( 368, "<channel> :End of channel ban list" );
    public static final Replies RPL_ENDOFWHOWAS = new Replies( 369, "<nick> :End of WHOWAS" );
    public static final Replies RPL_INFO = new Replies( 371, ":<string>" );
    public static final Replies RPL_MOTD = new Replies( 372, ":- <text>" );
    public static final Replies RPL_ENDOFINFO = new Replies( 374, ":End of /INFO list" );
    public static final Replies RPL_MOTDSTART = new Replies( 375, ":- <server> Message of the day - " );
    public static final Replies RPL_ENDOFMOTD = new Replies( 376, ":End of /MOTD command" );
    public static final Replies RPL_YOUREOPER = new Replies( 381, ":You are now an IRC operator" );
    public static final Replies RPL_REHASHING = new Replies( 382, "<config file> :Rehashing" );
    public static final Replies RPL_TIME = new Replies( 391, "<server> :<string showing server's local time>" );
    public static final Replies RPL_USERSSTART = new Replies( 392, ":UserID   Terminal  Host" );
    public static final Replies RPL_USERS = new Replies( 393, ":%-8s %-9s %-8s" );
    public static final Replies RPL_ENDOFUSERS = new Replies( 394, ":End of users" );
    public static final Replies RPL_NOUSERS = new Replies( 395, ":Nobody logged in" );


    // IRC Errors
    public static final Replies ERR_NOSUCHNICK = new Replies( 401, "<nickname> :No such nick/channel" );
    public static final Replies ERR_NOSUCHSERVER = new Replies( 402, "<server name> :No such server" );
    public static final Replies ERR_NOSUCHCHANNEL = new Replies( 403, "<channel name> :No such channel" );
    public static final Replies ERR_CANNOTSENDTOCHAN = new Replies( 404, "<channel name> :Cannot send to channel" );
    public static final Replies ERR_TOOMANYCHANNELS = new Replies( 405, "<channel name> :You have joined too many channels" );
    public static final Replies ERR_WASNOSUCHNICK = new Replies( 406, "<nickname> :There was no such nickname" );
    public static final Replies ERR_TOOMANYTARGETS = new Replies( 407, "<target> :Duplicate recipients. No message delivered" );
    public static final Replies ERR_NOORIGIN = new Replies( 409, ":No origin specified" );
    public static final Replies ERR_NORECIPIENT = new Replies( 411, ":No recipient given (<command>)" );
    public static final Replies ERR_NOTEXTTOSEND = new Replies( 412, ":No text to send" );
    public static final Replies ERR_NOTOPLEVEL = new Replies( 413, "<mask> :No toplevel domain specified" );
    public static final Replies ERR_WILDTOPLEVEL = new Replies( 414, "<mask> :Wildcard in toplevel domain" );
    public static final Replies ERR_UNKNOWNCOMMAND = new Replies( 421, "<command> :Unknown command" );
    public static final Replies ERR_NOMOTD = new Replies( 422, ":MOTD File is missing" );
    public static final Replies ERR_NOADMININFO = new Replies( 423, "<server> :No administrative info available" );
    public static final Replies ERR_FILEERROR = new Replies( 424, ":File error doing <file op> on <file>" );
    public static final Replies ERR_NONICKNAMEGIVEN = new Replies( 431, ":No nickname given" );
    public static final Replies ERR_ERRONEUSNICKNAME = new Replies( 432, "<nick> :Erroneus nickname" );
    public static final Replies ERR_NICKNAMEINUSE = new Replies( 433, "<nick> :Nickname is already in use" );
    public static final Replies ERR_NICKCOLLISION = new Replies( 436, "<nick> :Nickname collision KILL" );
    public static final Replies ERR_USERNOTINCHANNEL = new Replies( 441, "<nick> <channel> :They aren't on that channel" );
    public static final Replies ERR_NOTONCHANNEL = new Replies( 442, "<channel> :You're not on that channel" );
    public static final Replies ERR_USERONCHANNEL = new Replies( 443, "<user> <channel> :is already on channel" );
    public static final Replies ERR_NOLOGIN = new Replies( 444, "<user> :User not logged in" );
    public static final Replies ERR_SUMMONDISABLED = new Replies( 445, ":SUMMON has been disabled" );
    public static final Replies ERR_USERSDISABLED = new Replies( 446, ":USERS has been disabled" );
    public static final Replies ERR_NOTREGISTERED = new Replies( 451, ":You have not registered" );
    public static final Replies ERR_NEEDMOREPARAMS = new Replies( 461, "<command> :Not enough parameters" );
    public static final Replies ERR_ALREADYREGISTRED = new Replies( 462, ":You may not reregister" );
    public static final Replies ERR_NOPERMFORHOST = new Replies( 463, ":Your host isn't among the privileged" );
    public static final Replies ERR_PASSWDMISMATCH = new Replies( 464, ":Password incorrect" );
    public static final Replies ERR_YOUREBANNEDCREEP = new Replies( 465, ":You are banned from this server" );
    public static final Replies ERR_KEYSET = new Replies( 467, "<channel> :Channel key already set" );
    public static final Replies ERR_CHANNELISFULL = new Replies( 471, "<channel> :Cannot join channel (+l)" );
    public static final Replies ERR_UNKNOWNMODE = new Replies( 472, "<char> :is unknown mode char to me" );
    public static final Replies ERR_INVITEONLYCHAN = new Replies( 473, "<channel> :Cannot join channel (+i)" );
    public static final Replies ERR_BANNEDFROMCHAN = new Replies( 474, "<channel> :Cannot join channel (+b)" );
    public static final Replies ERR_BADCHANNELKEY = new Replies( 475, "<channel> :Cannot join channel (+k)" );
    public static final Replies ERR_NOPRIVILEGES = new Replies( 481, ":Permission Denied- You're not an IRC operator" );
    public static final Replies ERR_CHANOPRIVSNEEDED = new Replies( 482, "<channel> :You're not channel operator" );
    public static final Replies ERR_CANTKILLSERVER = new Replies( 483, ":You cant kill a server!" );
    public static final Replies ERR_NOOPERHOST = new Replies( 491, ":No O-lines for your host" );
    public static final Replies ERR_UMODEUNKNOWNFLAG = new Replies( 501, ":Unknown MODE flag" );
    public static final Replies ERR_USERSDONTMATCH = new Replies( 502, ":Cant change mode for other users" );

    private int code;
    private String syntax;

    /**
     * @param code the replies's code
     * @param syntax the replies's syntax
     */
    private Replies( int code, String syntax )
    {
        this.code = code;
        this.syntax = syntax;
    }

    /**
     * to get the code
     */
    public int getCode()
    {
        return code;
    }

    /**
     * to get the content of the resulting message by using the given parameters. All the
     * tokens of the syntax are replaced by the value if the parameters
     * @param sParams the input parameters
     */
    public String getMessage( String[] sParams )
    {
        return Utilities.getMessage( getSyntax(), sParams );
    }

    /**
     * to get the syntax
     */
    public String getSyntax()
    {
        return syntax;
    }
}

