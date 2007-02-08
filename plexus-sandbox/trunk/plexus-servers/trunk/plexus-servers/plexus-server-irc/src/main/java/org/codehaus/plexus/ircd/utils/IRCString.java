/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.       *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/

package org.codehaus.plexus.ircd.utils;

public interface IRCString
{
    static char CHANNEL_BAN_MASK_CHAR = 'b';
    static char CHANNEL_CAN_SPEAK_ON_MODERATED_CHAR = '+';
    static char CHANNEL_CLIENT_INSIDE_ONLY_CHAR = 'n';
    static char CHANNEL_INVITE_ONLY_CHAR = 'i';
    static char CHANNEL_IS_OPERATOR_CHAR = '@';
    static char CHANNEL_MODERATED_CHAR = 'm';
    static char CHANNEL_OPERATOR_CHAR = 'o';
    static char CHANNEL_PASSWORD_CHAR = 'k';
    static char CHANNEL_PRIVATE_CHAR = 'p';
    static char CHANNEL_SECRET_CHAR = 's';
    static char CHANNEL_SPEAK_ON_MODERATED_CHAR = 'v';
    static char CHANNEL_TOPIC_SETTABLE_CHAR = 't';
    static char CHANNEL_USER_LIMIT_CHAR = 'l';
    static char DOT_CHAR = '.';
    static char DOUBLE_POINTS_CHAR = ':';
    static char PLUS_CHAR = '+';
    static char MINUS_CHAR = '-';
    static char SLASH_CHAR = '/';
    static char SPACE_CHAR = ' ';
    static char STAR_CHAR = '*';
    static char USER_OPERATOR_CHAR = 'o';
    static char USER_INVISIBLE_CHAR = 'i';
    static char NULL_CHAR = '\u0000';

    static String CHANNEL_BAN_MASK = "b";
    static String CHANNEL_CAN_SPEAK_ON_MODERATED = "+";
    static String CHANNEL_CLIENT_INSIDE_ONLY = "n";
    static String CHANNEL_INVITE_ONLY = "i";
    static String CHANNEL_IS_OPERATOR = "@";
    static String CHANNEL_MODERATED = "m";
    static String CHANNEL_OPERATOR = "o";
    static String CHANNEL_PASSWORD = "k";
    static String CHANNEL_PRIVATE = "p";
    static String CHANNEL_SECRET = "s";
    static String CHANNEL_SPEAK_ON_MODERATED = "v";
    static String CHANNEL_TOPIC_SETTABLE = "t";
    static String CHANNEL_USER_LIMIT = "l";
    static String DOT = ".";
    static String DOUBLE_POINTS = ":";
    static String FALSE = "false";
    static String PLUS = "+";
    static String MINUS = "-";
    static String SLASH = "/";
    static String SPACE = " ";
    static String STAR = "*";
    static String TRUE = "true";
    static String USER_OPERATOR = "o";
    static String USER_INVISIBLE = "i";
    static String VOID = "";
}
