package org.codehaus.plexus.security;


/**
  * Represents a single user/agaent/principal in the system. Components should not store 
  * a reference to this object as this object may be regenerated from time to time.
  * 
  * <p>Created on 15/08/2003</p>
  *
  * @author <a href="mailto:bertvanbrakel@sourceforge.net">Bert van Brakel</a>
  * @revision $Revision$
  */
public interface Agent
{
	/** Return the id which uniquely identifies this agent in the system */
	public String getId();
	
	/** Return the ACL for this agent */
	//public ACL getACL();
	
	/** Return a human friendly name */
	public String getHumanName();
		
	/** Return the credential with the given key */
	public Object getCredential(String key);
	
	/** Return all the agenst credentials */
	public Object[] getCredentials();	
}
