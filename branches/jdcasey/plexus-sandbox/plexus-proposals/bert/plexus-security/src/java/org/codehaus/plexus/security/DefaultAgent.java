package org.codehaus.plexus.security;

import org.codehaus.plexus.security.credentials.acl.ACL;




/**
  * 
  * <p>Created on 16/08/2003</p>
  *
  * @author <a href="mailto:bertvanbrakel@sourceforge.net">Bert van Brakel</a>
  * @revision $Revision$
  */
public class DefaultAgent implements Agent
{
	private ACL acl;
	private String id;
	private String humanName;
	
    /**
     * 
     */
    protected DefaultAgent(String id,String humanName,ACL acl)
    {
        super();
        this.id = id;
        this.acl = acl;
        this.humanName = humanName;
    }

    /**
     * @see org.codehaus.plexus.security.Agent#getACL()
     */
    public ACL getACL()
    {
        return acl;
    }

    /**
     * @see org.codehaus.plexus.security.Agent#getId()
     */
    public String getId()
    {
        return id;
    }

    /**
     * @return
     */
    public String getHumanName()
    {
        return humanName;
    }

    /**
     * @see org.apache.plexus.Agent#getCredential(java.lang.String)
     */
    public Object getCredential(String key)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.apache.plexus.Agent#getCredentials()
     */
    public Object[] getCredentials()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
