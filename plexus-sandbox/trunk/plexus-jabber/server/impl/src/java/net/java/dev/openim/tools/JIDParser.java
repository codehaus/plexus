/*
 * BSD License http://open-im.net/bsd-license.html
 * Copyright (c) 2003, OpenIM Project http://open-im.net
 * All rights reserved.
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the OpenIM project. For more
 * information on the OpenIM project, please see
 * http://open-im.net/
 */
package net.java.dev.openim.tools;


/**
 * @author AlAg
 * @author PV
 */
public class JIDParser {
    
    //-------------------------------------------------------------------------
    public static final String getJID( final String jidAndRes ) {
        return getName( jidAndRes )+'@'+getHostname( jidAndRes );
    }
    //-------------------------------------------------------------------------
    public static final String getHostname( final String jidAndRes ) {
        String hostname = "";
        
            int index = jidAndRes.indexOf( '@' );
            if( index > 0 ){
                hostname = jidAndRes.substring( index + 1 );
                index = hostname.indexOf( '/' );
                if( index > 0 ){
                    hostname = hostname.substring( 0, index );
                }
                hostname = hostname.toLowerCase();
            }
            else return jidAndRes;

        return hostname;
    }
    
    //-------------------------------------------------------------------------
    public static final String getNameAndRessource( final String jidAndRes ) {
        String nameAndRes = null;
        
        if( jidAndRes != null ){
            int index = jidAndRes.indexOf( '@' );
            if( index > 0 ){
                nameAndRes = jidAndRes.substring( 0, index ).toLowerCase();
                index = jidAndRes.lastIndexOf( '/' );
                if( index > 0 ){
                    nameAndRes += jidAndRes.substring( index );
                }
            }
        }
        
        return nameAndRes;
    }
    //-------------------------------------------------------------------------
    public static final String getName( final String jidAndRes ) {
        String name = jidAndRes;
    
        if( jidAndRes != null ){

            int index = jidAndRes.lastIndexOf( '/' );
            if( index > 0 ){
                name = jidAndRes.substring( 0, index );
            }

            index = name.indexOf( '@' );
            if( index > 0 ){
                name = name.substring( 0, index );
            } 
            
            if( name != null ){
                name = name.toLowerCase();
            }
        }
        
        return name;
    }

    public static boolean isSameEntity(String jid1, String jid2) {
        String name1 = JIDParser.getName(jid1);
        String name2 = JIDParser.getName(jid2);
        if (!name1.equalsIgnoreCase(name2)) return false;
        String hostname1 = JIDParser.getHostname(jid1);
        String hostname2 = JIDParser.getHostname(jid2);
        if (!hostname1.equalsIgnoreCase(hostname2)) return false;
        return true;
    }
}

