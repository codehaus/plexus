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

import java.security.MessageDigest;

/**
 * @author AlAg
 */
public class Digester {
    
    
    // -----------------------------------------------------------------------
    public static final String digest( long value ){
        return digest( Long.toString( value ) );
    }
    // -----------------------------------------------------------------------
    public static final String digest( String value ) {

        String digest = null;
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            digest = bytesToHex( messageDigest.digest( value.getBytes() ) );    
        
        } catch( Exception e ){
            e.printStackTrace();
        }
        
        return digest;
    }
    
    
    // ===============================================================================
    /** quick array to convert byte values to hex codes */
    private final static char[] HEX = {'0','1','2','3','4','5','6','7','8', '9','a','b','c','d','e','f'};

    /**
     * This utility method is passed an array of bytes. It returns
     * this array as a String in hexadecimal format. This is used
     * internally by <code>digest()</code>. Data is returned in
     * the format specified by the Jabber protocol.
     */
    private static String bytesToHex( byte[] data ){
        StringBuffer retval = new StringBuffer();
        for(int i=0;i<data.length;i++) {
            retval.append(HEX[ (data[i]>>4)&0x0F ]);
            retval.append(HEX[ data[i]&0x0F ]);
        }
        return retval.toString();
    }
    
}

