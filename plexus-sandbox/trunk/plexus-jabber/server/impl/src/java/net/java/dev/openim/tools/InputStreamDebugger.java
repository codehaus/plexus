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

import java.io.InputStream;
import java.io.IOException;
import org.apache.avalon.framework.logger.Logger;

/**
 * @author AlAg
 */
public class InputStreamDebugger extends InputStream {
    
    private InputStream m_is;
    private Logger m_logger;
    private long m_id;

    public InputStreamDebugger( InputStream is, Logger logger, long id ){
        m_is = is;
        m_logger = logger;
        m_id = id;
    }
    
    public int available() throws IOException{
        return m_is.available();
    }
    
    public void close() throws IOException{
        m_is.close();
    }
    
    public void mark(int readlimit){
        m_is.mark( readlimit );
    }
    
    public boolean markSupported(){
        return m_is.markSupported();
    }
    
    public int read() throws IOException{
        int b = m_is.read();
        m_logger.info( "Input ("+m_id+"): " + new Character( (char)b ) );
        return b;
    }
    public int read(byte[] b) throws IOException{
        int i = m_is.read( b );
        if (i != -1 ) {
           m_logger.info( "Input ("+m_id+"): " + new String( b, 0, i ) );
        } 
        return i;
    }
    
    public int read(byte[] b, int off, int len) throws IOException {
        int i = m_is.read( b, off, len );
        if ( i != -1) {
           m_logger.info( "Input ("+m_id+"): " + new String( b, off, i ) );
        } 
        
        return i;
    }
    
    public void reset() throws IOException {
        m_is.reset();
    }
    
    public long skip( long n ) throws IOException{
        return m_is.skip( n );
    }
}

