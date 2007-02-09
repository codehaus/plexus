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
package net.java.dev.openim.session;



import java.net.Socket;

import java.io.*;

import org.apache.avalon.framework.logger.AbstractLogEnabled;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;
import org.xmlpull.v1.XmlPullParserException;

import net.java.dev.openim.IMRouter;
import net.java.dev.openim.data.Transitable;
import net.java.dev.openim.jabber.Streams;
import ctu.tools.xml.XMLUtils;
//import net.java.dev.openim.tools.InputStreamDebugger;

public abstract class AbstractIMSession extends AbstractLogEnabled 
   implements IMSession
{
    
    protected String        m_defaultEncoding;
    
    protected OutputStreamWriter m_outputStreamWriter;
    private XmlPullParser   m_xpp;
    protected Socket        m_socket;    
    private String          m_encoding;
    private XmlPullParserFactory m_factory;

    
    protected IMRouter      m_router;

    protected volatile Boolean      m_disposed;
    
    
    protected long          m_sessionId;
    protected static Long   m_lastSessionId = new Long( System.currentTimeMillis() );
    
   protected Streams m_streams;
    
    //-------------------------------------------------------------------------
    public boolean isClosed(){
        boolean value = false;
        if( m_disposed != null ){
            synchronized( m_disposed ){
                value = m_disposed.booleanValue();
            }
        }
        return value;
    }

    /**
     * @return An instance of the XmlPullParserFactory class or null when the session is not set up.
     */
    public XmlPullParserFactory getXMLParserFactory() {
          return m_factory;
    }

    //-------------------------------------------------------------------------
    public void setup( final Socket socket ) throws Exception {
        m_socket = socket;

        m_factory = XmlPullParserFactory.newInstance();
        m_factory.setNamespaceAware( true );
        m_xpp = m_factory.newPullParser();
        
        // to be checked -- getInputEncoding should detect encoding (if parser impl do so)
        /*m_encoding = m_xpp.getInputEncoding();
        if( m_encoding == null || m_encoding.length() == 0 ){
            m_encoding = m_defaultEncoding;
        }
         */
        
        //DataInputStream is = new DataInputStream( new net.java.dev.openim.tools.InputStreamDebugger( socket.getInputStream(), getLogger() ) );
        DataInputStream is = new DataInputStream(socket.getInputStream() );

        InputStreamReader inputStreamReader = new InputStreamReader( is, m_defaultEncoding );
        //InputStreamReader inputStreamReader = new InputStreamReader( new net.java.dev.openim.tools.InputStreamDebugger( is, getLogger(), m_sessionId ) , m_defaultEncoding );
        
        m_xpp.setInput( inputStreamReader );
        
        
        DataOutputStream os = new DataOutputStream( socket.getOutputStream() );
        m_outputStreamWriter = new OutputStreamWriter( os, m_defaultEncoding );
        
        getLogger().debug( "Starting session: " +m_sessionId + " with encoding " + m_encoding );
        
    }
        
    //-------------------------------------------------------------------------
    public final XmlPullParser getXmlPullParser(){
        return m_xpp;
    }
    
    //-------------------------------------------------------------------------
    /*
    public final OutputStream getOutputStream() {
        return m_outputStream;
    }    
    */
    
    
    //-------------------------------------------------------------------------
    public final long getId() {
       return m_sessionId;
    }    

    
    //-------------------------------------------------------------------------
    
    public final String getEncoding() {
        return m_defaultEncoding;
    }
    
    //-------------------------------------------------------------------------
    public final void writeOutputStream(final String s) throws IOException {
        getLogger().debug( "Output ("+m_sessionId+"/"+getConnectionType()+"): "+ s );
        if( s != null && m_outputStreamWriter != null ){
            if( !m_socket.isClosed() && m_socket.isConnected() ){
                synchronized( m_outputStreamWriter ){
                    m_outputStreamWriter.write( s );
                    if ( m_streams instanceof net.java.dev.openim.jabber.FlashStreams) {
                       m_outputStreamWriter.write("\0");
                    } // end of if ()
                    
                    m_outputStreamWriter.flush();
                }
            }
            else{
                throw new IOException( "Output socket closed or not connected" );
            }
        }
    }

    public final void writeOutputStream(Transitable packet) throws IOException {
        writeOutputStream(packet.toString(getEncoding()));
    }

    //-------------------------------------------------------------------------
    public final IMRouter getRouter() {
        return m_router;
    }
    //-------------------------------------------------------------------------
    public final void setRouter( IMRouter router ) {
        m_router = router;
    }
    
   public void setStreams(Streams streams) {
      m_streams = streams;
   }


   public Streams getStreams() {
      return m_streams;
   }
    //-------------------------------------------------------------------------
    public final String toString() {
        return "I: "+ getId();
    }
    
    //-------------------------------------------------------------------------
    public final int hashCode(){
        Long sessionL = new Long( m_sessionId );
        return sessionL.hashCode();
    }
    //-------------------------------------------------------------------------
    // implementer to make PMD happy ,)
    public boolean equals( final Object obj ){
        boolean result = false;
        if( obj instanceof IMSession ){
            result = obj == this;
        }
        return result;
    }

    /**
     * Serializes the current XML node.
     *
     * @param out Output character stream.
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    public void roundTripNode(Writer out) throws XmlPullParserException, java.io.IOException
    {
        XmlSerializer serializer = getXMLParserFactory().newSerializer();
        serializer.setOutput(out);

        XMLUtils.serialize(getXmlPullParser(), serializer, true);
    }
}

