package org.codehaus.plexus.security.simple;

import org.codehaus.plexus.security.Agent;

/**
  * 
  * <p>Created on 21/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class SimpleAgent implements Agent
{
	private String id;
	private String humanName;
	private SimpleACL acl;
	
    /**
     * 
     */
    public SimpleAgent(String id,String humanName,SimpleACL acl)
    {
        super();
        this.id = id;
        this.humanName = humanName;
        this.acl = acl;
    }

    /**
     * @see org.apache.plexus.Agent#getCredential(java.lang.String)
     */
    public Object getCredential(String key)
    {
        return null;
    }

    /**
     * @see org.apache.plexus.Agent#getCredentials()
     */
    public Object[] getCredentials()
    {
        return new Object[] {};
    }

    /**
     * @see org.apache.plexus.Agent#getHumanName()
     */
    public String getHumanName()
    {
        return humanName;
    }

    /**
     * @see org.apache.plexus.Agent#getId()
     */
    public String getId()
    {
        return id;
    }

    /**
     * @return
     */
    public SimpleACL getACL()
    {
        return acl;
    }

}
