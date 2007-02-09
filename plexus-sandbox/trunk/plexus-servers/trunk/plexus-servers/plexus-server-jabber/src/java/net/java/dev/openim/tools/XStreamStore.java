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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.avalon.framework.logger.Logger;
import org.apache.commons.lang.StringUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;





public class XStreamStore 
{
    public static final String DEFAULT_ENCODING = "UTF-8";
    
    private File m_file;
    private XStream m_xstream;
    private Logger m_logger;
    private Map m_map;
    
    private String m_substituteFrom;
    private String m_substituteTo;
    private String m_xmlProlog; 
 
    public XStreamStore( File file, Logger logger) {
       this(file,logger,DEFAULT_ENCODING);
    }
    
    public XStreamStore( File file, Logger logger,String encoding ){
        m_file = file;
        m_logger = logger;
        m_xstream = new XStream(new DomDriver());
        String enc = encoding != null ? encoding : DEFAULT_ENCODING;
        m_xmlProlog = "<?xml version='1.0' encoding='"+enc+"'?>";
    }
    
    
    public void load(){
        m_map = loadMap();
    }
    //  --------------------------------------------------------------------------    
    public void alias( String name, Class classz ){
        m_xstream.alias( name, classz );
    }
    //  --------------------------------------------------------------------------    
    public void substitute( String from, String to ){
        m_substituteFrom = from;
        m_substituteTo= to;
    }
    
    //  --------------------------------------------------------------------------
    public Object get( Object key ){
        Object value = null;
        synchronized( m_map ){
            value = m_map.get( key );
        }
        return value;
    }
    
    //  --------------------------------------------------------------------------
    public Object remove( Object key ){
        Object value = null;
        synchronized( m_map ){
            value = m_map.remove( key );
            saveMap( m_map );
        }
        return value;
    }
    
    //  --------------------------------------------------------------------------
    public void put( Object key, Object value ){
        synchronized( m_map ){
            value = m_map.put( key, value );
            saveMap( m_map );
        }
    }
    
    
    
    //  --------------------------------------------------------------------------    
    private Logger getLogger(){
        return m_logger;
    }
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    private void saveMap( Map map ){
        String xstreamData = m_xstream.toXML( map );
        if( m_substituteFrom != null && m_substituteTo != null ){
            xstreamData = StringUtils.replace(  xstreamData, m_substituteFrom, m_substituteTo );
        }
        xstreamData = m_xmlProlog+"\n"+xstreamData;
        //getLogger().info("saving roster " + xstreamData);
        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream( m_file );
            fos.write( xstreamData.getBytes() );
        }
        catch( IOException e ){
            getLogger().error( e.getMessage(), e );
        }
        finally{
            if( fos != null ){
                try{
                    fos.close();
                }
                catch( IOException e ){
                    getLogger().error( e.getMessage() );
                }
            }
        }
        
    }
    //--------------------------------------------------------------------------
    private Map loadMap(){
        Map map = null;
        
        if( m_file.exists() ){
            try{
                FileInputStream fis = new FileInputStream( m_file );
                String xmlData = IOUtils.toString( fis );
                fis.close();
                if( m_substituteFrom != null && m_substituteTo != null ){
                    xmlData = StringUtils.replace(  xmlData, m_substituteTo, m_substituteFrom );
                }
                map = (Map)m_xstream.fromXML( xmlData );
            }
            catch( Exception e ){
                getLogger().error( e.getMessage(), e );
            }
            
        }
        else{
            getLogger().info( "No " + m_file + " => starting with void store" );
            map = new HashMap();
        }
        
        return map;
    }    
    
    
}

