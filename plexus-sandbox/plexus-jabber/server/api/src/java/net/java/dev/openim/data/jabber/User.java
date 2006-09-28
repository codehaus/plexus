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
package net.java.dev.openim.data.jabber;


import java.util.List;

/*
 * @version 1.0
 *
 * @author AlAg
 */
public interface User 
{
    
    public void setName( String username );
    public String getName();
    
    public void setHostname( String hostname );
    public String getHostname();
    
    public void setPassword( String password );
    public String getPassword();
    
    public void setDigest( String digest );
    public String getDigest();
    
    public void setResource( String resource );
    public String getResource();
    
    public boolean isAuthenticationTypeSupported( int type );
    public void authenticate( String sessionId ) throws Exception;
    
    public String getJID();
    public String getNameAndRessource();
    public String getJIDAndRessource();

    public void setRosterItemList( List rosterlist );
    public List getRosterItemList();

    // should be removed when using another component container 
    // (this is a workaround due to Phoenix(?) strange behaviour
    //public User newInstance();

}


